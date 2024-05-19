package com.qianyishen.service;

import com.qianyishen.domain.RawData;

import java.sql.Timestamp;
import java.util.List;

public interface IUserAgentFilterService {

    /**
     * 查询用于UserAgent头分析所需的原始数据（但是对于已经存在于ip黑名单中对应的原始数据不进行采集）
     * 作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 用户UserAgent分析所需的原始数据
     */
    List<RawData> findAllUaRawDataWithoutBlackIp(Timestamp timestamp);


    /**
     * 开始根据UserAgent正则进行解析
     * 根据原始数据分析出应被拦截的爬虫ip，并将ip添加进mysql数据库的yishen_blackIp表中（ip黑名单）
     * @return 若ip都正常(即没有爬虫) 或 无原始数据 或 无referer正则，则都返回false
     */
    boolean analyzeCrawlersByUa(Timestamp timestamp);


}
