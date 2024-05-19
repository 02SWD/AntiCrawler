package com.qianyishen.service;

import com.qianyishen.domain.Referer;

import java.util.List;

/**
 * referer操作类
 *      查看所有，添加，删除
 * @author user
 */
public interface IRefererActionService {

    /**
     * 查询所有referer
     * @return 返回所有查询结果
     */
    List<Referer> findAllRef();

    /**
     * 插入referer规则
     * @param referer 封装有referer规则的对象
     * @return 返回 -1 0 1
     */
    String insertReferer(Referer referer);

    /**
     * 删除referer规则
     * @param referer 封装有refererId的对象
     * @return 返回 -1 0 1
     */
    String deleteReferer(Referer referer);


}
