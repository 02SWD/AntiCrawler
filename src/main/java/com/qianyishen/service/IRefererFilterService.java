package com.qianyishen.service;

import com.qianyishen.domain.RawData;

import java.sql.Timestamp;
import java.util.List;

/**
 * 该类用于分析原始数据中的referer头，从而找出爬虫ip，并将其加入黑名单
 */
public interface IRefererFilterService {

    /**
     * 查询，用于referer头分析，所需的原始数据（但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * 作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 原始数据
     */
    List<RawData> findAllRefRawdataWithoutBlackIp(Timestamp timestamp);

    /**
     * 分析爬虫，并将分析出来的ip黑名单添加进yishen_blackIp表中
     * @return 若ip都正常(即没有爬虫) 或 无原始数据 或 无referer正则，则都返回false
     */
    boolean analyzeCrawlersByRef(Timestamp timestamp);


}
