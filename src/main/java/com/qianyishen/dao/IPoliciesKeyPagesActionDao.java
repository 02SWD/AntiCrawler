package com.qianyishen.dao;

import com.qianyishen.domain.KeyPages;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *该dao用于操作关键页面的正则数据 （操作 yishen_keyPages）
 *      查询所有，添加，删除
 */
@Repository("keyPagesActionDao")
public interface IPoliciesKeyPagesActionDao {

    /**
     * 查询所有 关键页面的正则
     * @return 返回关键页面的正则
     */
    @Select("select * from yishen_keyPages")
    @Results(id = "keyPagesMap", value = {
            @Result(id = true, column = "keyPages_id", property = "keyPagesId"),
            @Result(column = "keyPages_pattern", property = "keyPagesPattern")
    })
    List<KeyPages> findAllKeyPagesPattern();

    /**
     * 添加关键页面的正则
     * @param keyPages 封装有关键页面正则数据的实体类
     * @return 添加成功返回true
     */
    @Insert("insert into yishen_keyPages (keyPages_pattern) values (#{keyPagesPattern})")
    boolean insertKeyPagesPattern(KeyPages keyPages);

    /**
     * 删除关键页面正则
     * @param keyPages 封装有 将被删除的页面正则对应的 主键id值
     * @return 删除成功则返回true
     */
    @Delete("delete from yishen_keyPages where keyPages_id = #{keyPagesId}")
    boolean deleteKeyPagesPattern(KeyPages keyPages);








}
