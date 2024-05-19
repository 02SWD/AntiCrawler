package com.qianyishen.dao;

import com.qianyishen.domain.BlackIp;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 该类用于操作 yishen_blackIp 表中的数据（操作Ip黑名单）
 * 用于创建，查询，删除 ip黑名单
 */
@Repository("blackIpActionDao")
public interface IBlackIpActionDao {

    /**
     * 读取yishen_blackIp表中的数据（读取ip黑名单，注意：这里的ip是仍处于封禁期间的ip）
     * @return ip黑名单
     */
    @Select("select * from yishen_blackIp where black_time > now()")
    @Results(id = "blackIp", value = {
            @Result(id = true, column = "black_id", property = "blackId"),
            @Result(column = "black_ip", property = "blackIp"),
            @Result(column = "black_timeStart", property = "blackTimeStart"),
            @Result(column = "black_time", property = "blackTime")
    })
    List<BlackIp> findBlackIp();

    /**
     * 查询某一时间段内的爬虫IP（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的爬虫ip）
     * 比如：
     *      要查询 "2022-05-06 0:0:0"+1 ~ "2022-05-06 0:0:0"+2 时间段之内的爬虫IP  是指
     *      要查询 "2022-05-06 1:0:0" ~ "2022-05-06 2:0:0" 时间段之内的爬虫IP
     *
     * @param initialData 初始时刻 如："2022-05-06 0:0:0"
     * @param downLinkInterval 时刻（单位：小时）
     * @param upLinkInterval 时刻（单位：小时）
     * @return 爬虫IP列表
     */
    @Select("<script>" +
            "select * from yishen_blackIp" +
            "<where>" +
            "   black_timeStart between date_add(#{initialData},interval #{downLinkInterval} hour) and date_add(#{initialData},interval #{upLinkInterval} hour)" +
            "</where>" +
            "</script>")
    @ResultMap("blackIp")
    List<BlackIp> findBlackIpByHour(@Param("initialData") String initialData,
                                    @Param("downLinkInterval") int downLinkInterval,
                                    @Param("upLinkInterval") int upLinkInterval);

    /**
     * 查询某一时间段内的爬虫IP（查询 initialData+downLinkInterval ~ initialData+upLinkInterval 时间段之内的爬虫ip）
     * 比如：
     *      要查询 "2022-05-06 17:0:0"+1 ~ "2022-05-06 17:0:0"+2 时间段之内的爬虫IP 是指
     *      要查询 "2022-05-06 17:1:0" ~ "2022-05-06 17:2:0" 时间段之内的爬虫IP
     *
     * @param initialData 初始时刻 如："2022-05-06 17:0:0"
     * @param downLinkInterval 时刻（单位：分钟）
     * @param upLinkInterval 时刻（单位：分钟）
     * @return 爬虫IP列表
     */
    @Select("<script>" +
            "select * from yishen_blackIp" +
            "<where>" +
            "   black_timeStart between date_add(#{initialData},interval #{downLinkInterval} minute) and date_add(#{initialData},interval #{upLinkInterval} minute)" +
            "</where>" +
            "</script>")
    @ResultMap("blackIp")
    List<BlackIp> findBlackIpByMinute(@Param("initialData") String initialData,
                                      @Param("downLinkInterval") int downLinkInterval,
                                      @Param("upLinkInterval") int upLinkInterval);

    /**
     * 将ip集合添加进黑名单（参数为ip集合）
     * @param blackIpList ip黑名单
     * @param blackTime 要封禁多长时间（单位：小时）
     * @return 插入成功返回true
     */
    @Insert("<script>" +
                "insert into yishen_blackIp (black_ip, black_timeStart, black_time) values " +
                "<foreach collection='listIp' item='item' index='index' separator=','>" +
                "   (#{item.blackIp}, now(), date_add(now(), interval #{blackTimeInterval} hour))" +
                "</foreach>" +
            "</script>")
    boolean insertBlackIpByList(@Param("listIp") List<BlackIp> blackIpList, @Param("blackTimeInterval") Integer blackTime);


}
