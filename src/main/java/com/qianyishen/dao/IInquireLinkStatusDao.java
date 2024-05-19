package com.qianyishen.dao;

import com.qianyishen.domain.BlackIp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 查询【链路流量，即服务器的用户请求数量】
 */
@Repository("inquireLinkStatusDao")
public interface IInquireLinkStatusDao {

    /**
     * 查询某一时间段内【全部用户】的请求数量（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的请求量）
     * 比如：
     *      要查询 "2022-05-06 0:0:0"+1 ~ "2022-05-06 0:0:0"+2 时间段之内的请求量  是指
     *      要查询 "2022-05-06 1:0:0" ~ "2022-05-06 2:0:0" 时间段之内的请求量
     *
     * @param initialData 初始时刻 如："2022-05-06 0:0:0"
     * @param downLinkInterval 时刻（单位：小时）
     * @param upLinkInterval 时刻（单位：小时）
     * @return 全部IP的请求量
     */
    @Select("<script>" +
            "select count(*) from yishen_rawdata " +
            "<where>" +
            "   raw_localTime between date_add(#{initialData},interval #{downLinkInterval} hour) and date_add(#{initialData},interval #{upLinkInterval} hour)" +
            "</where>" +
            "</script>")
    int getTotalFlowByRawHour(@Param("initialData") String initialData,
                             @Param("downLinkInterval") int downLinkInterval,
                             @Param("upLinkInterval") int upLinkInterval);

    /**
     * 查询某一时间段内的【爬虫IP】请求数量（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的请求量）
     * 比如：
     *      要查询 "2022-05-06 0:0:0"+1 ~ "2022-05-06 0:0:0"+2 时间段之内的爬虫IP请求量  是指
     *      要查询 "2022-05-06 1:0:0" ~ "2022-05-06 2:0:0" 时间段之内的爬虫IP请求量
     *
     * @param blackIpList 爬虫IP集合
     * @param initialData 初始时刻 如："2022-05-06 0:0:0"
     * @param downLinkInterval 时刻（单位：小时）
     * @param upLinkInterval 时刻（单位：小时）
     * @return 爬虫IP的请求量
     */
    @Select("<script>" +
            "select count(*) from yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='raw_remoteAddress in (' close=') and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "   (raw_localTime between date_add(#{initialData},interval #{downLinkInterval} hour) and date_add(#{initialData},interval #{upLinkInterval} hour))" +
            "</where>" +
            "</script>")
    int getTotalFlowByReptileHour(@Param("blackIpList") List<BlackIp> blackIpList,
                                 @Param("initialData") String initialData,
                                 @Param("downLinkInterval") int downLinkInterval,
                                 @Param("upLinkInterval") int upLinkInterval);

    /**
     * 查询某一时间段内【全部用户】的请求数量（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的请求量）
     * 比如：
     *      要查询 "2022-05-06 13:0:0"+1 ~ "2022-05-06 13:0:0"+2 时间段之内的全部用户的请求量 是指
     *      要查询 "2022-05-06 13:1:0" ~ "2022-05-06 13:2:0" 时间段之内的全部用户的请求量
     *
     * @param initialData 初始时刻 如："2022-05-06 13:0:0"
     * @param downLinkInterval 时刻（单位：分钟）
     * @param upLinkInterval 时刻（单位：分钟）
     * @return 全部IP的请求量
     */
    @Select("<script>" +
            "select count(*) from yishen_rawdata " +
            "<where>" +
            "   raw_localTime between date_add(#{initialData},interval #{downLinkInterval} minute) and date_add(#{initialData},interval #{upLinkInterval} minute)" +
            "</where>" +
            "</script>")
    int getTotalFlowByRawMinute(@Param("initialData") String initialData,
                                 @Param("downLinkInterval") int downLinkInterval,
                                 @Param("upLinkInterval") int upLinkInterval);

    /**
     * 查询某一时间段内的【爬虫IP】请求数量（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的请求量）
     * 比如：
     *      要查询 "2022-05-06 13:0:0"+1 ~ "2022-05-06 13:0:0"+2 时间段之内的爬虫IP请求量 是指
     *      要查询 "2022-05-06 13:1:0" ~ "2022-05-06 13:2:0" 时间段之内的爬虫IP请求量
     *
     * @param blackIpList 爬虫IP集合
     * @param initialData 初始时刻 如："2022-05-06 13:0:0"
     * @param downLinkInterval 时刻（单位：分钟）
     * @param upLinkInterval 时刻（单位：分钟）
     * @return 爬虫IP的请求量
     */
    @Select("<script>" +
            "select count(*) from yishen_rawdata " +
            "<where>" +
            "   <if test='blackIpList != null and blackIpList.size() > 0'>" +
            "       <foreach collection='blackIpList' open='raw_remoteAddress in (' close=') and ' item='blackIp' separator=','>" +
            "           #{blackIp.blackIp}" +
            "       </foreach>" +
            "   </if>" +
            "   (raw_localTime between date_add(#{initialData},interval #{downLinkInterval} minute) and date_add(#{initialData},interval #{upLinkInterval} minute))" +
            "</where>" +
            "</script>")
    int getTotalFlowByReptileMinute(@Param("blackIpList") List<BlackIp> blackIpList,
                                  @Param("initialData") String initialData,
                                  @Param("downLinkInterval") int downLinkInterval,
                                  @Param("upLinkInterval") int upLinkInterval);


}
