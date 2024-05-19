package com.qianyishen.service;

import com.qianyishen.domain.User;

/**
 * 登录类
 * @author user
 */
public interface ILoginService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return user对象
     */
    User login(String username, String password);


}
