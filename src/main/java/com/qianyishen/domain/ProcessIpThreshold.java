package com.qianyishen.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 该实体类应用于 process策略 分析
 *
 * 用于记录，具有嫌疑的ip及所对应的阈值
 */
@Component("processIpThreshold")
@Scope("prototype")
public class ProcessIpThreshold implements Serializable {

    //具有嫌疑的ip
    private String suspectIp;
    //嫌疑ip的阈值
    private Integer suspectIpThreshold;


    public String getSuspectIp() {
        return suspectIp;
    }

    public void setSuspectIp(String suspectIp) {
        this.suspectIp = suspectIp;
    }

    public Integer getSuspectIpThreshold() {
        return suspectIpThreshold;
    }

    public void setSuspectIpThreshold(Integer suspectIpThreshold) {
        this.suspectIpThreshold = suspectIpThreshold;
    }

    @Override
    public String toString() {
        return "ProcessIpThreshold{" +
                "suspectIp='" + suspectIp + '\'' +
                ", suspectIpThreshold=" + suspectIpThreshold +
                '}';
    }
}
