package com.qianyishen.service.impl.ServerStatusView;

import com.alibaba.fastjson.JSONObject;
import com.qianyishen.domain.ServerStatus;
import com.qianyishen.service.WebSocketService;
import com.qianyishen.utils.SpringContextUtil;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 这里使用websocket来为前端实时推送数据
 */
@ServerEndpoint(value = "/websocket")
public class WebSocketServiceImpl implements WebSocketService {

    //线程安全的静态变量，表示在线连接数
    private static volatile int onlineCount = 0;
    //用来存放每个客户端对应的WebSocketTest对象，适用于同时与多个客户端通信
    private static CopyOnWriteArraySet<WebSocketServiceImpl> webSocketSet = new CopyOnWriteArraySet<WebSocketServiceImpl>();
    //若要实现服务端与指定客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<Session, Object> webSocketMap = new ConcurrentHashMap<Session, Object>();
    //与某个客户端的连接会话，通过它实现定向推送(只推送给某个用户)
    private Session session;


    /**
     * 建立连接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);            // 添加到set中
        webSocketMap.put(session,this);    // 添加到map中
        addOnlineCount();                  // 添加在线人数
        //System.out.println("新人加入，当前在线人数为："  + getOnlineCount());
    }

    /**
     * 关闭连接调用的方法
     */
    @OnClose
    public void onClose(Session closeSession){
        webSocketMap.remove(closeSession);
        webSocketSet.remove(this);
        subOnlineCount();
        //System.out.println("有人离开，当前在线人数为：" + getOnlineCount());
    }

    /**
     *  收到客户端消息调用的方法
     */
    @OnMessage
    public void onMessage(String message,Session mysession) throws Exception{
        //获取serverStatusMonitorService（在websocke中不可以使用@Resource注解进行注入，这里进行手动获取）
        ServerStatusMonitorServiceImpl serverStatusMonitorService = SpringContextUtil.getBean(ServerStatusMonitorServiceImpl.class);
        //解析客户端发送来的数据
        String[] splits = message.split("#");
        int id = Integer.parseInt(splits[0]);    //将要被查询的服务器的主键id
        long Millis = Long.parseLong(splits[1]); //当前的时间
        int m = Integer.parseInt(splits[2]);     //从第几条开始查（从第m条数据向后查5条数据）
        //将毫秒数转化为timestamp类型的对象
        //这行代码用于生产环境
        Timestamp timestamp = new Timestamp(Millis - 10 * 1000);
        //使用serverStatusMonitorService查询相应的服务器状态数据
        List<ServerStatus> serverStatus = serverStatusMonitorService.findServerStatus(id, timestamp, m);
        //将查询到的服务器状态数据转化为json字符串发送给客户端
        mysession.getAsyncRemote().sendText(JSONObject.toJSON(serverStatus).toString());
    }

    // 获取在线人数
    private static synchronized int getOnlineCount(){
        return onlineCount;
    }

    // 添加在线人+1
    private static synchronized void addOnlineCount(){
        onlineCount ++;
    }

    // 减少在线人-1
    private static synchronized void subOnlineCount(){
        onlineCount --;
    }




}
