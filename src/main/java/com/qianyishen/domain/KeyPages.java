package com.qianyishen.domain;

import java.io.Serializable;

/**
 * 关键页实体类
 *      用于封装关键页面正则
 */
public class KeyPages implements Serializable {

    private Integer keyPagesId;
    private String keyPagesPattern;

    public Integer getKeyPagesId() {
        return keyPagesId;
    }

    public void setKeyPagesId(Integer keyPagesId) {
        this.keyPagesId = keyPagesId;
    }

    public String getKeyPagesPattern() {
        return keyPagesPattern;
    }

    public void setKeyPagesPattern(String keyPagesPattern) {
        this.keyPagesPattern = keyPagesPattern;
    }

    @Override
    public String toString() {
        return "KeyPages{" +
                "keyPagesId=" + keyPagesId +
                ", keyPagesPattern='" + keyPagesPattern + '\'' +
                '}';
    }
}
