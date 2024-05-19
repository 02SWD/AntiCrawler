package com.qianyishen.service.impl.AnalyzeCrawlers;

import com.qianyishen.dao.*;
import com.qianyishen.domain.*;
import com.qianyishen.service.IRefererFilterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 该类用于分析原始数据中的referer头，从而找出爬虫ip，并将其加入黑名单
 */
@Service("refererFilterService")
public class RefererFilterServiceImpl implements IRefererFilterService {

    //用于获取已存在的ip黑名单 或 将爬虫ip添加至ip黑名单
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    //需要使用CrawlerFilteringOperation实体类【存储已存在的ip黑名单】、【获取原始数据的时间间隔】
    @Resource(name = "crawlerFilteringOperation")
    private CrawlerFilteringOperation crawlerFilteringOperation;

    //查询用于根据referer头进行分析所需的爬虫原始数据
    @Resource(name = "crawlerRawDataDao")
    private ICrawlerRawDataDao crawlerRawDataDao;

    //用于查询referer正则
    @Resource(name = "policiesRefererAction")
    private IPoliciesRefererActionDao policiesRefererActionDao;

    //用于读取 爬虫ip要被封禁的时间间隔
    @Resource(name = "blackTimeActionDao")
    private IBlackTimeActionDao blackTimeActionDao;

    //用于获取【获取原始数据的时间间隔：timeInterval】（作用：使系统获取now()-timeInterval ~ now() 之内的数据）
    @Resource(name = "analysisIntervalActionDao")
    private IAnalysisIntervalActionDao analysisIntervalActionDao;


    /**
     * 查询，用于referer头分析，所需的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     *  作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 原始数据
     */
    @Override
    public List<RawData> findAllRefRawdataWithoutBlackIp(Timestamp timestamp){
        //查询 获取原始数据的时间间隔
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
        return crawlerRawDataDao.findAllRefRawData(crawlerFilteringOperation);
    }

    /**
     * 开始根据referer正则进行解析
     * 根据原始数据分析出应被拦截的ip，并将ip添加进mysql数据库的yishen_blackIp表中（ip黑名单）
     * @return 若ip都正常(即没有爬虫) 或 无原始数据 或 无referer正则，则都返回false
     */
    @Override
    public boolean analyzeCrawlersByRef(Timestamp timestamp){
        //获取除ip黑名单之外，且在【获取原始数据时间间隔】之内的所有ip的原始数据
        List<RawData> rawDataList = findAllRefRawdataWithoutBlackIp(timestamp);
        //获取referer过滤策略
        List<Referer> refererList = policiesRefererActionDao.findAllRef();
        //该list集合用于【暂时存储】爬虫ip
        ArrayList<BlackIp> blackIpList = new ArrayList<>();
        //开始分析ip原始数据，并将应被拦截的ip先封装进BlackIp实体类中，然后再将该BlackIp实体类添加进blackIpList集合中，最后将该集合插入mysql中
        //若无原始数据 或 无referer正则，则不进行拦截
        if (rawDataList != null && rawDataList.size() > 0 && refererList != null && refererList.size() > 0){
            for (RawData rawData : rawDataList) {
                for (Referer refererPatten : refererList) {
                    //进行逐一匹配
                    boolean matches = Pattern.matches(refererPatten.getRefPattern(), rawData.getRawReferer());
                    //因为referer采用【白名单】，所以【匹配不成功】的都是爬虫的ip
                    if (!matches){
                        //new 一个BlackIp实体类
                        BlackIp blackIp = new BlackIp();
                        //将应被拦截的ip先封装进BlackIp实体类中
                        blackIp.setBlackIp(rawData.getRawRemoteAddress());
                        //将该BlackIp实体类添加进blackIpList集合
                        blackIpList.add(blackIp);
                    }
                }
            }
            //若该list集合中有值，则将该list集合中的【爬虫ip】写进mysql的yishen_blackIp表中（即：ip黑名单）
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
