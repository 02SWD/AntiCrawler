package com.qianyishen.domain;

/**
 * 用于封装【process策略中，ipUserAgent规则分析】所需要的原始数据
 */
public class ProcessFilterIpUserAgent {

    //要分析的ip
    private String ip;
    //x分钟内，某ip的useragent种类数
    private Integer userAgentCount;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getUserAgentCount() {
        return userAgentCount;
    }

    public void setUserAgentCount(Integer userAgentCount) {
        this.userAgentCount = userAgentCount;
    }

    @Override
    public String toString() {
        return "ProcessFilterIpUserAgent{" +
                "ip='" + ip + '\'' +
                ", userAgentCount=" + userAgentCount +
                '}';
    }
}
