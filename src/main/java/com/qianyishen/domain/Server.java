package com.qianyishen.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用于存在哪些服务器，且服务器的创建时间是多少
 */
public class Server implements Serializable {

    //记录服务器对应的主键id
    private Integer serverId;
    //用于记录服务器对应的主机名
    private String serverHostname;
    //用于记录服务器的创建时间
    private Timestamp serverTime;

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public Timestamp getServerTime() {
        return serverTime;
    }

    public void setServerTime(Timestamp serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverId=" + serverId +
                ", serverHostname='" + serverHostname + '\'' +
                ", serverTime=" + serverTime +
                '}';
    }
}
