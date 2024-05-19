package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 用于封装原始数据的时间间隔（即：查询 now()-intervalTime ~ now() 之内的原始数据，单位：秒）
 */
public class AnalysisInterval implements Serializable {

    private Integer intervalId;
    private Integer intervalTime;

    public Integer getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(Integer intervalId) {
        this.intervalId = intervalId;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    @Override
    public String toString() {
        return "AnalysisInterval{" +
                "intervalId=" + intervalId +
                ", intervalTime=" + intervalTime +
                '}';
    }
}
