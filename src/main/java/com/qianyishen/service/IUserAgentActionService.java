package com.qianyishen.service;

import com.qianyishen.domain.UserAgent;

import java.util.List;

/**
 * userAgent操作类
 *      查看所有，添加，删除
 * @author user
 */
public interface IUserAgentActionService {

    /**
     * 查询所有useragent
     * @return 返回所有查询结果
     */
    List<UserAgent> findAllUa();

    /**
     * 添加useragent
     * @param userAgent 封装有useragent正则信息的对象
     * @return 返回 -1 0 1
     */
    String insertUa(UserAgent userAgent);

    /**
     * 删除useragent
     * @param userAgent 封装有useragent id信息的对象
     * @return 返回 -1 0 1
     */
    String deleteUa(UserAgent userAgent);








}
