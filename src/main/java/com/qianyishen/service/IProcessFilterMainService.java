package com.qianyishen.service;

import java.sql.Timestamp;

/**
 * process策略总调度
 */
public interface IProcessFilterMainService {

    /**
     * 根据process中的策略来分析原始数据，从而筛选出爬虫ip，并将该ip写进mysql数据库的ip黑名单中
     * @return false 代表本次分析【没有出现】【爬虫ip】
     *         true  代表本次分析【出现了】【爬虫ip】，并将该爬虫ip成功地添加进了mysql的yishen_blackIp表中
     */
    boolean analyzeCrawlersByProMain(Timestamp timestamp);


}
