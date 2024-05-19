package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 用于封装【process策略中，ipKeyInterval规则分析】所需要的原始数据
 */
public class ProcessFilterIpKeyInterval implements Serializable {

    //要分析的ip
    private String ip;
    //x分钟内，ip访问的URI
    private String uri;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "ProcessFilterIpKeyInterval{" +
                "ip='" + ip + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
