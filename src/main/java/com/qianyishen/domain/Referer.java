package com.qianyishen.domain;

import java.io.Serializable;

/**
 * referer正则实体类
 * @author user
 */
public class Referer implements Serializable {

    private Integer refId;     //referer正则的主键id
    private String refPattern; //referer正则

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getRefPattern() {
        return refPattern;
    }

    public void setRefPattern(String refPattern) {
        this.refPattern = refPattern;
    }

    @Override
    public String toString() {
        return "Referer{" +
                "refId=" + refId +
                ", refPattern='" + refPattern + '\'' +
                '}';
    }
}
