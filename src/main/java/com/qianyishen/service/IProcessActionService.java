package com.qianyishen.service;

import com.qianyishen.domain.Process;

import java.util.List;

/**
 * 流程策略操作，查询所有，添加，删除
 * @author user
 */
public interface IProcessActionService {

    /**
     * 查询所有流程策略
     * @return 策略集合
     */
    List<Process> findAllPro();

    /**
     * 添加流程策略
     * @param process 封装有流程策略的对象
     * @return 返回
     *          -2 代表该process策略名已经存在
     *          -1 代表添加失败
     *           0 代表用户输入不完全
     *           1 代表添加成功
     */
    String insertPro(Process process);

    /**
     * 根据策略名删除process策略
     * @param process 封装有流程id的对象
     * @return 返回
     *          -3 代表要删除的策略正在启用，不允许删除
     *          -2 代表要删除的策略不存在
     *          -1 代表删除失败
     *           0 代表用户没有输入要删除的id
     *           1 代表删除成功
     */
    String deletePro(Process process);

    /**
     * 根据 策略名 启用process策略
     * process策略只能启用一个，所以如果当前process策略1正在启用，而你想启用process策略2的话，要先关闭process策略1
     * @param process 封装有将要启用的process策略id
     * @return -2 代表用户想要删除的process策略不存在
     *         -1 代表启用失败
     *          0 代表用户没有输入
     *          1 代表启用成功
     */
    String enableProcess(Process process);
}
