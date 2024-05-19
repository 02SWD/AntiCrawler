package com.qianyishen.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用于封装 yishen_blackIp 表的数据的实体类
 */
public class BlackIp implements Serializable {

    private Integer blackId;
    private String blackIp;
    private Timestamp blackTimeStart;   //该ip加入黑名单时的时间
    private Timestamp blackTime;        //直到该时间时，ip解封

    public Integer getBlackId() {
        return blackId;
    }

    public void setBlackId(Integer blackId) {
        this.blackId = blackId;
    }

    public String getBlackIp() {
        return blackIp;
    }

    public void setBlackIp(String blackIp) {
        this.blackIp = blackIp;
    }

    public Timestamp getBlackTimeStart() {
        return blackTimeStart;
    }

    public void setBlackTimeStart(Timestamp blackTimeStart) {
        this.blackTimeStart = blackTimeStart;
    }

    public Timestamp getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(Timestamp blackTime) {
        this.blackTime = blackTime;
    }

    @Override
    public String toString() {
        return "BlackIp{" +
                "blackId=" + blackId +
                ", blackIp='" + blackIp + '\'' +
                ", blackTimeStart=" + blackTimeStart +
                ", blackTime=" + blackTime +
                '}';
    }
}
