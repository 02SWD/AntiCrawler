package com.qianyishen.controller;

import com.qianyishen.domain.BlackTime;
import com.qianyishen.service.impl.BlackTimeActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 本controller提供封禁时间间隔的查询和修改（不提供添加，删除，因为封禁时间间隔唯一且不为空）
 */
@Controller("blackTimeActionController")
@RequestMapping("systemPages/blackTimeActionController")
public class BlackTimeActionController {

    //引入 blackTimeActionService
    @Resource(name = "blackTimeActionService")
    private BlackTimeActionServiceImpl blackTimeActionService;

    /**
     * 查询封禁的时间间隔（用于处理Ajax类型的请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findBlackTimeAjax")
    public Object findBlackTimeAjax(){
        return blackTimeActionService.findBlackTime();
    }


    /**
     * 修改封禁时间间隔（用于处理Ajax类型的请求）
     * @param blackTime 将要修改的时间间隔
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/updateBlackTimeAjax")
    public String updateBlackTimeAjax(BlackTime blackTime){
        String flag = blackTimeActionService.updateBlackTime(blackTime);
        if ("-2".equals(flag)){
            return "不能将封禁时间设置为 <=0 的数";
        }else if ("-1".equals(flag)){
            return "blackTimeInterval修改失败";
        }else if ("0".equals(flag)){
            return "请输入将要修改至的封禁时间间隔";
        }else if ("1".equals(flag)){
            return "封禁时间间隔修改成功";
        }
        return "blackTimeInterval修改失败";
    }


}
