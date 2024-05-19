package com.qianyishen.dao;

import com.qianyishen.domain.BlackTime;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本dao提供 封禁时间间隔 的查询和修改（不提供添加，删除，因为封禁时间间隔唯一且不为空）
 */
@Repository("blackTimeActionDao")
public interface IBlackTimeActionDao {

    /**
     * 查询封禁的时间间隔
     * @return 返回封禁时间间隔
     */
    @Select("select * from yishen_blackTime")
    @Results(id = "blackTimeMap",value = {
            @Result(id = true, column = "black_id", property = "blackId"),
            @Result(column = "black_timeInterval",property = "blackTimeInterval")
    })
    List<BlackTime> findBlackTime();

    /**
     * 修改封禁时间间隔
     * @param blackTime 将要修改的时间间隔
     * @return 修改成功返回true
     */
    @Update("update yishen_blackTime set black_timeInterval=#{blackTimeInterval}")
    boolean updateBlackTime(BlackTime blackTime);


















}
