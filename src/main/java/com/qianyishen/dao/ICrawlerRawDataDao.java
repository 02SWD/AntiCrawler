package com.qianyishen.dao;

import com.qianyishen.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 该类用于操作 yishen_RawData 表，用于获取分析所需的原始数据
 */
@Repository("crawlerRawDataDao")
public interface ICrawlerRawDataDao {

    /**
     * 查询用于referer头分析所需的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 该实体类中包含【已存在的ip黑名单】于【获取原始数据的时间间隔】。作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 用户referer分析所需的原始数据
     */
    @Select("<script>" +
            "SELECT DISTINCT raw_remoteAddress,raw_referer " +
            "FROM yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='and raw_remoteAddress not in (' close=') and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "   (raw_localTime between date_sub(#{nowDate},interval #{analysisInterval} second) and #{nowDate})" +
            "</where>" +
            "</script>")
    @Results(id = "rawDataMap", value = {
            @Result(id = true, column = "raw_id", property = "rawId"),
            @Result(column = "raw_remoteAddress", property = "rawRemoteAddress"),
            @Result(column = "raw_requestMethod", property = "rawRequestMethod"),
            @Result(column = "raw_serverAddress", property = "rawServerAddress"),
            @Result(column = "raw_referer", property = "rawReferer"),
            @Result(column = "raw_userAgent", property = "rawUserAgent"),
            @Result(column = "raw_uri", property = "rawUri"),
            @Result(column = "raw_request", property = "rawRequest"),
            @Result(column = "raw_requestBody", property = "rawRequestBody"),
            @Result(column = "raw_localTime", property = "rawLocalTime"),
    })
    List<RawData> findAllRefRawData(CrawlerFilteringOperation crawlerFilteringOperation);


    /**
     * 查询用于UserAgent头分析所需的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 该实体类中包含【已存在的ip黑名单】于【获取原始数据的时间间隔】。作用：这样在查询原始数据的时候，就可以直接查询除该ip之外的数据了，以减少数据的处理数量
     * @return 用户UserAgent分析所需的原始数据
     */
    @Select("<script>" +
            "SELECT DISTINCT raw_remoteAddress,raw_userAgent " +
            "FROM yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='raw_remoteAddress not in (' close=') and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "   (raw_localTime between date_sub(#{nowDate},interval #{analysisInterval} second) and #{nowDate})" +
            "</where>" +
            "</script>")
    @ResultMap("rawDataMap")
    List<RawData> findAllUserAgentRawData(CrawlerFilteringOperation crawlerFilteringOperation);


    /**
     * 获取用于【process策略中ipSum规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 封装有【ip黑名单】，【获取原始数据的时间间隔】，【当前时间】
     * @return 原始数据的list集合
     */
    @Select("<script>" +
            "SELECT raw_remoteAddress,count(raw_remoteAddress) as count " +
            "FROM yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='(raw_remoteAddress not in (' close=')) and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "   (raw_localTime between date_sub(#{nowDate},interval #{analysisInterval} second) and #{nowDate}) group by raw_remoteAddress" +
            "</where>" +
            "</script>")
    @Results(id = "processFilterIpSumMap",value = {
            @Result(column = "raw_remoteAddress", property = "ip"),
            @Result(column = "count", property = "count")
    })
    List<ProcessFilterIpSum> findAllProIpSumRawdata(CrawlerFilteringOperation crawlerFilteringOperation);


    /**
     * 获取用于【process策略中ipUserAgent规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 封装有【ip黑名单】，【获取原始数据的时间间隔】，【当前时间】
     * @return 原始数据的list集合
     */
    @Select("<script>" +
            "select raw_remoteAddress,count(raw_remoteAddress) as count " +
            "from (select distinct raw_remoteAddress, raw_userAgent from yishen_rawdata where raw_localTime between date_sub(#{nowDate},interval #{analysisInterval} second) and #{nowDate}) as a " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='(raw_remoteAddress not in (' close=')) ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "</where>" +
            "group by raw_remoteAddress" +
            "</script>")
    @Results(id = "processFilterIpUserAgentMap", value = {
            @Result(column = "raw_remoteAddress", property = "ip"),
            @Result(column = "count", property = "userAgentCount")
    })
    List<ProcessFilterIpUserAgent> findAllProIpUserAgentRawdata(CrawlerFilteringOperation crawlerFilteringOperation);


    /**
     * 获取用于【process策略中ipKeySum规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 封装有【ip黑名单】，【获取原始数据的时间间隔】，【当前时间】
     * @return 原始数据的list集合
     */
    @Select("<script>" +
            "select raw_remoteAddress,raw_uri " +
            "from yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='(raw_remoteAddress not in (' close=')) and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "(raw_localTime between date_sub(now(),interval #{analysisInterval} second) and now())" +
            "</where>" +
            "</script>")
    @Results(id = "processFilterIpKeySum", value = {
            @Result(column = "raw_remoteAddress", property = "ip"),
            @Result(column = "raw_uri", property = "uri")
    })
    List<ProcessFilterIpKeySum> findAllProIpKeySumRawdata(CrawlerFilteringOperation crawlerFilteringOperation);

    /**
     * 获取用于【process策略中ipInterval规则分析】的原始数据（第一部分）
     * 查询在x分钟内，有哪些ip发送了请求
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 封装有【ip黑名单】，【获取原始数据的时间间隔】，【当前时间】
     * @return 原始数据的list集合
     */
    @Select("<script>" +
            "select distinct raw_remoteAddress " +
            "from yishen_rawData " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='(raw_remoteAddress not in (' close=')) and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "(raw_localTime between date_sub(now(),interval #{analysisInterval} second) and now())" +
            "</where>" +
            "</script>")
    List<String> findAllProIpIntervalRawdataFirst(CrawlerFilteringOperation crawlerFilteringOperation);

    /**
     * 获取用于【process策略中ipInterval规则分析】的原始数据（第二部分）
     * 查询ip查询其在x分钟内的所有访问时间，以便计算其平均访问间隔
     * @param ip ip地址（该ip不为嫌疑ip）
     * @return 该ip在x分钟内，所有访问时间的集合
     */
    @Select("select raw_localTime from yishen_rawData where raw_remoteAddress = #{ip} order by raw_localTime")
    List<Timestamp> findAllProIpIntervalRawdataSecond(String ip);

    /**
     * 获取用于【process策略中ipInterval规则分析】的原始数据
     * （但是对于已经存在于ip黑名单中对应的原始数据不进行采集，且只对【获取原始数据的时间间隔】之内的数据进行查询）
     * @param crawlerFilteringOperation 封装有【ip黑名单】，【获取原始数据的时间间隔】，【当前时间】
     * @return 原始数据的list集合
     */
    @Select("<script>" +
            "select distinct raw_remoteAddress, raw_uri " +
            "from yishen_rawData " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='(raw_remoteAddress not in (' close=')) and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "(raw_localTime between date_sub(now(),interval #{analysisInterval} second) and now())" +
            "</where>" +
            "</script>")
    @Results(id = "processFilterIpKeyIntervalMap", value = {
            @Result(column = "raw_remoteAddress", property = "ip"),
            @Result(column = "raw_uri", property = "uri")
    })
    List<ProcessFilterIpKeyInterval> findAllProIpKeyIntervalRawdataFirst(CrawlerFilteringOperation crawlerFilteringOperation);

    /**
     * 获取用于【process策略中ipKeyInterval规则分析】的原始数据（第二部分）
     * 查询ip查询其在x分钟内的所有访问时间，以便计算其平均访问间隔
     * @param ip ip地址（该ip不为嫌疑ip）
     * @return 该ip在x分钟内，所有访问时间的集合
     */
    @Select("select raw_localTime from yishen_rawData where raw_remoteAddress = #{ip} order by raw_localTime")
    List<Timestamp> findAllProIpKeyIntervalRawdataSecond(String ip);

}
