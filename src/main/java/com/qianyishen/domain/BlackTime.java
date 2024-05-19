package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 用于封装yishen_blackTime表的实体类（封装封禁时间间隔信息的实体类）
 */
public class BlackTime implements Serializable {

    private Integer blackId;
    private Integer blackTimeInterval;

    public Integer getBlackId() {
        return blackId;
    }

    public void setBlackId(Integer blackId) {
        this.blackId = blackId;
    }

    public Integer getBlackTimeInterval() {
        return blackTimeInterval;
    }

    public void setBlackTimeInterval(Integer blackTimeInterval) {
        this.blackTimeInterval = blackTimeInterval;
    }

    @Override
    public String toString() {
        return "BlackTime{" +
                "blackId=" + blackId +
                ", blackTimeInterval=" + blackTimeInterval +
                '}';
    }
}
