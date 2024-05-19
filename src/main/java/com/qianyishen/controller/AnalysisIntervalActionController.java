package com.qianyishen.controller;

import com.qianyishen.domain.AnalysisInterval;
import com.qianyishen.service.impl.AnalysisIntervalActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *  用于“查询，修改”[获取原始数据的时间间隔]（不提供添加，删除，因为 获取原始数据的时间间隔 唯一且不为空）
 *  "获取原始数据的时间间隔"的作用：
 *      查询 now()-时间间隔 ~ now() 之内的原始数据，单位：秒
 */
@Controller("analysisIntervalActionController")
@RequestMapping("systemPages/analysisIntervalActionController")
public class AnalysisIntervalActionController {

    //引入 analysisIntervalActionService
    @Resource(name = "analysisIntervalActionService")
    private AnalysisIntervalActionServiceImpl analysisIntervalActionService;

    /**
     * 查询 获取原始数据的时间间隔（用于处理Ajax类型的请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAnalysisTimeIntervalAjax")
    public Object findAnalysisTimeIntervalAjax(){
        return analysisIntervalActionService.findTimeInterval();
    }


    /**
     * 修改 获取原始数据的时间间隔（用于处理Ajax类型的请求）
     * @param analysisInterval 将要修改的时间间隔
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/updateAnalysisTimeIntervalAjax")
    public String updateAnalysisTimeIntervalAjax(AnalysisInterval analysisInterval){
        String flag = analysisIntervalActionService.updateTimeInterval(analysisInterval);
        if("-2".equals(flag)){
            return "不能将时间间隔设置为 <=0 的数";
        }else if ("-1".equals(flag)){
            return "AnalysisTimeInterval 修改失败";
        }else if ("0".equals(flag)){
            return "请输入将要修改的时间间隔";
        }else if ("1".equals(flag)){
            return "时间间隔修改成功";
        }
        return "AnalysisTimeInterval 修改失败";
    }


}
