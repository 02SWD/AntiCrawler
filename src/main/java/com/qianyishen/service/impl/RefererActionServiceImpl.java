package com.qianyishen.service.impl;

import com.qianyishen.dao.IPoliciesRefererActionDao;
import com.qianyishen.domain.Referer;
import com.qianyishen.service.IRefererActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * referer操作类
 *      查看所有，添加，删除
 * @author user
 */
@Service("refererActionService")
public class RefererActionServiceImpl implements IRefererActionService {

    @Resource(name = "policiesRefererAction")
    private IPoliciesRefererActionDao policiesRefererAction;

    /**
     * 查询所有referer
     * @return 返回所有查询结果
     */
    @Override
    public List<Referer> findAllRef() {
        return policiesRefererAction.findAllRef();
    }

    /**
     * 插入referer规则
     * @param referer 封装有referer规则的对象
     * @return 添加成功返回
     *      -1 代表添加失败
     *       0 代表用户没有输入就提交
     *       1 代表添加成功
     */
    @Override
    public String insertReferer(Referer referer) {
        String flag = "-1";
        if ("".equals(referer.getRefPattern())){
            flag = "0";
            return flag;
        }
        if (policiesRefererAction.insertReferer(referer)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 删除referer规则（根据id）
     * @param referer 封装有refererId的对象
     * @return 添加成功返回
     *          -1 代表删除失败
     *           0 代表用户名没有输入要删除的id
     *           1 代表删除成功
     */
    @Override
    public String deleteReferer(Referer referer) {
        String flag = "-1";
        if (referer.getRefId() == null){
            flag = "0";
            return flag;
        }
        if (policiesRefererAction.deleteReferer(referer)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
