package com.qianyishen.service;

import com.qianyishen.domain.KeyPages;

import java.util.List;

/**
 *该service用于操作关键页面的正则数据 （操作 yishen_keyPages）
 *      查询所有，添加，删除
 */
public interface IKeyPagesActionService {

    /**
     * 查询所有 关键页面的正则
     * @return 返回关键页面的正则
     */
    List<KeyPages> findAllKeyPagesPattern();

    /**
     * 添加关键页面的正则
     * @param keyPages 封装有关键页面正则数据的实体类
     * @return -1 代表添加失败
     *          0 代表用户输入为空
     *          1 代表添加成功
     */
    String insertKeyPagesPattern(KeyPages keyPages);

    /**
     * 根据主键id删除关键页面正则
     * @param keyPages 封装有 将被删除的页面正则对应的 主键id值
     * @return -1 代表插入失败
     *          0 代表用户输入为空
     *          1 代表删除成功
     */
    String deleteKeyPagesPattern(KeyPages keyPages);











}
