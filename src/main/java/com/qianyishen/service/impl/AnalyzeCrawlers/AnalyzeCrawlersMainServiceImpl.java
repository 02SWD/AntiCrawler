package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.service.IAnalyzeCrawlersMainService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * 爬虫分析【总调度类】
 */
@Service("analyzeCrawlersMainService")
public class AnalyzeCrawlersMainServiceImpl implements IAnalyzeCrawlersMainService {

    //引入referer策略分析类
    @Resource(name = "refererFilterService")
    private RefererFilterServiceImpl refererFilterService;

    //引入UserAgent策略分析类
    @Resource(name = "userAgentFilterService")
    private UserAgentFilterServiceImpl userAgentFilterService;

    //引入process策略分析类
    @Resource(name = "processFilterMainService")
    private ProcessFilterMainServiceImpl processFilterMainService;

    /**
     * 该方法为【爬虫分析】的总调度（该方法使用了定时器，为 ========【每5分钟】执行一次（即每5分钟分析一次）===========），
     * 先后调用RefererFilterServiceImpl、UserAgentFilterServiceImpl、ProcessFilterMainServiceImpl
     * 三个类中的分析方法，分析ip原始数据，并识别出爬虫ip，将爬虫ip列入mysql的ip黑名单中
     *
     * 另外：之所以先进行Referer分析，再进行UserAgent分析，最后进行process分析，
     * 是因为：process分析所需要处理的数据量及复杂度 远大于 Referer分析和UserAgent分析，
     * 而我在设计之初就规定：获取的原始数据，是获取除了已存在于ip黑名单之外的ip原始数据，
     * 所以若可以先进行Referer分析和UserAgent分析筛选出部分爬虫ip的话，
     * 那么最后再process分析时，就可以减少ip原始数据的获取，也就减少了数据的分析量，加快程序的执行
     */
    @Override
    @Scheduled(cron = "0 0/1 * * * ?")//每1分钟进行一次爬虫分析
    public void test() {
        //获取当前时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //使用referer策略识别爬虫ip，并将爬虫ip加入mysql中的ip黑名单
        refererFilterService.analyzeCrawlersByRef(timestamp);
        //使用UserAgent策略识别爬虫ip，并将爬虫ip加入mysql中的ip黑名单
        userAgentFilterService.analyzeCrawlersByUa(timestamp);
        //使用process策略识别爬虫ip，并将爬虫ip加入mysql中的ip黑名单
        processFilterMainService.analyzeCrawlersByProMain(timestamp);
    }
}
