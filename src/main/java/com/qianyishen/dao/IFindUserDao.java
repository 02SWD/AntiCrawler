package com.qianyishen.dao;

import com.qianyishen.domain.User;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 功能：
 *      根据用户名和密码查询用户，仅根据用户名查询用户
 * @author User
 */
@Repository("findUserDao")
public interface IFindUserDao {

    /**
     * 根据用户名和密码查询
     * @param user 封装有用户名和密码的用户数据
     * @return 返回查询的对象
     */
    @Select("select * from yishen_user where username=#{username} and password=#{password}")
    @Results(id = "userMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "telephone", property = "telephone"),
            @Result(column = "email", property = "email"),
            @Result(column = "createDate", property = "createDate")
    })
    User findUserByNameAndPasswd(User user);

    /**
     * 根据用户名查询
     * @param user 含有用户名的user对象
     * @return 查询出的user
     */
    @Select("select * from yishen_user where username=#{username}")
    User findByName(User user);

}
