package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.IBlackIpActionDao;
import com.qianyishen.dao.IBlackTimeActionDao;
import com.qianyishen.dao.IPoliciesProcessActionDao;
import com.qianyishen.domain.BlackIp;
import com.qianyishen.domain.BlackTime;
import com.qianyishen.domain.ProcessIpThreshold;
import com.qianyishen.domain.ProcessIpThresholdList;
import com.qianyishen.service.IProcessFilterMainService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * process策略总调度
 */
@Service("processFilterMainService")
public class ProcessFilterMainServiceImpl implements IProcessFilterMainService {

    //用于记录嫌疑ip，及其对应的阈值，以便在最后得出最终结论，判断出真正的爬虫ip
    @Resource(name = "processIpThresholdList")
    private ProcessIpThresholdList processIpThresholdList;

    //根据process策略的ipSum规则筛选出嫌疑ip
    @Resource(name = "processFilterIpSumService")
    private ProcessFilterIpSumServiceImpl processFilterIpSumService;

    //根据process策略的ipKeySum规则筛选出嫌疑ip
    @Resource(name = "processFilterIpKeySumService")
    private ProcessFilterIpKeySumServiceImpl processFilterIpKeySumService;

    //根据process策略的ipUserAgent规则筛选出嫌疑ip
    @Resource(name = "processFilterIpUserAgentService")
    private ProcessFilterIpUserAgentServiceImpl processFilterIpUserAgentService;

    //根据process策略的ipInterval规则筛选出嫌疑ip
    @Resource(name = "processFilterIpIntervalService")
    private ProcessFilterIpIntervalServiceImpl processFilterIpIntervalService;

    //根据process策略的ipKeyInterval规则筛选出嫌疑ip
    @Resource(name = "processFilterIpKeyIntervalService")
    private ProcessFilterIpKeyIntervalServiceImpl processFilterIpKeyIntervalService;

    //将爬虫ip添加至ip黑名单
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    @Resource(name = "blackTimeActionDao")
    private IBlackTimeActionDao blackTimeActionDao;

    //引入 policiesProcessActionDao 用于查询正在启用的process策略
    @Resource(name = "policiesProcessActionDao")
    private IPoliciesProcessActionDao policiesProcessActionDao;

    /**
     * process策略分析的【主函数】，该函数会依次调用：
     * ProcessFilterIpSumServiceImpl、ProcessFilterIpKeySumServiceImpl、ProcessFilterIpUserAgentServiceImpl、
     * ProcessFilterIpIntervalServiceImpl、ProcessFilterIpKeyIntervalServiceImpl
     * 这5个类中的【分析方法】，先筛选出【嫌疑ip】，
     * 然后将每一个嫌疑ip对应的【累加阈值】【除以5】得到最终的【平均阈值】，
     * 再将【平均阈值】与process策略设定的【最终阈值】做比较
     * 若【平均阈值】 > 【最终阈值】：则判定该【嫌疑ip】为【爬虫ip】，并将该爬虫ip写进mysql的ip黑名单中（即：yishen_blackIp表中）
     * 若【平均阈值】 <=【最终阈值】：则判定该【嫌疑ip】为【非爬虫ip】，不做任何处理
     * @return false 代表本次分析【没有出现】【爬虫ip】
     *         true  代表本次分析【出现了】【爬虫ip】，并将该爬虫ip成功地添加进了mysql的yishen_blackIp表（ip黑名单）中
     */
    @Override
    public boolean analyzeCrawlersByProMain(Timestamp timestamp){
        //根据process策略的ipSum规则筛选出嫌疑ip
        ProcessIpThresholdList processIpThresholdListByIpSum = processFilterIpSumService.analyzeCrawlersByProIpSum(this.processIpThresholdList,timestamp);
        //根据process策略的ipKeySum规则筛选出嫌疑ip
        ProcessIpThresholdList processIpThresholdListByIpKeySum = processFilterIpKeySumService.analyzeCrawlersByProIpKeySum(processIpThresholdListByIpSum, timestamp);
        //根据process策略的ipUserAgent规则筛选出嫌疑ip
        ProcessIpThresholdList processIpThresholdListByIpUserAgent = processFilterIpUserAgentService.analyzeCrawlersByProIpUserAgent(processIpThresholdListByIpKeySum, timestamp);
        //根据process策略的ipInterval规则筛选出嫌疑ip
        ProcessIpThresholdList processIpThresholdListByIpInterval = processFilterIpIntervalService.analyzeCrawlersByProIpInterval(processIpThresholdListByIpUserAgent, timestamp);
        //根据process策略的ipKeyInterval规则筛选出嫌疑ip
        ProcessIpThresholdList processIpThresholdListByIpKeyInterval = processFilterIpKeyIntervalService.analyzeCrawlersByProIpKeyInterval(processIpThresholdListByIpInterval, timestamp);
        //该list集合用于【暂时存储】爬虫ip
        ArrayList<BlackIp> blackIpList = new ArrayList<>();
        //获取正在启用的process策略所设定的【最终阈值】
        Integer proFinalThreshold = policiesProcessActionDao.findEnableProcess().get(0).getProFinalThreshold();
        //判断是否筛选出了一些【嫌疑ip】
        if (processIpThresholdListByIpKeyInterval.getProcessIpThreshold() != null){
            //若筛选出了一些【嫌疑ip】，就遍历这些嫌疑ip（并将每一个嫌疑ip对应的【累加阈值】【除以5】得到最终的【平均阈值】，再将【平均阈值】与process策略设定的【最终阈值】做比较）
            for (ProcessIpThreshold ipThreshold : processIpThresholdListByIpKeyInterval.getProcessIpThreshold()) {
                //将每个【嫌疑ip】对应的【累加阈值】除以5，得到最终的【平均阈值】
                Integer ipFinalThreshold = ipThreshold.getSuspectIpThreshold()/5;
                //将【平均阈值】与process策略设定的【最终阈值】做比较
                if (ipFinalThreshold > proFinalThreshold){
                    //若【平均阈值】 > 【最终阈值】，则将该【嫌疑ip】置为【爬虫ip】，并暂存进blackIpList集合中去
                    BlackIp blackIp = new BlackIp();
                    blackIp.setBlackIp(ipThreshold.getSuspectIp());
                    blackIpList.add(blackIp);
                }
            }
        }
        //若该list集合中有值，则将该list集合中的【爬虫ip】写进mysql的yishen_blackIp表中（即：ip黑名单）
        if (blackIpList.size() > 0){
            //读取爬虫ip要被封禁的时间间隔
            List<BlackTime> blackTime = blackTimeActionDao.findBlackTime();
            //将爬虫ip插入到 yishen_blackIp表中，写入成功返回true
            return blackIpActionDao.insertBlackIpByList(blackIpList,blackTime.get(0).getBlackTimeInterval());
        }
        return false;
    }


}
