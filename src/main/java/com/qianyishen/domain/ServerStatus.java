package com.qianyishen.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用于记录服务器的状态信息
 */
public class ServerStatus  implements Serializable {

    //用于记录状态信息的主键id
    private Integer serId;
    //用于记录服务器的cpu占用率
    private Float serCpu;
    //用于记录服务器的内存占用率
    private Float serMemory;
    //用于记录服务器的磁盘占用率
    private Float serDisk;
    //用于记录服务器的磁盘IO率
    private Float serDiskIo;
    //该服务器状态的时间
    private Timestamp serCreateTime;
    //用于记录该服务器状态对应的服务器主键id
    private Integer serForeignId;

    public Integer getSerId() {
        return serId;
    }

    public void setSerId(Integer serId) {
        this.serId = serId;
    }

    public Float getSerCpu() {
        return serCpu;
    }

    public void setSerCpu(Float serCpu) {
        this.serCpu = serCpu;
    }

    public Float getSerMemory() {
        return serMemory;
    }

    public void setSerMemory(Float serMemory) {
        this.serMemory = serMemory;
    }

    public Float getSerDisk() {
        return serDisk;
    }

    public void setSerDisk(Float serDisk) {
        this.serDisk = serDisk;
    }

    public Float getSerDiskIo() {
        return serDiskIo;
    }

    public void setSerDiskIo(Float serDiskIo) {
        this.serDiskIo = serDiskIo;
    }

    public Integer getSerForeignId() {
        return serForeignId;
    }

    public void setSerForeignId(Integer serForeignId) {
        this.serForeignId = serForeignId;
    }

    public Timestamp getSerCreateTime() {
        return serCreateTime;
    }

    public void setSerCreateTime(Timestamp serCreateTime) {
        this.serCreateTime = serCreateTime;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "serId=" + serId +
                ", serCpu=" + serCpu +
                ", serMemory=" + serMemory +
                ", serDisk=" + serDisk +
                ", serDiskIo=" + serDiskIo +
                ", serCreateTime=" + serCreateTime +
                ", serForeignId=" + serForeignId +
                '}';
    }
}
