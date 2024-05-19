package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.*;
import com.qianyishen.domain.*;
import com.qianyishen.domain.Process;
import com.qianyishen.service.IProcessFilterIpSumService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据process策略的ipSum规则筛选出嫌疑ip
 */
@Service("processFilterIpSumService")
public class ProcessFilterIpSumServiceImpl implements IProcessFilterIpSumService {

    //引入 policiesProcessActionDao 用于查询正在启用的process策略
    @Resource(name = "policiesProcessActionDao")
    private IPoliciesProcessActionDao policiesProcessActionDao;

    //用于获取【process策略中ipSum规则分析】的原始数据
    @Resource(name = "crawlerRawDataDao")
    private ICrawlerRawDataDao crawlerRawDataDao;

    //用于获取【获取原始数据的时间间隔：timeInterval】（作用：使系统获取now()-timeInterval ~ now() 之内的数据）
    @Resource(name = "analysisIntervalActionDao")
    private IAnalysisIntervalActionDao analysisIntervalActionDao;

    //用于获取已存在的ip黑名单
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    //需要使用CrawlerFilteringOperation实体类来存储【已存在的ip黑名单】【获取原始数据的时间间隔】【当前时间】
    @Resource(name = "crawlerFilteringOperation")
    private CrawlerFilteringOperation crawlerFilteringOperation;

    //用于获取【多例状态】下的ProcessIpThreshold实体类对象，以存储嫌疑ip及其对应的阈值
    @Autowired
    private BeanFactory beanFactory;

    /**
     * 获取用于【process策略中ipSum规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     *  作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @param timestamp 设置当前时间
     *        这里你会发现，相比于 RefererFilterServiceImpl 和 UserAgentFilterServiceImpl 类的原始数据获取方法多了一个 timestamp 参数
     *        原因：相比于RefererFilter和UserAgentFilter策略分析，process策略分析更为复杂，因此我采用以下方法实现
     *             1. 先创建一个ProcessFilterMainService类，为总调度
     *             2. 而process策略中共5个规则：ipSum、ipKeySum、pro_ipInterval、pro_ipKeyinterval、pro_ipUseragent
     *             3. 分别使用五个类来实现，这5个类中都存在一个分析方法，用于根据本类的规则筛选出嫌疑ip
     *             4. process总调度会依次调用这5个类中的分析方法，最终分析出爬虫ip
     *             5. 此时若仍采用 RefererFilter和UserAgentFilter策略分析 中使用的原始数据采集思路是行不通的
     *             6. 之前的这个思路是将时间写死进了sql语句中（在sql中使用now()函数），而process总调度在分别调用5个类中的分析方法时，由于
     *             7. 处理原始数据需要时间，就会产生分析方法的【调用时间差】，这样的话就会导致，process策略的五个规则处理的【不是相同时间段内】的原始数据
     *             8. 为了解决这个问题，我们就不能将时间写死进sql语句中，而是为5个类中的分析方法提供一个统一的时间，所以这里的原始数据获取方法多了一个 timestamp 参数
     * @return 原始数据的list集合
     */
    @Override
    public List<ProcessFilterIpSum> findProIpSumRawdataWithoutBlackIp(Timestamp timestamp) {
        //查询 获取原始数据的时间间隔（即：now()-timeInterval ~ now() 之内的数据）
        List<AnalysisInterval> timeInterval = analysisIntervalActionDao.findTimeInterval();
        //查询已存在的ip黑名单
        List<BlackIp> allBlackIp = blackIpActionDao.findBlackIp();
        //将该ip黑名单存入CrawlerFilteringOperation实体类的BlackIpList属性中
        crawlerFilteringOperation.setBlackIpList(allBlackIp);
        //将【获取原始数据的时间间隔】存入CrawlerFilteringOperation实体类的analysisInterval属性中（即：查询 now()-analysisInterval ~ now() 之内的原始数据，单位：秒）
        crawlerFilteringOperation.setAnalysisInterval(timeInterval.get(0).getIntervalTime());
        //设置当前时间
        crawlerFilteringOperation.setNowDate(timestamp.toString());
        //返回除ip黑名单之外，且在“获取原始数据时间间隔”之内的所有ip的原始数据
        return crawlerRawDataDao.findAllProIpSumRawdata(crawlerFilteringOperation);
    }

