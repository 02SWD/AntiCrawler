package com.qianyishen.service.impl.ServerStatusView;

import com.qianyishen.dao.IInquireServerStatusDao;
import com.qianyishen.domain.Server;
import com.qianyishen.domain.ServerStatus;
import com.qianyishen.service.ServerStatusMonitorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用于查询服务器的基本信息及其状态信息
 */
@Service("serverStatusMonitorService")
public class ServerStatusMonitorServiceImpl implements ServerStatusMonitorService {

    /**
     * 用于服务器状态的查询
     */
    @Resource(name = "inquireServerStatusDao")
    private IInquireServerStatusDao inquireServerStatusDao;

    /**
     * 用于查询服务器的基本信息（主键id，主机名，创建时间）
     * @return 封装有服务器基本信息的list集合
     */
    @Override
    public List<Server> findBasicInformation() {
        return inquireServerStatusDao.findBasicInformation();
    }

    /**
     * 用于查询服务器的状态信息（cpu占用率，内存占用率，磁盘占用率，磁盘IO率）
     * @param id 所要查询的服务器状态信息的服务器主键id
     * @param nowDate 传递从什么时刻开始查询
     * @param m 代表从第m条信息开始向后查询（用于查询：limit m,n  m=m+1 n=linesize）
     * @return 封装有服务器状态信息的list集合
     */
    @Override
    public List<ServerStatus> findServerStatus(int id, Timestamp nowDate, int m) {
        return inquireServerStatusDao.findServerStatus(id, nowDate.toString(), m);
    }

    /**
     * 根据id查询对应服务器的基本信息
     * @param id 服务器对应的主键id
     * @return 封装有服务器数据
     */
    @Override
    public Server findBasicInformationById(int id){
        return inquireServerStatusDao.findBasicInformationById(id);
    }

}
