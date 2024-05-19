package com.qianyishen.service.impl;

import com.qianyishen.dao.IPoliciesUserAgentActionDao;
import com.qianyishen.domain.UserAgent;
import com.qianyishen.service.IUserAgentActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * userAgent操作类
 *      查看所有，添加，删除
 * @author user
 */
@Service("userAgentActionService")
public class UserAgentActionServiceImpl implements IUserAgentActionService {

    @Resource(name = "policiesUserAgentAction")
    private IPoliciesUserAgentActionDao policiesUserAgentAction;

    /**
     * 查询所有useragent
     * @return 返回所有查询结果
     */
    @Override
    public List<UserAgent> findAllUa() {
        return policiesUserAgentAction.findAllUa();
    }

    /**
     * 添加useragent
     * @param userAgent 封装有useragent正则信息的对象
     * @return 返回
     *          -1 代表添加失败
     *           0 代表用户没有输入useragent正则
     *           1 代表添加成功
     */
    @Override
    public String insertUa(UserAgent userAgent) {
        String flag = "-1";
        if ("".equals(userAgent.getUserAgentPattern())){
            flag = "0";
            return flag;
        }
        if (policiesUserAgentAction.insertUa(userAgent)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 删除useragent（根据id）
     * @param userAgent 封装有useragent id信息的对象
     * @return 返回
     *          -1 代表添加失败
     *           0 代表用户没有输入useragent正则
     *           1 代表添加成功
     */
    @Override
    public String deleteUa(UserAgent userAgent) {
        String flag = "-1";
        if (userAgent.getUserAgentId() == null){
            flag = "0";
            return flag;
        }
        if (policiesUserAgentAction.deleteUa(userAgent)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
