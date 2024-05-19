package com.qianyishen.service.impl;

import com.qianyishen.dao.IPoliciesProcessActionDao;
import com.qianyishen.domain.Process;
import com.qianyishen.service.IProcessActionService;
import jdk.nashorn.internal.ir.Flags;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用于添加，查询，删除，启用 流程策略
 * @author user
 */
@Service("processActionService")
public class ProcessActionServiceImpl implements IProcessActionService {

    @Resource(name = "policiesProcessActionDao")
    private IPoliciesProcessActionDao policiesProcessActionDao;

    /**
     * 查询所有流程策略
     * @return 策略集合
     */
    @Override
    public List<Process> findAllPro() {
        return policiesProcessActionDao.findAllPro();
    }

    /**
     * 添加流程策略
     * @param process 封装有流程策略的对象
     * @return 返回
     *          -2 代表该process策略名已经存在
     *          -1 代表添加失败
     *           0 代表用户输入不完全
     *           1 代表添加成功
     */
    @Override
    public String insertPro(Process process) {
        String flag = "-1";
        //注意要去掉字符串首尾的空格
        String proName = process.getProName().trim();
        //process的创建时间不由用户自定义，由系统自动生成
        long currentTimeMillis = System.currentTimeMillis();
        process.setProDate(new Timestamp(currentTimeMillis));

        Integer proIpSum = process.getProIpSum();
        Integer proIpKeySum = process.getProIpKeySum();
        Integer proIpInterval = process.getProIpInterval();
        Integer proIpKeyInterval = process.getProIpKeyInterval();
        Integer proIpUserAgent = process.getProIpUserAgent();
        Integer proFinalThreshold = process.getProFinalThreshold();
        if ("".equals(proName) || proIpSum == null || proIpKeySum == null || proIpInterval == null || proIpKeyInterval == null || proIpUserAgent == null || proFinalThreshold == null){
            flag = "0";
            return flag;
        }
        Integer proIpSumThreshold = process.getProIpSumThreshold();
        Integer proIpKeySumThreshold = process.getProIpKeySumThreshold();
        Integer proIpIntervalThreshold = process.getProIpIntervalThreshold();
        Integer proIpKeyIntervalThreshold = process.getProIpKeyIntervalThreshold();
        Integer proIpUserAgentThreshold = process.getProIpUserAgentThreshold();
        if (proIpSumThreshold == null || proIpKeySumThreshold == null || proIpIntervalThreshold == null || proIpKeyIntervalThreshold == null || proIpUserAgentThreshold == null){
            flag = "0";
            return flag;
        }
        if (policiesProcessActionDao.findProByName(proName) != null){
            flag = "-2";
            return flag;
        }
        if (policiesProcessActionDao.insertPro(process)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 根据 策略名 删除process策略
     * @param process 封装有流程策略名的对象
     * @return 返回
     *          -3 代表要删除的策略正在启用，不允许删除
     *          -2 代表要删除的策略不存在
     *          -1 代表删除失败
     *           0 代表用户没有输入要删除的策略名
     *           1 代表删除成功
     */
    @Override
    public String deletePro(Process process) {
        //注意要去掉字符串首尾的空格
        process.setProName(process.getProName().trim());
        String flag = "-1";
        if ("".equals(process.getProName())){
            flag = "0";
            return flag;
        }
        if (policiesProcessActionDao.findProByName(process.getProName()) == null){
            flag = "-2";
            return flag;
        }
        if (policiesProcessActionDao.findProByName(process.getProName()).getProEnableId() == 1){
            flag = "-3";
            return flag;
        }
        if (policiesProcessActionDao.deletePro(process)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 根据 策略名 启用process策略
     * process策略只能启用一个，所以如果当前process策略1正在启用，而你想启用process策略2的话，要先关闭process策略1
     * @param process 封装有将要启用的process策略的策略名
     * @return -2 代表用户想要启用的process策略不存在
     *         -1 代表启用失败
     *          0 代表用户没有输入
     *          1 代表启用成功
     */
    @Override
    public String enableProcess(Process process) {
        //注意要去掉字符串首尾的空格
        process.setProName(process.getProName().trim());
        String flag = "-1";
        //若用户没有输入，则返回 "0"
        if ("".equals(process.getProName())){
            flag = "0";
            return flag;
        }
        //若用户想要删除的process策略不存在，则返回 "-2"
        if (policiesProcessActionDao.findProByName(process.getProName()) == null){
            flag = "-2";
            return flag;
        }
        //查看此时是否存在已经启用的process策略，若存在，则将该process策略关闭
        List<Process> enableProcessList = policiesProcessActionDao.findEnableProcess();
        if (enableProcessList != null){
            for (Process pro : enableProcessList) {
                pro.setProEnableId(0);
                policiesProcessActionDao.updateEnableProcess(pro);
            }
        }
        //启用用户所选择的策略
        process.setProEnableId(1);
        if (policiesProcessActionDao.updateEnableProcess(process)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
