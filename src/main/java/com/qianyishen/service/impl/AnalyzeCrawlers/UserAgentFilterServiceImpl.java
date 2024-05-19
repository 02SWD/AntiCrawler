package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.*;
import com.qianyishen.domain.*;
import com.qianyishen.service.IUserAgentFilterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 该类用于分析原始数据中的UserAgent头，从而找出爬虫ip，并将其加入黑名单
 */
@Service("userAgentFilterService")
public class UserAgentFilterServiceImpl implements IUserAgentFilterService {

    //用于获取已存在的ip黑名单 或 将爬虫ip添加至ip黑名单
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    //需要使用CrawlerFilteringOperation实体类【存储已存在的ip黑名单】、【获取原始数据的时间间隔】
    @Resource(name = "crawlerFilteringOperation")
    private CrawlerFilteringOperation crawlerFilteringOperation;

    //查询“用于根据UserAgent头进行分析所需”的爬虫原始数据
    @Resource(name = "crawlerRawDataDao")
    private ICrawlerRawDataDao crawlerRawDataDao;

    //用于查询UserAgent正则
    @Resource(name = "policiesUserAgentAction")
    private IPoliciesUserAgentActionDao policiesUserAgentActionDao;

    //用于读取爬虫ip要被封禁的时间间隔
    @Resource(name = "blackTimeActionDao")
    private IBlackTimeActionDao blackTimeActionDao;

    //用于获取【获取原始数据的时间间隔：timeInterval】（作用：使系统获取now()-timeInterval ~ now() 之内的数据）
    @Resource(name = "analysisIntervalActionDao")
    private IAnalysisIntervalActionDao analysisIntervalActionDao;

    /**
     * 查询，用于UserAgent头分析，所需的原始数据（但是对于已经存在于ip黑名单中对应的原始数据不进行采集）
     *      作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 原始数据
     */
    @Override
    public List<RawData> findAllUaRawDataWithoutBlackIp(Timestamp timestamp) {
        //查询 获取原始数据的时间间隔
        List<AnalysisInterval> timeInterval = analysisIntervalActionDao.findTimeInterval();
        //用于查询已存在的ip黑名单
        List<BlackIp> allBlackIp = blackIpActionDao.findBlackIp();
        //将该ip黑名单存入CrawlerFilteringOperation实体类的BlackIpList属性中
        crawlerFilteringOperation.setBlackIpList(allBlackIp);
        //查询原始数据的时间间隔（即：查询 now()-analysisInterval ~ now() 之内的原始数据，单位：秒）
        crawlerFilteringOperation.setAnalysisInterval(timeInterval.get(0).getIntervalTime());
        //设置当前时间
        crawlerFilteringOperation.setNowDate(timestamp.toString());
        //返回除ip黑名单之外，且在“获取原始数据时间间隔”之内的所有ip的原始数据
        return crawlerRawDataDao.findAllUserAgentRawData(crawlerFilteringOperation);
    }

    /**
     * 开始根据UserAgent正则进行解析
     * 根据原始数据分析出应被拦截的爬虫ip，并将ip添加进mysql数据库的yishen_blackIp表中（ip黑名单）
     * @return 若ip都正常(即没有爬虫) 或 无原始数据 或 无referer正则，则都返回false
     */
    @Override
    public boolean analyzeCrawlersByUa(Timestamp timestamp) {
        //获取除ip黑名单之外，且在【获取原始数据时间间隔】之内的所有ip的原始数据
        List<RawData> rawDataList = findAllUaRawDataWithoutBlackIp(timestamp);
        //获取UserAgent正则
        List<UserAgent> userAgentList = policiesUserAgentActionDao.findAllUa();
        //该list集合用于【暂时存储】爬虫ip
        ArrayList<BlackIp> blackIpList = new ArrayList<>();
        //开始分析ip原始数据，并将应被拦截的ip先封装进BlackIp实体类中，然后再将该BlackIp实体类添加进blackIpList集合中，最后将该集合插入mysql中
        //若无原始数据 或 无referer正则，则不进行拦截
        if (rawDataList != null && rawDataList.size() > 0 && userAgentList != null && userAgentList.size() > 0){
            for (RawData rawData : rawDataList) {
                for (UserAgent userAgent : userAgentList) {
                    //进行逐一匹配
                    boolean matches = Pattern.matches(userAgent.getUserAgentPattern(), rawData.getRawUserAgent());
                    //因为UserAgent正则采用的是【黑名单】，所以【匹配成功】的均为爬虫ip，要列入黑名单
                    if (matches){
                        //new 一个BlackIp实体类
                        BlackIp blackIp = new BlackIp();
                        //将应被拦截的ip先封装进BlackIp实体类中
                        blackIp.setBlackIp(rawData.getRawRemoteAddress());
                        //将该BlackIp实体类添加进blackIpList集合
                        blackIpList.add(blackIp);
                    }
                }
            }
            if (blackIpList.size() > 0){
                //读取爬虫ip要被封禁的时间间隔
                List<BlackTime> blackTimeList = blackTimeActionDao.findBlackTime();
                //将爬虫ip插入到 yishen_blackIp表中
                return blackIpActionDao.insertBlackIpByList(blackIpList,blackTimeList.get(0).getBlackTimeInterval());
            }

        }
        return false;
    }
}
