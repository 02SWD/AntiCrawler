package com.qianyishen.service;

import com.qianyishen.domain.BlackTime;

import java.util.List;

/**
 * 封禁时间操作
 *  查询、修改
 */
public interface IBlackTimeActionService {

    /**
     * 查询目前所设置的封禁时间
     * @return 返回查询的封禁时间
     */
    List<BlackTime> findBlackTime();

    /**
     * 修改封禁时间
     * @param blackTime 封装有将要被修改成的封禁时间
     * @return -2 代表用户输入了 <=0 的数（这是不允许的）
     *         -1 代表修改失败
     *          0 代表用户没有输入提交
     *          1 代表修改成功
     *
     */
    String updateBlackTime(BlackTime blackTime);















}
