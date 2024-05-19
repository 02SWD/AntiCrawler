package com.qianyishen.dao;

import com.qianyishen.domain.AnalysisInterval;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于操作 yishen_AnalysisInterval 表
 *      用于“查询，修改”[获取原始数据的时间间隔]（不提供添加，删除，因为 获取原始数据的时间间隔 唯一且不为空）
 *      "获取原始数据的时间间隔"的作用：
 *          查询 now()-时间间隔 ~ now() 之内的原始数据，单位：秒
 */
@Repository("analysisIntervalActionDao")
public interface IAnalysisIntervalActionDao {

    /**
     * 查询 获取原始数据的时间间隔
     * @return 时间间隔
     */
    @Select("select * from yishen_analysisinterval")
    @Results(id = "timeIntervalMap",value = {
            @Result(id = true, column = "interval_id", property = "intervalId"),
            @Result(column = "interval_time", property = "intervalTime")
    })
    List<AnalysisInterval> findTimeInterval();

    /**
     * 修改 获取原始数据的时间间隔
     * @param analysisInterval 封装有将要修改至的时间间隔
     * @return 修改成功返回true
     */
    @Update("update yishen_analysisinterval set interval_time=#{intervalTime}")
    boolean updateTimeInterval(AnalysisInterval analysisInterval);

}
