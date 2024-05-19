package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.IAnalysisIntervalActionDao;
import com.qianyishen.dao.IBlackIpActionDao;
import com.qianyishen.dao.ICrawlerRawDataDao;
import com.qianyishen.dao.IPoliciesProcessActionDao;
import com.qianyishen.domain.*;
import com.qianyishen.domain.Process;
import com.qianyishen.service.IProcessFilterIpIntervalService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据process策略的IpInterval规则筛选出嫌疑ip
 */
@Service("processFilterIpIntervalService")
public class ProcessFilterIpIntervalServiceImpl implements IProcessFilterIpIntervalService {

    //用于获取【process策略中ipInterval规则分析】的原始数据
    @Resource(name = "crawlerRawDataDao")
    private ICrawlerRawDataDao crawlerRawDataDao;

    //用于获取已存在的ip黑名单
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    //用于获取【获取原始数据的时间间隔：timeInterval】（作用：使系统获取now()-timeInterval ~ now() 之内的数据）
    @Resource(name = "analysisIntervalActionDao")
    private IAnalysisIntervalActionDao analysisIntervalActionDao;

    //需要使用CrawlerFilteringOperation实体类来存储【已存在的ip黑名单】【获取原始数据的时间间隔】【当前时间】
    @Resource(name = "crawlerFilteringOperation")
    private CrawlerFilteringOperation crawlerFilteringOperation;

    ///引入 policiesProcessActionDao 用于查询正在启用的process策略
    @Resource(name = "policiesProcessActionDao")
    private IPoliciesProcessActionDao policiesProcessActionDao;

    //用于获取【多例状态】下的ProcessIpThreshold实体类对象，以存储嫌疑ip及其对应的阈值
    @Autowired
    private BeanFactory beanFactory;

    /**
     * 获取用于【process策略中ipInterval规则分析】的原始数据（第一部分）（查询在x分钟内，有哪些ip发送了请求）
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
    public List<String> findProIpIntervalRawdataWithoutBlackIpFirst(Timestamp timestamp) {
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
        //返回除ip黑名单之外，且在“获取原始数据时间间隔”之内的所有ip
        return crawlerRawDataDao.findAllProIpIntervalRawdataFirst(crawlerFilteringOperation);
    }

    /**
     * 获取用于【process策略中ipInterval规则分析】的原始数据（第二部分）
     * 查询ip查询其在x分钟内的所有访问时间，以便计算其平均访问间隔
     * @param ip ip地址（该ip不为嫌疑ip）
     * @return 该ip在x分钟内，所有访问时间的集合
     */
    @Override
    public List<Timestamp> findProIpIntervalRawdataWithoutBlackIpSecond(String ip) {
        //查询对应ip的所有访问时间
        return crawlerRawDataDao.findAllProIpIntervalRawdataSecond(ip);
    }

