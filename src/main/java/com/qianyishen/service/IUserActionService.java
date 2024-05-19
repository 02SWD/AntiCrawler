package com.qianyishen.service;

import com.qianyishen.domain.User;

import java.util.List;

/**
 * 功能：
 *      用户的添加、修改、删除、查询全部
 * @author user
 */
public interface IUserActionService {

    /**
     * 查询除system用户外的所有用户
     * @return user对象的列表集合
     */
    List<User> findAll();

    /**
     * 添加用户
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    String insertUser(User user);

    /**
     * 修改用户
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    String updateUser(User user);

    /**
     * 删除用户
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    String deleteUser(User user);

}
