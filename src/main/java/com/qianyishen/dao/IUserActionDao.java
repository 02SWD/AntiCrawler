package com.qianyishen.dao;

import com.qianyishen.domain.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能：
 *      用户信息的添加、修改、删除、查询全部
 * @author user
 */
@Repository("userServiceDao")
public interface IUserActionDao {

    /**
     * 查询除了system外的所有用户
     * @return user对象的list集合
     */
    @Select("select username,telephone,email,createDate from yishen_user where username != 'system'")
    List<User> findAll();

    /**
     * 添加用户
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    @Insert("insert into yishen_user(username,password,telephone,email,createDate) values (#{username},#{password},#{telephone},#{email},#{createDate})")
    boolean insertUser(User user);

    /**
     * 根据用户名修改用户密码
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    @Update("update yishen_user set password=#{password} where username=#{username}")
    boolean updateUserPasswd(User user);

    /**
     * 根据用户名修改用户电话
     * 由于使用动态查询操作来修改用户信息太过麻烦，所以这里我对用户的密码，电话，邮箱的修改操作分离开来，单独修改
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    @Update("update yishen_user set telephone=#{telephone} where username=#{username}")
    boolean updateUserTel(User user);

    /**
     * 根据用户名修改用户邮件
     * 由于使用动态查询操作来修改用户信息太过麻烦，所以这里我对用户的密码，电话，邮箱的修改操作分离开来，单独修改
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    @Update("update yishen_user set email=#{email} where username=#{username}")
    boolean updateUserEmail(User user);

    /**
     * 根据用户名删除用户
     * @param user 封装有用户数据的对象
     * @return 成功返回true
     */
    @Delete("delete from yishen_user where username=#{username}")
    boolean deleteUser(User user);
}
