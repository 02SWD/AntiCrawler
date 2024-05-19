package com.qianyishen.dao;

import com.qianyishen.domain.Referer;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能：
 *      用于referer策略的创建，查询，删除
 * @author user
 */
@Repository("policiesRefererAction")
public interface IPoliciesRefererActionDao {

    /**
     * 查询所有referer
     * @return 成功则返回true
     */
    @Select("select * from yishen_referer")
    @Results(id = "refererMap", value = {
            @Result(id = true, column = "ref_id", property = "refId"),
            @Result(column = "ref_pattern", property = "refPattern")
    })
    List<Referer> findAllRef();

    /**
     * 插入referer
     * @param referer 封装有referer信息的实体类
     * @return 成功则返回true
     */
    @Insert("insert into yishen_referer (ref_pattern) values (#{refPattern})")
    boolean insertReferer(Referer referer);

    /**
     * 删除referer
     * @param referer 封装有referer id信息的实体类
     * @return 成功则返回true
     */
    @Delete("delete from yishen_referer where ref_id = #{refId}")
    boolean deleteReferer(Referer referer);









}
