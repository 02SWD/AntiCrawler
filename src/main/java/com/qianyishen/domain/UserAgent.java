package com.qianyishen.domain;

import java.io.Serializable;

/**
 * UserAgent正则实体类
 * @author user
 */
public class UserAgent implements Serializable {

    private Integer userAgentId;     //userAgent正则的主键id
    private String userAgentPattern; //userAgent正则

    public Integer getUserAgentId() {
        return userAgentId;
    }

    public void setUserAgentId(Integer userAgentId) {
        this.userAgentId = userAgentId;
    }

    public String getUserAgentPattern() {
        return userAgentPattern;
    }

    public void setUserAgentPattern(String userAgentPattern) {
        this.userAgentPattern = userAgentPattern;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "userAgentId=" + userAgentId +
                ", userAgentPattern='" + userAgentPattern + '\'' +
                '}';
    }
}
