package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.*;
import com.qianyishen.domain.*;
import com.qianyishen.domain.Process;
import com.qianyishen.service.IProcessFilterIpKeySumService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 根据process策略的ipKeySum规则筛选出嫌疑ip
 */
@Service("processFilterIpKeySumService")
public class ProcessFilterIpKeySumServiceImpl implements IProcessFilterIpKeySumService {

    //用于获取【process策略中ipKeySum规则分析】的原始数据
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

    //引入 policiesProcessActionDao 用于查询正在启用的process策略
    @Resource(name = "policiesProcessActionDao")
    private IPoliciesProcessActionDao policiesProcessActionDao;

    //用于获取关键页面的正则
    @Resource(name = "keyPagesActionDao")
    private IPoliciesKeyPagesActionDao keyPagesActionDao;

    //用于获取【多例状态】下的ProcessIpThreshold实体类对象，以存储嫌疑ip及其对应的阈值
    @Autowired
    private BeanFactory beanFactory;

    /**
     * 获取用于【process策略中ipKeySum规则分析】的原始数据
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
    public List<ProcessFilterIpKeySum> findProIpKeySumRawdataWithoutBlackIp(Timestamp timestamp) {
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
        return crawlerRawDataDao.findAllProIpKeySumRawdata(crawlerFilteringOperation);
    }

    /**
     * 根据process策略中的ipKeySum规则分析ip原始数据，从而判断出嫌疑ip
     * 步骤：
     *      1. 先获取【用于process策略中ipKeySum规则分析】的ip原始数据
     *      2. 获取正在启用的process策略
     *      3. 获取关键页面的匹配正则
     *      3. 若【原始数据】、【启用的process策略】且【关键页面正则】不为null，则执行以下代码
     *          1. 获取正在启用的process策略的ipKeySum规则（即获取ip访问关键页面的最大次数）
     *          2. 获取正在启用的process策略中的ipKeySum规则所设定的阈值
     *          3. 创建一个map集合，用于存储ip及其访问关键页面的次数
     *          4. 遍历rawDataList原始数据，让原始数据中ip的uri与关键页面正则依次进行匹配
     *              1. 若匹配成功，说明该ip访问了关键页面，需要将该ip添加进map集合中
     *              2. 遍历map集合，看该ip是否已经存在
     *              3. 若存在，则将该ip对关键页面的访问次数+1；若不存在，则直接加入该ip，并将该ip对关键页面的访问次数置为1
     *          5. 然后遍历map集合，结合process策略的ipKeySum规则，判断map集合中访问过关键页面的ip是否为嫌疑ip
     *              1. 若该ip对关键页面的访问次数 > 规定的最大次数，则将该ip置为嫌疑ip
     *              2. 判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
     *                  1. 若为null，则直接加入该嫌疑ip
     *                  2. 若不为null，则遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在于ProcessIpThreshold中
     *                      1. 若已经存在，则将阈值叠加
     *                      2. 若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
     * @param processIpThresholdList 封装有【所有嫌疑ip】及【其阈值】的ProcessIpThresholdList实体类
     * @param timestamp 封装当前时间
     * @return 返回 封装有所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     */
    @Override
    public ProcessIpThresholdList analyzeCrawlersByProIpKeySum(ProcessIpThresholdList processIpThresholdList, Timestamp timestamp) {
        //获取用于process策略的ipKeySum规则分析的原始ip数据
        List<ProcessFilterIpKeySum> rawDataList = findProIpKeySumRawdataWithoutBlackIp(timestamp);
        //获取正在启用的process策略
        Process enableProcess = policiesProcessActionDao.findEnableProcess().get(0);
        //获取关键页面的匹配正则
        List<KeyPages> allKeyPagesPattern = keyPagesActionDao.findAllKeyPagesPattern();
        if (rawDataList != null && rawDataList.size() > 0 && enableProcess != null && allKeyPagesPattern != null && allKeyPagesPattern.size() > 0){
            //获取正在启用的process策略中的ipKeySum规则（即获取ip访问关键页面的最大次数）
            Integer proIpKeySum = enableProcess.getProIpKeySum();
            //获取正在启用的process策略中的ipKeySum规则所设定的阈值
            Integer proIpKeySumThreshold = enableProcess.getProIpKeySumThreshold();
            //创建一个map集合，用于存储ip及其访问关键页面的次数(key：存储ip地址，value：存储该ip访问关键页面的次数)
            Map<String,Integer> map = new HashMap<>();
            //遍历ip原始数据
            for (ProcessFilterIpKeySum rawData : rawDataList) {
                //获取ip的uri
                String uri = rawData.getUri();
                //让该ip的uri与关键页面正则依次进行匹配
                for (KeyPages keyPages : allKeyPagesPattern) {
                    //若匹配成功，则返回true
                    boolean flag = Pattern.matches(keyPages.getKeyPagesPattern(), uri);
                    //若匹配成功，说明该ip访问了关键页面，需要将该ip添加进map集合中
                    if (flag){
                        //判断map集合是否为空
                        if (map.size() == 0){
                            //若map为空，则直接加入该ip，并将该ip对关键页面的访问次数置为1
                            map.put(rawData.getIp(),1);
                        }else {
                            //若map不为空，则遍历map集合，看该ip是否已经存在
                            boolean already = false;
                            //注意这里使用 map.containsKey方法来判断map中是否已经存在该键值，而不使用for，是因为对于map而言containskey方法的效率更高；但是对于list集合而言，它的contains方法效率并不比for遍历快多少。（原因请百度）
                            if (map.containsKey(rawData.getIp())){
                                //若map集合中已经存在该ip，则将该ip对关键页面的访问次数+1（使用put方法，若该键值已存在则只更新value，若不存在put的键值，则向map中添加该键值对）
                                map.put(rawData.getIp(),map.get(rawData.getIp())+1);
                                already = true;
                            }
//                            for (Map.Entry<String, Integer> entry: map.entrySet()){
//                                if (entry.getKey().equals(rawData.getIp())){
//                                    //若map集合中已经存在该ip，则将该ip对关键页面的访问次数+1
//                                    entry.setValue(entry.getValue() + 1);
//                                    already = true;
//                                }
//                            }
                            if (!already){
                                //否则，直接加入该ip，并将该ip对关键页面的访问次数置为1
                                map.put(rawData.getIp(),1);
                            }
                        }
                    }
                }
            }
            //遍历map集合，结合process策略的ipKeySum规则，判断ip是否为嫌疑ip
            for (Map.Entry<String,Integer> entry: map.entrySet()){
                //若该ip对关键页面的访问次数 > 规定的最大次数，则将该ip置为嫌疑ip
                if (entry.getValue() > proIpKeySum){
                    boolean flag = false;
                    //判断ProcessIpThresholdList中的ProcessIpThreshold属性是否为null
                    if (processIpThresholdList.getProcessIpThreshold() == null){
                        //若为空则直接加入该嫌疑ip
                        //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                        ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                        processIpThreshold.setSuspectIp(entry.getKey());
                        processIpThreshold.setSuspectIpThreshold(proIpKeySumThreshold);
                        ArrayList<ProcessIpThreshold> processIpThresholds = new ArrayList<>();
                        processIpThresholds.add(processIpThreshold);
                        processIpThresholdList.setProcessIpThreshold(processIpThresholds);
                    }else {
                        //遍历processIpThresholdList实体类中的ProcessIpThreshold属性，看该嫌疑ip是否已经存在
                        for (ProcessIpThreshold ipThreshold : processIpThresholdList.getProcessIpThreshold()) {
                            if (ipThreshold.getSuspectIp().equals(entry.getKey())){
                                //若已经存在，则将阈值叠加
                                ipThreshold.setSuspectIpThreshold(ipThreshold.getSuspectIpThreshold() + proIpKeySumThreshold);
                                flag = true;
                            }
                        }
                        //若还不存在该嫌疑ip，就将该嫌疑ip加入，并设置阈值
                        if (!flag){
                            //使用 bean工厂来获取 ProcessIpThreshold 对象（这样获取的才是多例的）
                            ProcessIpThreshold processIpThreshold = beanFactory.getBean(ProcessIpThreshold.class);
                            processIpThreshold.setSuspectIp(entry.getKey());
                            processIpThreshold.setSuspectIpThreshold(proIpKeySumThreshold);
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
