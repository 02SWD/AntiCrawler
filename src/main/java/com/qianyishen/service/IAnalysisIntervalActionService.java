package com.qianyishen.service;


import com.qianyishen.domain.AnalysisInterval;

import java.util.List;

/**
 * [获取原始数据的时间间隔]操作
 *  查询、修改
 *  "获取原始数据的时间间隔"的作用：查询 now()-时间间隔 ~ now() 之内的原始数据，单位：秒
 */
public interface IAnalysisIntervalActionService {

    /**
     * 查询 “获取原始数据的时间间隔”
     * @return 返回时间间隔
     */
    List<AnalysisInterval> findTimeInterval();

    /**
     * 修改 “获取原始数据的时间间隔”
     * @param analysisInterval 时间间隔
     * @return  -2 代表用户输入了 <=30 的数（这是不允许的）
     *          -1 代表修改失败
     *           0 代表用户没有输入
     *           1 代表修改成功
     */
    String updateTimeInterval(AnalysisInterval analysisInterval);








}
