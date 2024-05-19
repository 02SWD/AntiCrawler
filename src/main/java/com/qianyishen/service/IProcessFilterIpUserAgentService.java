package com.qianyishen.service;

import com.qianyishen.domain.ProcessFilterIpUserAgent;
import com.qianyishen.domain.ProcessIpThresholdList;

import java.sql.Timestamp;
import java.util.List;

/**
 * 根据process策略的ipUserAgent规则筛选出嫌疑ip
 */
public interface IProcessFilterIpUserAgentService {

    /**
     * 获取用于【process策略中ipUserAgent规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     *  作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @param timestamp 设置当前时间
     *        这里你会发现，相比于 RefererFilterServiceImpl 和 UserAgentFilterServiceImpl 类的原始数据获取方法
     *        多了一个 timestamp 参数
     * @return 原始数据的list集合
     */
    List<ProcessFilterIpUserAgent> findProIpUserAgentRawdataWithoutBlackIp(Timestamp timestamp);


    /**
     * 根据process策略中的ipUserAgent规则分析ip原始数据，从而判断出嫌疑ip
     * @param processIpThresholdList 封装有，所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     * @param timestamp 封装当前时间
     * @return 返回 封装有所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     */
    ProcessIpThresholdList analyzeCrawlersByProIpUserAgent(ProcessIpThresholdList processIpThresholdList, Timestamp timestamp);







}
