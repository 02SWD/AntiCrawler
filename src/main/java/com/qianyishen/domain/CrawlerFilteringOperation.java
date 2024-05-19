package com.qianyishen.domain;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * 用于封装原始数据的初始筛选条件（即在获取原始数据时，先在mysql中做一次“预处理”，以减少正式分析时的数据量）
 */
@Component("crawlerFilteringOperation")
public class CrawlerFilteringOperation implements Serializable {

    //用于存储ip黑名单
    private List<BlackIp> blackIpList;

    //用于存储【获取原始数据的时间间隔】（即：获取 now()-AnalysisInterval ~ now() 时间之内的原始数据，单位：秒）
    private Integer analysisInterval;

    //用于存储当前的时间（该变量只应用于process策略分析，不应用于RefererFilterServiceImpl与UserAgentFilterServiceImpl，这是由process策略实现方式而决定的）
    private String nowDate;

    public List<BlackIp> getBlackIpList() {
        return blackIpList;
    }

    public void setBlackIpList(List<BlackIp> blackIpList) {
        this.blackIpList = blackIpList;
    }

    public Integer getAnalysisInterval() {
        return analysisInterval;
    }

    public void setAnalysisInterval(Integer analysisInterval) {
        this.analysisInterval = analysisInterval;
    }

    public String getNowDate() {
        return nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }


    @Override
    public String toString() {
        return "CrawlerFilteringOperation{" +
                "blackIpList=" + blackIpList +
                ", analysisInterval=" + analysisInterval +
                ", nowDate='" + nowDate + '\'' +
                '}';
    }
}
