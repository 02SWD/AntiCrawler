package com.qianyishen.service;

import com.qianyishen.domain.Server;
import com.qianyishen.domain.ServerStatus;

import java.sql.Timestamp;
import java.util.List;

/**
 * 用于查询服务器的基本信息及其状态信息
 */
public interface ServerStatusMonitorService {

    /**
     * 用于查询服务器的基本信息（主键id，主机名，创建时间）
     * @return 封装有服务器基本信息的list集合
     */
    List<Server> findBasicInformation();


    /**
     * 用于查询服务器的状态信息（cpu占用率，内存占用率，磁盘占用率，磁盘IO率）
     * @param id 所要查询的服务器状态信息的服务器主键id
     * @return 封装有服务器状态信息的list集合
     */
    List<ServerStatus> findServerStatus(int id, Timestamp nowDate, int m);

    /**
     * 根据id查询对应服务器的基本信息
     * @param id 服务器对应的主键id
     * @return 封装有服务器数据
     */
    Server findBasicInformationById(int id);
}
