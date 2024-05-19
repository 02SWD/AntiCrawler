package com.qianyishen.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程实体类
 * @author user
 */
public class Process implements Serializable {

    private Integer proId;                    //流程id主键
    private String proName;                   //流程名
    private Timestamp proDate;                //流程创建的时间
    private Integer proIpSum;                 //x分钟内，某ip访问总量
    private Integer proIpSumThreshold;        //x分钟内，某ip访问总量的阈值
    private Integer proIpKeySum;              //x分钟内，某ip对于关键页面的访问总量
    private Integer proIpKeySumThreshold;     //x分钟内，某ip对于关键页面的访问总量的阈值
    private Integer proIpInterval;            //x分钟内，某ip访问间隔
    private Integer proIpIntervalThreshold;   //x分钟内，某ip访问间隔的阈值
    private Integer proIpKeyInterval;         //x分钟内，某ip对关键页面的访问间隔
    private Integer proIpKeyIntervalThreshold;//x分钟内，某ip对关键页面的访问间隔的阈值
    private Integer proIpUserAgent;           //x分钟内，某ip的useragent种类数
    private Integer proIpUserAgentThreshold;  //x分钟内，某ip的useragent种类数的阈值
    private Integer proFinalThreshold;        //最终阈值
    private Integer proEnableId;              //1：启用，0：未启用

    public Integer getProId() {
        return proId;
    }

    public void setProId(Integer proId) {
        this.proId = proId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Timestamp getProDate() {
        return proDate;
    }

    public void setProDate(Timestamp proDate) {
        this.proDate = proDate;
    }

    public Integer getProIpSum() {
        return proIpSum;
    }

    public void setProIpSum(Integer proIpSum) {
        this.proIpSum = proIpSum;
    }

    public Integer getProIpSumThreshold() {
        return proIpSumThreshold;
    }

    public void setProIpSumThreshold(Integer proIpSumThreshold) {
        this.proIpSumThreshold = proIpSumThreshold;
    }

    public Integer getProIpKeySum() {
        return proIpKeySum;
    }

    public void setProIpKeySum(Integer proIpKeySum) {
        this.proIpKeySum = proIpKeySum;
    }

    public Integer getProIpKeySumThreshold() {
        return proIpKeySumThreshold;
    }

    public void setProIpKeySumThreshold(Integer proIpKeySumThreshold) {
        this.proIpKeySumThreshold = proIpKeySumThreshold;
    }

    public Integer getProIpInterval() {
        return proIpInterval;
    }

    public void setProIpInterval(Integer proIpInterval) {
        this.proIpInterval = proIpInterval;
    }

    public Integer getProIpIntervalThreshold() {
        return proIpIntervalThreshold;
    }

    public void setProIpIntervalThreshold(Integer proIpIntervalThreshold) {
        this.proIpIntervalThreshold = proIpIntervalThreshold;
    }

    public Integer getProIpKeyInterval() {
        return proIpKeyInterval;
    }

    public void setProIpKeyInterval(Integer proIpKeyInterval) {
        this.proIpKeyInterval = proIpKeyInterval;
    }

    public Integer getProIpKeyIntervalThreshold() {
        return proIpKeyIntervalThreshold;
    }

    public void setProIpKeyIntervalThreshold(Integer proIpKeyIntervalThreshold) {
        this.proIpKeyIntervalThreshold = proIpKeyIntervalThreshold;
    }

    public Integer getProIpUserAgent() {
        return proIpUserAgent;
    }

    public void setProIpUserAgent(Integer proIpUserAgent) {
        this.proIpUserAgent = proIpUserAgent;
    }

    public Integer getProIpUserAgentThreshold() {
        return proIpUserAgentThreshold;
    }

    public void setProIpUserAgentThreshold(Integer proIpUserAgentThreshold) {
        this.proIpUserAgentThreshold = proIpUserAgentThreshold;
    }

    public Integer getProFinalThreshold() {
        return proFinalThreshold;
    }

    public void setProFinalThreshold(Integer proFinalThreshold) {
        this.proFinalThreshold = proFinalThreshold;
    }

    public Integer getProEnableId() {
        return proEnableId;
    }

    public void setProEnableId(Integer proEnableId) {
        this.proEnableId = proEnableId;
    }

    @Override
    public String toString() {
        return "Process{" +
                "proId=" + proId +
                ", proName='" + proName + '\'' +
                ", proDate=" + proDate +
                ", proIpSum=" + proIpSum +
                ", proIpSumThreshold=" + proIpSumThreshold +
                ", proIpKeySum=" + proIpKeySum +
                ", proIpKeySumThreshold=" + proIpKeySumThreshold +
                ", proIpInterval=" + proIpInterval +
                ", proIpIntervalThreshold=" + proIpIntervalThreshold +
                ", proIpKeyInterval=" + proIpKeyInterval +
                ", proIpKeyIntervalThreshold=" + proIpKeyIntervalThreshold +
                ", proIpUserAgent=" + proIpUserAgent +
                ", proIpUserAgentThreshold=" + proIpUserAgentThreshold +
                ", proFinalThreshold=" + proFinalThreshold +
                ", proEnableId=" + proEnableId +
                '}';
    }
}
