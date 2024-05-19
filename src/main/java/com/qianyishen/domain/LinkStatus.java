package com.qianyishen.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用来封装【链路流量】（包括全部的用户流量、爬虫流量）
 */
@Component("linkStatus")
@Scope("prototype")
public class LinkStatus {

    private String flag;                //用于表示查询是否成功
    private List<Integer> RawCount;     //用于封装 [某一天内每个小时 或 某一天中、某一小时的每5分钟的数据量] 的请求量集合
    private List<Integer> reptileCount; //用于封装 [某一天内每个小时 或 某一天中、某一小时的每5分钟的数据量] 的爬虫IP请求量集合
    private String msg;                 //用于封装错误信息（在flag不为1时才有用）

    public List<Integer> getRawCount() {
        return RawCount;
    }

    public void setRawCount(List<Integer> rawCount) {
        RawCount = rawCount;
    }

    public List<Integer> getReptileCount() {
        return reptileCount;
    }

    public void setReptileCount(List<Integer> reptileCount) {
        this.reptileCount = reptileCount;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "LinkStatus{" +
                "flag='" + flag + '\'' +
                ", RawCount=" + RawCount +
                ", reptileCount=" + reptileCount +
                ", msg='" + msg + '\'' +
                '}';
    }
}
