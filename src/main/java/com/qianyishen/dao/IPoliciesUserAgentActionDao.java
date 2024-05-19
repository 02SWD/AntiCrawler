package com.qianyishen.dao;

import com.qianyishen.domain.UserAgent;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能：
 *      用于UserAgent策略的创建、查询、删除
 * @author user
 */
@Repository("policiesUserAgentAction")
public interface IPoliciesUserAgentActionDao {

    /**
     * 查找全部UA
     * @return 返回所有useragent对象
     */
    @Select("select * from yishen_useragent")
    @Results(id = "uaMap", value = {
            @Result(id = true, column = "ua_id", property = "userAgentId"),
            @Result(column = "ua_pattern", property = "userAgentPattern")
    })
    List<UserAgent> findAllUa();

    /**
     * 插入UA
     * @param userAgent 封装有useragent信息的对象
     * @return 成功返回true
     */
    @Insert("insert into yishen_useragent (ua_pattern) values (#{userAgentPattern})")
    boolean insertUa(UserAgent userAgent);

    /**
     * 删除UA
     * @param userAgent 封装有useragent id信息的对象
     * @return 成功返回true
     */
    @Delete("delete from yishen_useragent where ua_id = #{userAgentId}")
    boolean deleteUa(UserAgent userAgent);





}
