package com.qianyishen.dao;

import com.qianyishen.domain.Server;
import com.qianyishen.domain.ServerStatus;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于查询服务器的状态信息
 */
@Repository("inquireServerStatusDao")
public interface IInquireServerStatusDao {


    /**
     * 用于查询服务器的基本信息（主键id，主机名，创建时间）
     * @return 封装有服务器基本信息的list集合
     */
    @Select("select * from yishen_server")
    @Results(id = "BasicInformationMap", value = {
            @Result(id = true,column = "server_id", property = "serverId"),
            @Result(column = "server_hostname", property = "serverHostname"),
            @Result(column = "server_time", property = "serverTime")
    })
    List<Server> findBasicInformation();


    /**
     * 用于查询服务器的状态信息（cpu占用率，内存占用率，磁盘占用率，磁盘IO率）
     * @param id 所要查询的服务器状态信息的服务器主键id
     * @param nowDate 传递从什么时刻开始查询
     * @param m 代表从第m条信息开始向后查询（用于分页查询：limit m,n  m=m+1 n=linesize）
     * @return 封装有服务器状态信息的list集合
     */
    @Select("select * from yishen_serverStatus where ser_foreignKeyId = #{id} and ser_createTime >= #{nowDate} limit #{m},6")
    @Results(id = "ServerStatusMap", value = {
            @Result(id = true, column = "ser_id", property = "serId"),
            @Result(column = "ser_cpu", property = "serCpu"),
            @Result(column = "ser_memory", property = "serMemory"),
            @Result(column = "ser_disk", property = "serDisk"),
            @Result(column = "ser_diskIo", property = "serDiskIo"),
            @Result(column = "ser_foreignKeyId", property = "serForeignId"),
            @Result(column = "ser_createTime",property = "serCreateTime")
    })
    List<ServerStatus> findServerStatus(@Param("id") int id, @Param("nowDate") String nowDate, @Param("m") int m);

    /**
     * 根据id查询对应服务器的基本信息
     * @param id 服务器对应的主键id
     * @return 封装有服务器数据
     */
    @Select("select * from yishen_server where server_id = #{id}")
    @ResultMap("BasicInformationMap")
    Server findBasicInformationById(@Param("id") int id);

}
