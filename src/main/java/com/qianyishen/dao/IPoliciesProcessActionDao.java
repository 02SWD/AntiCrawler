package com.qianyishen.dao;

import com.qianyishen.domain.Process;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能：
 *  用于流程策略的创建、查询All、查询已启用的process、查询ByName、删除byId、启用/停止
 *  但是不支持：修改process策略信息
 * @author user
 */
@Repository("policiesProcessActionDao")
public interface IPoliciesProcessActionDao {

    /**
     * 查找所有流程信息
     * @return 流程信息list集合
     */
    @Select("select * from yishen_process")
    @Results(id = "processMap", value = {
            @Result(id = true, column = "pro_id", property = "proId"),
            @Result(column = "pro_name", property = "proName"),
            @Result(column = "pro_date", property = "proDate"),
            @Result(column = "pro_ipSum", property = "proIpSum"),
            @Result(column = "pro_ipSumThreshold", property = "proIpSumThreshold"),
            @Result(column = "pro_ipKeysum", property = "proIpKeySum"),
            @Result(column = "pro_ipKeysumThreshold", property = "proIpKeySumThreshold"),
            @Result(column = "pro_ipInterval", property = "proIpInterval"),
            @Result(column = "pro_ipIntervalThreshold", property = "proIpIntervalThreshold"),
            @Result(column = "pro_ipKeyinterval", property = "proIpKeyInterval"),
            @Result(column = "pro_ipKeyintervalThreshold", property = "proIpKeyIntervalThreshold"),
            @Result(column = "pro_ipUseragent", property = "proIpUserAgent"),
            @Result(column = "pro_ipUseragentThreshold", property = "proIpUserAgentThreshold"),
            @Result(column = "pro_finalThreshold", property = "proFinalThreshold"),
            @Result(column = "pro_enable", property = "proEnableId")
    })
    List<Process> findAllPro();

    /**
     * 根据process策略名查找
     * @param processName process策略名
     * @return 所要查找的策略信息
     */
    @Select("select * from yishen_process where pro_name = #{processName}")
    @ResultMap("processMap")
    Process findProByName(String processName);

    /**
     * 查询已经启用的process策略
     * @return 返回已被启用的process策略的list集合（这里虽然使用的是集合，但实际上集合中只有一个元素，因为只能启用一个process策略）
     */
    @Select("select * from yishen_process where pro_enable = 1")
    @ResultMap("processMap")
    List<Process> findEnableProcess();

    /**
     * 添加流程策略
     * @param process 封装有流程策略的对象
     * @return 添加成功返回true
     */
    @Insert("insert into yishen_process " +
            "(" +
            "pro_name,pro_date," +
            "pro_ipSum,pro_ipSumThreshold," +
            "pro_ipKeysum,pro_ipKeysumThreshold," +
            "pro_ipInterval,pro_ipIntervalThreshold," +
            "pro_ipKeyinterval,pro_ipKeyintervalThreshold," +
            "pro_ipUseragent,pro_ipUseragentThreshold," +
            "pro_finalThreshold" +
            ")" +
            "values" +
            "(" +
            "#{proName},#{proDate}," +
            "#{proIpSum},#{proIpSumThreshold}," +
            "#{proIpKeySum},#{proIpKeySumThreshold}," +
            "#{proIpInterval},#{proIpIntervalThreshold}," +
            "#{proIpKeyInterval},#{proIpKeyIntervalThreshold}," +
            "#{proIpUserAgent},#{proIpUserAgentThreshold}," +
            "#{proFinalThreshold}" +
            ")")
    boolean insertPro(Process process);

    /**
     * 根据策略名删除process策略
     * @param process 封装有将要删除的流程策略的策略名
     * @return 删除成功则返回true
     */
    @Delete("delete from yishen_process where pro_name = #{proName}")
    boolean deletePro(Process process);

    /**
     * 根据process策略名，修改process策略的pro_enable字段，以启用/停止process策略
     * @param process 封装有【策略名】和【启用标志信息】
     * @return 修改成功返回true
     */
    @Update("update yishen_process set pro_enable=#{proEnableId} where pro_name=#{proName}")
    boolean updateEnableProcess(Process process);

}
