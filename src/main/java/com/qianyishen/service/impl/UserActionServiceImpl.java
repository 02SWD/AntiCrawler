package com.qianyishen.service.impl;

import com.qianyishen.dao.IFindUserDao;
import com.qianyishen.dao.IUserActionDao;
import com.qianyishen.domain.User;
import com.qianyishen.service.IUserActionService;
import com.qianyishen.utils.HashUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * 功能：
 *      用户信息的添加、修改、删除、查询全部
 * @author user
 */
@Service("userActionServiceImpl")
public class UserActionServiceImpl implements IUserActionService {

    @Resource(name = "userServiceDao")
    private IUserActionDao userActionDao;

    @Resource(name = "findUserDao")
    private IFindUserDao findUserDao;

    /**
     * 查询除system用户外的所有用户
     * @return user对象
     */
    @Override
    public List<User> findAll(){
        return userActionDao.findAll();
    }

    /**
     * 添加用户
     * @param user 封装有用户数据的对象
     * @return 成功返回 flag
     *       0 代表用户信息未填写完整(信息必须全部填写)
     *      -3 代表用户名过长，最长为20个字符
     *      -4 电话长度过长
     *      -5 密码长度过长
     *      -2 代表用户创建失败
     *      -1 代表该用户已被创建
     *       1 代表用户创建成功
     */
    @Override
    public String insertUser(User user) {
        //这个用户的创建时间由系统确定，不让用户自定义
        user.setCreateDate(new Timestamp(System.currentTimeMillis()));
        //将用户传输来的明文密码加密一下
        user.setPassword(HashUtil.sha256(user.getPassword()));
        //去掉用户名的首尾空格
        user.setUsername(user.getUsername().trim());
        String flag = "-2";
        if (user.getUsername() == null || user.getPassword() == null || user.getTelephone() == null || user.getEmail() == null){
            flag = "0";
            return flag;
        }
        if (user.getUsername().length() >20){
            flag = "-3";
            return flag;
        }
        if (user.getTelephone().length() > 20){
            flag = "-4";
            return flag;
        }
        if (user.getPassword().length() > 256){
            flag = "-5";
            return flag;
        }
        if (user.getEmail().length() > 256){
            flag = "-6";
            return flag;
        }
        if (findUserDao.findByName(user) != null){
            flag = "-1";
            return flag;
        }
        if (userActionDao.insertUser(user)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 根据用户名修改用户
     * 可以修改【密码】，【电话】，【邮箱】，但一次性只能修改一个
     * @param user 封装有用户数据的对象
     * @return 成功返回 flag
     *      -7 代码用户没有输入用户名
     *      -6 代表邮箱长度超过256
     *      -5 代表电话长度超过20
     *      -4 代表密码长度超过256
     *      -3 代表用户名长度超过20
     *      -2 代表表单一次性提交了多个信息，但是这里仅支持一次修改一个信息
     *      -1 代表修改失败
     *       1 代表修改成功
     */
    @Override
    public String updateUser(User user) {
        if ("".equals(user.getUsername())){
            return "-7";
        }
        System.out.println(user.getUsername());
        //去掉用户名的首尾空格
        user.setUsername(user.getUsername().trim());
        String flag = "-2";
        //检查用户名是否超长
        if (user.getUsername().length() > 20){
            flag = "-3";
            return flag;
        }
        if (user.getPassword() != null && user.getTelephone() == null && user.getEmail() == null){
            if (user.getPassword().length() > 256){
                flag = "-4";
                return flag;
            }
            user.setPassword(HashUtil.sha256(user.getPassword()));
            flag = userActionDao.updateUserPasswd(user) ? "1" : "-1";
            return flag;
        }else if (user.getTelephone() != null && user.getPassword() == null && user.getEmail() == null){
            if (user.getTelephone().length() > 20){
                flag = "-5";
                return flag;
            }
            flag = userActionDao.updateUserTel(user) ? "1" : "-1";
            return flag;
        }else if (user.getEmail() != null && user.getPassword() == null && user.getTelephone() == null){
            if (user.getEmail().length() > 256){
                flag = "-6";
                return flag;
            }
            flag = userActionDao.updateUserEmail(user) ? "1" : "-1";
            return flag;
        }
        return flag;
    }

    /**
     * 根据用户名删除用户
     * @param user 封装有用户数据的对象
     * @return 成功返回 flag
     *      -1 表示用户提交的是空表单
     *      -2 表示删除失败
     *      -3 表示用户名过长
     *       0 表示用户要删除的用户在数据库中不存在
     *       1 表示删除成功
     */
    @Override
    public String deleteUser(User user) {
        //去掉用户名的首尾空格
        user.setUsername(user.getUsername().trim());
        String flag;
        if (user.getUsername() == null){
            flag = "-1";
            return flag;
        }
        if (user.getUsername().length() > 20){
            flag = "-3";
            return flag;
        }
        if (findUserDao.findByName(user) == null){
            flag = "0";
            return flag;
        }
        //删除用户，若删除成功则 flag="1"，删除失败 flag="-2"
        flag = userActionDao.deleteUser(user) ? "1" : "-2";
        return flag;
    }
}
