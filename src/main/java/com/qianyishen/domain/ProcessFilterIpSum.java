package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 用于封装【process策略中，ipSum规则分析】所需要的原始数据
 */
public class ProcessFilterIpSum implements Serializable {

    private String ip;    //要分析的ip
    private Integer count;//x分钟内，ip出现的次数

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ProcessFilterIpSum{" +
                "ip='" + ip + '\'' +
                ", count=" + count +
                '}';
    }
}
