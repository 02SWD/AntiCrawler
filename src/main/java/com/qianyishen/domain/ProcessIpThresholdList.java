package com.qianyishen.domain;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用于封装ProcessIpThreshold实体类
 */
@Component("processIpThresholdList")
public class ProcessIpThresholdList {

    //用于记录，具有嫌疑的ip所对应的阈值 的实体类
    private List<ProcessIpThreshold> processIpThreshold;

    public List<ProcessIpThreshold> getProcessIpThreshold() {
        return processIpThreshold;
    }

    public void setProcessIpThreshold(List<ProcessIpThreshold> processIpThreshold) {
        this.processIpThreshold = processIpThreshold;
    }

    @Override
    public String toString() {
        return "ProcessIpThresholdList{" +
                "processIpThreshold=" + processIpThreshold +
                '}';
    }
}
