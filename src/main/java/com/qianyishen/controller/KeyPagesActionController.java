package com.qianyishen.controller;

import com.qianyishen.domain.KeyPages;
import com.qianyishen.service.impl.KeyPagesActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller("keyPagesActionController")
@RequestMapping("systemPages/keyPagesActionController")
public class KeyPagesActionController {

    @Resource(name = "keyPagesActionService")
    private KeyPagesActionServiceImpl keyPagesActionService;

    /**
     * 查询所有 关键页面的正则（用于处理Ajax类型的请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAllKeyPagesPatternAjax")
    public Object findAllKeyPagesPatternAjax(){
        //查询所有 keyPages 正则
        List<KeyPages> allKeyPagesPattern = keyPagesActionService.findAllKeyPagesPattern();
        if (allKeyPagesPattern == null || allKeyPagesPattern.size() < 1){
            return "您还未添加keyPages正则";
        }else {
            return allKeyPagesPattern;
        }
    }


    /**
     * 根据主键id删除关键页面正则（用于处理Ajax类型的请求）
     * @param keyPages 封装有 将被删除的页面正则对应的 主键id值
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/insertKeyPagesPatternAjax")
    public String insertKeyPagesPatternAjax(KeyPages keyPages){
        //添加 keyPages 正则
        String flag = keyPagesActionService.insertKeyPagesPattern(keyPages);
        if ("-1".equals(flag)){
            return "keyPages正则添加失败";
        }else if ("0".equals(flag)){
            return "用户输入为空";
        }else if ("1".equals(flag)){
            return "keyPages正则添加成功";
        }
        return "keyPages正则添加失败";
    }

    /**
     * 根据主键id删除关键页面正则（用于处理Ajax类型的请求）
     * @param keyPages 封装有 将被删除的页面正则对应的 主键id值
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/deleteKeyPagesPatternAjax")
    public String deleteKeyPagesPatternAjax(KeyPages keyPages){
        //删除 keyPages 正则
        String flag = keyPagesActionService.deleteKeyPagesPattern(keyPages);
        if ("-1".equals(flag)){
            return "keyPages正则删除失败";
        }else if ("0".equals(flag)){
            return "用户输入为空";
        }else if ("1".equals(flag)){
            return "keyPages正则删除成功";
        }
        return "keyPages正则删除失败";
    }

}