    /**
     * 根据process策略中的ipInterval规则分析ip原始数据，从而判断出嫌疑ip
     * 步骤：
     *      1. 查询在x分钟内，有哪些ip发送了请求
     *      2. 获取正在启用的process策略
     *      3. 若原始数据且启用的process策略不为null，则执行以下代码
     *          1. 获取正在启用的process策略中的ipInterval规则（即ip访问的最小时间间隔，小于这个值就会被判定为嫌疑ip）
     *          2. 获取正在启用的process策略中的ipInterval规则所设定的阈值
     *          3. 遍历访问服务器的ip
     *              1. 查询该ip在x分钟内的所有访问时间
     *              2. 遍历访问时间，并计算、累加访问时间间隔（最多遍历20次，因为一般根据20次的访问时间就可以判断该ip是否为嫌疑ip了）
     *          4. 计算出该ip的平均访问时间间隔
     *          5. 若 该ip的平均访问时间间隔 < ip访问的最小时间间隔，则判定该ip为嫌疑ip
     *          6. 判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
     *              1. 若为null，则直接加入该嫌疑ip
     *              2. 若不为null，则遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在于ProcessIpThreshold中
     *                  1. 若已经存在，则将阈值叠加
     *                  2. 若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
     *       4. 返回嫌疑ip名单
     * @param processIpThresholdList 封装有，所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     * @param timestamp 封装当前时间
     * @return 返回 封装有所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     */
    @Override
    public ProcessIpThresholdList analyzeCrawlersByProIpInterval(ProcessIpThresholdList processIpThresholdList, Timestamp timestamp) {
        //查询在x分钟内，有哪些ip发送了请求
        List<String> ipList = findProIpIntervalRawdataWithoutBlackIpFirst(timestamp);
        //获取正在启用的process策略
        Process process = policiesProcessActionDao.findEnableProcess().get(0);
        if (ipList != null && ipList.size() > 0 && process != null){
            //获取正在启用的process策略中的ipInterval规则（即ip访问的最小时间间隔，小于这个值就会被判定为嫌疑ip）
            Integer proIpInterval = process.getProIpInterval();
            //获取正在启用的process策略中的ipInterval规则所设定的阈值
            Integer proIpIntervalThreshold = process.getProIpIntervalThreshold();
            //遍历访问服务器的ip
            for (String ip : ipList) {
                //用于记录该ip在x分钟内，访问时间间隔的总和（用于计算平均访问时间间隔）
                int sumInterval = 0;
                //用于记录本次计算了多少个时间间隔
                int num = 0;
                //用于存储该ip的平均访问时间间隔（默认为int数据类型的最大值，即不会小于设定的ip访问最小时间间隔）
                int averageInterval = Integer.MAX_VALUE;
                //查询该ip在x分钟内的所有访问时间
                List<Timestamp> ipTimeList = findProIpIntervalRawdataWithoutBlackIpSecond(ip);
                //若ipTimeList.size() 不大于1，说明该ip只访问了一次，自然就不会判断该ip为嫌疑ip了（此时该ip的访问时间间隔默认为int数据类型的最大值，即一定不会小于设定的ip访问最小时间间隔）
                if (ipTimeList.size() > 1){
                    //遍历访问时间（最多遍历20次，因为一般根据20次的访问时间就可以判断该ip是否为嫌疑ip了）
                    for (int i=1; i<ipTimeList.size() && i<20; i++){
                        //累加访问时间间隔（单位：秒）
                        sumInterval = sumInterval + (int) (ipTimeList.get(i).getTime() - ipTimeList.get(i-1).getTime())/1000;
                        //用于计算的时间间隔个数
                        num = num + 1;
                    }
                    //计算该ip的平均访问时间间隔
                    averageInterval = sumInterval/num;
                }
                //若 该ip的平均访问时间间隔 < ip访问的最小时间间隔，则判定该ip为嫌疑ip
                if (averageInterval < proIpInterval){
                    boolean flag = false;
                    //判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
                    if (processIpThresholdList.getProcessIpThreshold() == null){
                        //若为空则直接加入该嫌疑ip
                        //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                        ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                        processIpThreshold.setSuspectIp(ip);
                        processIpThreshold.setSuspectIpThreshold(proIpIntervalThreshold);
                        ArrayList<ProcessIpThreshold> processIpThresholds = new ArrayList<>();
                        processIpThresholds.add(processIpThreshold);
                        processIpThresholdList.setProcessIpThreshold(processIpThresholds);
                    }else {
                        //遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在
                        for (ProcessIpThreshold ipThreshold : processIpThresholdList.getProcessIpThreshold()) {
                            if (ipThreshold.getSuspectIp().equals(ip)){
                                //若已经存在，则将阈值叠加
                                ipThreshold.setSuspectIpThreshold(ipThreshold.getSuspectIpThreshold() + proIpIntervalThreshold);
                                flag = true;
                            }
                        }
                        //若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
                        if (!flag){
                            //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                            ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                            processIpThreshold.setSuspectIp(ip);
                            processIpThreshold.setSuspectIpThreshold(proIpIntervalThreshold);
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
