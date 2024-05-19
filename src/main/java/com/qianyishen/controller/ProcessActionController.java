package com.qianyishen.controller;

import com.qianyishen.domain.Process;
import com.qianyishen.service.impl.ProcessActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author user
 */
@Controller("processActionController")
@RequestMapping("systemPages/processActionController")
public class ProcessActionController {

    @Resource(name = "processActionService")
    private ProcessActionServiceImpl processActionService;

    /**
     * 查询所有process策略（用于处理Ajax类型的请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAllProAjax")
    public Object findAllProAjax(){
        //查询所有process策略
        List<Process> allPro = processActionService.findAllPro();
        if (allPro == null || allPro.size() < 1){
            return "你还未创建process策略";
        }else {
            return allPro;
        }
    }


    /**
     * 添加process策略（用于处理Ajax类型的请求）
     * @param process 封装有process策略的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/insertProAjax")
    public String insertProAjax(Process process){
        //添加process策略
        String flag = processActionService.insertPro(process);
        if ("-2".equals(flag)){
            return "该process策略名已经存在";
        }else if ("-1".equals(flag)){
            return "process策略插入失败";
        }else if ("0".equals(flag)){
            return "请输入process策略";
        }else if ("1".equals(flag)){
            return "process策略添加成功";
        }
        return "process策略插入失败";
    }


    /**
     * 根据 策略名 删除process策略（用于处理Ajax类型的请求）
     * @param process 封装有将要删除的process策略的策略名
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/deleteProAjax")
    public String deleteProAjax(Process process){
        String flag = processActionService.deletePro(process);
        if ("-3".equals(flag)){
            return "您要删除的策略正在启用，请禁用后再进行删除";
        }else if ("-2".equals(flag)){
            return "您要删除的process策略不存在";
        }else if ("-1".equals(flag)){
            return "process策略删除失败";
        }else if ("0".equals(flag)){
            return "请输入要删除的process的策略名";
        }else if ("1".equals(flag)){
            return "process策略删除成功";
        }
        return "process策略删除失败";
    }


    /**
     * 根据 策略名 启用process策略（用于处理Ajax类型的请求）
     * @param process 封装有将要启用的process策略的策略名
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/enableProcessAjax")
    public String enableProcessAjax(Process process){
        String flag = processActionService.enableProcess(process);
        if ("-2".equals(flag)){
            return "您想要启用的process策略不存在";
        }else if ("-1".equals(flag)){
            return "启用失败";
        }else if ("0".equals(flag)){
            return "请您输入要启用的策略名";
        }else if ("1".equals(flag)){
            return "启用成功";
        }
        return "启用失败";
    }
}