    /**
     * 根据process策略中的ipSum规则分析ip原始数据，从而判断出嫌疑ip
     * 步骤：
     *      1. 先获取【用于process策略中ipsum规则分析】的ip原始数据
     *      2. 获取正在启用的process策略
     *      3. 若原始数据且启用的process策略不为null，则执行以下代码
     *          1. 获取正在启用的process策略的ipsum规则（即获取规定的ip最大访问次数）
     *          2. 获取ipSum的阈值
     *          3. 遍历rawDataList原始数据，让原始数据中ip的访问次数与规定的最大访问次数进行比较，
     *              1. 若违反最大访问次数，则将该ip置为嫌疑ip，
     *              2. 判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
     *                  1. 若为null，则直接加入该嫌疑ip
     *                  2. 若不为null，则遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在于ProcessIpThreshold中
     *                      1. 若已经存在，则将阈值叠加
     *                      2. 若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
     * @param processIpThresholdList 封装有，所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     * @param timestamp 封装当前时间
     * @return 返回 封装有所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     */
    @Override
    public ProcessIpThresholdList analyzeCrawlersByProIpSum(ProcessIpThresholdList processIpThresholdList, Timestamp timestamp){
        //获取用于process策略的ipSum规则分析的原始ip数据
        List<ProcessFilterIpSum> rawDataList = findProIpSumRawdataWithoutBlackIp(timestamp);
        //获取正在启用的process策略
        Process enableProcess = policiesProcessActionDao.findEnableProcess().get(0);
        if (rawDataList != null && rawDataList.size() > 0 && enableProcess != null){
            //获取正在启用的process策略中的ipSum规则（即获取ip最大的访问次数）
            Integer proIpSum = enableProcess.getProIpSum();
            //获取正在启用的process策略中的ipSum规则所设定的阈值
            Integer proIpSumThreshold = enableProcess.getProIpSumThreshold();
            //将从原始数据获取的每个ip的访问次数与ipSum规则依次比较，若违反规则，则将该ip列为嫌疑ip，并将对应的阈值进行累加
            for (ProcessFilterIpSum rawData : rawDataList) {
                //判断是否超出最大ip访问次数，若超出，将该ip置为嫌疑ip。
                if (rawData.getCount() > proIpSum){
                    boolean flag = false;
                    //判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
                    if (processIpThresholdList.getProcessIpThreshold() == null){
                        //若为空则直接加入该嫌疑ip
                        //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                        ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                        processIpThreshold.setSuspectIp(rawData.getIp());
                        processIpThreshold.setSuspectIpThreshold(proIpSumThreshold);
                        ArrayList<ProcessIpThreshold> processIpThresholds = new ArrayList<>();
                        processIpThresholds.add(processIpThreshold);
                        processIpThresholdList.setProcessIpThreshold(processIpThresholds);
                    }else {
                        //若为不为空，则遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在
                        for (ProcessIpThreshold ipThreshold : processIpThresholdList.getProcessIpThreshold()) {
                            if (ipThreshold.getSuspectIp().equals(rawData.getIp())){
                                //若已经存在，则将阈值叠加
                                ipThreshold.setSuspectIpThreshold(ipThreshold.getSuspectIpThreshold() + proIpSumThreshold);
                                flag = true;
                            }
                        }
                        //若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
                        if (!flag){
                            //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                            ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                            processIpThreshold.setSuspectIp(rawData.getIp());
                            processIpThreshold.setSuspectIpThreshold(proIpSumThreshold);
                            processIpThresholdList.getProcessIpThreshold().add(processIpThreshold);
                        }
                    }
                }
            }
        }
        //返回嫌疑ip名单
        return processIpThresholdList;
    }
}
