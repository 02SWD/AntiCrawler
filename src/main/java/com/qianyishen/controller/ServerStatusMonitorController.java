package com.qianyishen.controller;

import com.qianyishen.domain.Server;
import com.qianyishen.service.impl.ServerStatusView.ServerStatusMonitorServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用于服务器信息的查询
 */
@Controller("serverStatusMonitorController")
public class ServerStatusMonitorController {

    //用于服务器状态的查询
    @Resource(name = "serverStatusMonitorService")
    private ServerStatusMonitorServiceImpl serverStatusMonitorService;

    /**
     * 用于查询服务器的基本信息，为普通用户使用（主键id，主机名，创建时间）
     * @return 封装有服务器基本信息的list集合
     */
    @ResponseBody
    @RequestMapping("/ServerStatusMonitorController/findBasicInformation")
    public List<Server> findBasicInformation(){
        return serverStatusMonitorService.findBasicInformation();
    }

    /**
     * 用于查询服务器的基本信息，为system使用（主键id，主机名，创建时间）
     * @return 封装有服务器基本信息的list集合
     */
    @ResponseBody
    @RequestMapping("/systemPages/ServerStatusMonitorController/findBasicInformation")
    public List<Server> findBasicInformationSystem(){
        return serverStatusMonitorService.findBasicInformation();
    }

    /**
     * 根据id查询对应服务器的基本信息，为普通用户使用
     * @param id 服务器对应的主键id
     * @return 封装有服务器数据
     */
    @ResponseBody
    @RequestMapping("/ServerStatusMonitorController/findBasicInformationById")
    public Server findBasicInformationById(int id){
        return serverStatusMonitorService.findBasicInformationById(id);
    }

    /**
     * 根据id查询对应服务器的基本信息，为system使用
     * @param id 服务器对应的主键id
     * @return 封装有服务器数据
     */
    @ResponseBody
    @RequestMapping("/systemPages/ServerStatusMonitorController/findBasicInformationById")
    public Server findBasicInformationByIdSystem(int id){
        return serverStatusMonitorService.findBasicInformationById(id);
    }

}
