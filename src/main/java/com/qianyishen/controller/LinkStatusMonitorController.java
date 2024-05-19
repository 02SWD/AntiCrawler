package com.qianyishen.controller;

import com.qianyishen.domain.LinkStatus;
import com.qianyishen.service.impl.LinkStatusView.LinkStatusMonitorServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 用于【链路流量】的查询
 */
@Controller("linkStatusMonitorController")
public class LinkStatusMonitorController {

    //用于查询 链路流量
    @Resource(name = "linkStatusMonitorService")
    private LinkStatusMonitorServiceImpl linkStatusMonitorService;

    /**
     * 提供给【普通用户】的【链路流量】查询方法
     * @param initialData 用户指定的时间
     * @return 返回封装有【链路流量】的对象
     */
    @ResponseBody
    @RequestMapping("/linkStatusMonitorController/getLinkFlow")
    public LinkStatus getLinkFlow(String initialData){
        return linkStatusMonitorService.getTotalFlow(initialData);

    }

    /**
     * 提供给【system用户】的【链路流量】查询方法
     * @param initialData 用户指定的时间
     * @return 返回封装有【链路流量】的对象
     */
    @ResponseBody
    @RequestMapping("/systemPages/linkStatusMonitorController/getLinkFlow")
    public LinkStatus getLinkFlowSystem(String initialData){
        return linkStatusMonitorService.getTotalFlow(initialData);
    }

}
