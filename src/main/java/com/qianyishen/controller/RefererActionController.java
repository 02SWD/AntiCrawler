package com.qianyishen.controller;

import com.qianyishen.domain.Referer;
import com.qianyishen.service.impl.RefererActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用于查找，添加，删除referer正则的controller
 * @author user
 */
@Controller("refererActionController")
@RequestMapping("systemPages/refererActionController")
public class RefererActionController {

    @Resource(name = "refererActionService")
    private RefererActionServiceImpl refererActionService;

    /**
     * 查询所有referer正则（用于处理Ajax类型的请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAllRefAjax")
    public Object findAllRefAjax(){
        //查询所有referer正则
        List<Referer> allRef = refererActionService.findAllRef();
        if (allRef == null || allRef.size() < 1){
            return "您还未创建referer正则";
        }else {
            return allRef;
        }
    }


    /**
     * 添加referer（用于处理Ajax请求）
     * @param referer 封装有referer正则的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/insertRefAjax")
    public String insertRefAjax(Referer referer){
        //添加referer
        String flag = refererActionService.insertReferer(referer);
        if ("-1".equals(flag)){
            return "referer正则插入失败";
        }else if ("0".equals(flag)){
            return "请输入referer正则";
        }else if ("1".equals(flag)){
            return "referer正则添加成功";
        }
        return "referer正则插入失败";
    }

    /**
     * 根据主键id删除referer正则（用于出咯Ajax类型的请求）
     * @param referer 封装有将要删除的referer正则id的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/deleteRefAjax")
    public String deleteRefAjax(Referer referer){
        //根据id删除referer正则
        String flag = refererActionService.deleteReferer(referer);
        if ("-1".equals(flag)){
            return "referer正则删除失败";
        }else if ("0".equals(flag)){
            return "请输入将要删除的referer正则id";
        }else if ("1".equals(flag)){
            return "referer正则删除成功";
        }
        return "referer正则删除失败";
    }



}
