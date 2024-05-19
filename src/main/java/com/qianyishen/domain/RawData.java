package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 封装原始数据的类
 */
public class RawData implements Serializable {

    private Integer rawId;
    private String rawRemoteAddress;
    private String rawRequestMethod;
    private String rawServerAddress;
    private String rawReferer;
    private String rawUserAgent;
    private String rawUri;
    private String rawRequest;
    private String rawRequestBody;
    private String rawLocalTime;

    public Integer getRawId() {
        return rawId;
    }

    public void setRawId(Integer rawId) {
        this.rawId = rawId;
    }

    public String getRawRemoteAddress() {
        return rawRemoteAddress;
    }

    public void setRawRemoteAddress(String rawRemoteAddress) {
        this.rawRemoteAddress = rawRemoteAddress;
    }

    public String getRawRequestMethod() {
        return rawRequestMethod;
    }

    public void setRawRequestMethod(String rawRequestMethod) {
        this.rawRequestMethod = rawRequestMethod;
    }

    public String getRawServerAddress() {
        return rawServerAddress;
    }

    public void setRawServerAddress(String rawServerAddress) {
        this.rawServerAddress = rawServerAddress;
    }

    public String getRawReferer() {
        return rawReferer;
    }

    public void setRawReferer(String rawReferer) {
        this.rawReferer = rawReferer;
    }

    public String getRawUserAgent() {
        return rawUserAgent;
    }

    public void setRawUserAgent(String rawUserAgent) {
        this.rawUserAgent = rawUserAgent;
    }

    public String getRawUri() {
        return rawUri;
    }

    public void setRawUri(String rawUri) {
        this.rawUri = rawUri;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getRawRequestBody() {
        return rawRequestBody;
    }

    public void setRawRequestBody(String rawRequestBody) {
        this.rawRequestBody = rawRequestBody;
    }

    public String getRawLocalTime() {
        return rawLocalTime;
    }

    public void setRawLocalTime(String rawLocalTime) {
        this.rawLocalTime = rawLocalTime;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "rawId=" + rawId +
                ", rawRemoteAddress='" + rawRemoteAddress + '\'' +
                ", rawRequestMethod='" + rawRequestMethod + '\'' +
                ", rawServerAddress='" + rawServerAddress + '\'' +
                ", rawReferer='" + rawReferer + '\'' +
                ", rawUserAgent='" + rawUserAgent + '\'' +
                ", rawUri='" + rawUri + '\'' +
                ", rawRequest='" + rawRequest + '\'' +
                ", rawRequestBody='" + rawRequestBody + '\'' +
                ", rawLocalTime='" + rawLocalTime + '\'' +
                '}';
    }
}
