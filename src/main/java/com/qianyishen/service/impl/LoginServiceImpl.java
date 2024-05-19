package com.qianyishen.service.impl;

import com.qianyishen.dao.IFindUserDao;
import com.qianyishen.domain.User;
import com.qianyishen.service.ILoginService;
import com.qianyishen.utils.HashUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登录类
 * @author user
 */
@Service("loginService")
public class LoginServiceImpl implements ILoginService {

    /**
     * 获取findUserByNameDao
     */
    @Resource(name = "findUserDao")
    private IFindUserDao findUserDao;

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return user对象
     */
    @Override
    public User login(String username, String password) {
        //由于密码在数据库中是以sha256加密后存储的，所以这里要加密password一下再进行封装，以便dao层进行比较
        String sha256 = HashUtil.sha256(password);
        //将用户名，密码封装到user中（由于user对象的其他字段用不到，所以这里没有进行封装）
        User user = new User();
        user.setUsername(username);
        user.setPassword(sha256);
        return findUserDao.findUserByNameAndPasswd(user);
    }
}
