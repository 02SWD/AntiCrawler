package com.qianyishen.controller;

import com.qianyishen.domain.UserAgent;
import com.qianyishen.service.impl.UserAgentActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * useragent正则的查找、添加、删除
 * @author user
 */
@Controller("userAgentActionController")
@RequestMapping("systemPages/userAgentActionController")
public class UserAgentActionController {

    @Resource(name = "userAgentActionService")
    private UserAgentActionServiceImpl userAgentActionService;

    /**
     * 查询所有useragent
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAllUaAjax")
    public Object findAllUaAjax(){
        //查询所有Useragent正则
        List<UserAgent> allUa = userAgentActionService.findAllUa();
        if (allUa == null || allUa.size() < 1){
            return "你还未创建UserAgent正则";
        }else {
            return allUa;
        }
    }


    /**
     * 添加useragent正则（用于处理Ajax类型的请求）
     * @param userAgent 封装有useragent正则信息的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/insertUaAjax")
    public String insertUaAjax(UserAgent userAgent){
        //添加useragent正则
        String flag = userAgentActionService.insertUa(userAgent);
        if ("-1".equals(flag)){
            return "Useragent正则添加失败";
        }else if ("0".equals(flag)){
            return "请输入要添加的Useragent正则";
        }else if ("1".equals(flag)){
            return "Useragent正则添加成功";
        }
        return "Useragent正则添加失败";
    }

    /**
     * 根据useragent正则的id删除（用于处理Ajax类型的请求）
     * @param userAgent 封装有useragent正则信息的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/deleteUaAjax")
    public String deleteUaAjax(UserAgent userAgent){
        //根据id删除useragent正则
        String flag = userAgentActionService.deleteUa(userAgent);
        if ("-1".equals(flag)){
            return "useragent正则删除失败";
        }else if ("0".equals(flag)){
            return "请输入要删除的Useragent正则id";
        }else if ("1".equals(flag)){
            return "useragent正则删除成功";
        }
        return "useragent正则删除失败";
    }
}
