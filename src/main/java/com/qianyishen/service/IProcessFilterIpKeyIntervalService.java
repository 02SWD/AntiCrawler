package com.qianyishen.service;

import com.qianyishen.domain.ProcessFilterIpKeyInterval;
import com.qianyishen.domain.ProcessIpThresholdList;

import java.sql.Timestamp;
import java.util.List;

public interface IProcessFilterIpKeyIntervalService {

    /**
     * 获取用于【process策略中IpKeyInterval规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     *  作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @param timestamp 设置当前时间
     *        这里你会发现，相比于 RefererFilterServiceImpl 和 UserAgentFilterServiceImpl 类的原始数据获取方法
     *        多了一个 timestamp 参数
     * @return 原始数据的list集合
     */
    List<ProcessFilterIpKeyInterval> findProIpKeyIntervalRawdataWithoutBlackIpFirst(Timestamp timestamp);

    /**
     * 获取用于【process策略中ipKeyInterval规则分析】的原始数据（第二部分）
     * 查询ip查询其在x分钟内的所有访问时间，以便计算其平均访问间隔
     * @param ip ip地址（该ip不为嫌疑ip）
     * @return 该ip在x分钟内，所有访问时间的集合
     */
    List<Timestamp> findProIpKeyIntervalRawdataWithoutBlackIpSecond(String ip);

    /**
     * 根据process策略中的IpKeyInterval规则分析ip原始数据，从而判断出嫌疑ip
     * @param processIpThresholdList 封装有，所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     * @param timestamp 封装当前时间
     * @return 返回 封装有所有嫌疑ip及其阈值的ProcessIpThresholdList实体类
     */
    ProcessIpThresholdList analyzeCrawlersByProIpKeyInterval(ProcessIpThresholdList processIpThresholdList, Timestamp timestamp);







}
