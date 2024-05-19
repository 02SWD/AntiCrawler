package com.qianyishen.service.impl;

import com.qianyishen.dao.IPoliciesKeyPagesActionDao;
import com.qianyishen.domain.KeyPages;
import com.qianyishen.service.IKeyPagesActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *该service用于操作关键页面的正则数据 （操作 yishen_keyPages）
 *      查询所有，添加，删除
 */
@Service("keyPagesActionService")
public class KeyPagesActionServiceImpl implements IKeyPagesActionService {

    //引入 keyPagesActionDao
    @Resource(name = "keyPagesActionDao")
    private IPoliciesKeyPagesActionDao keyPagesActionDao;

    /**
     * 查询所有 关键页面的正则
     * @return 返回关键页面的正则
     */
    @Override
    public List<KeyPages> findAllKeyPagesPattern() {
        return keyPagesActionDao.findAllKeyPagesPattern();
    }

    /**
     * 添加关键页面的正则
     * @param keyPages 封装有关键页面正则数据的实体类
     * @return -1 代表添加失败
     *          0 代表用户输入为空
     *          1 代表添加成功
     */
    @Override
    public String insertKeyPagesPattern(KeyPages keyPages) {
        String flag = "-1";
        if ("".equals(keyPages.getKeyPagesPattern())){
            flag = "0";
            return flag;
        }
        if (keyPagesActionDao.insertKeyPagesPattern(keyPages)){
            flag = "1";
            return flag;
        }
        return flag;
    }

    /**
     * 根据主键id删除关键页面正则
     * @param keyPages 封装有 将被删除的页面正则对应的 主键id值
     * @return -1 代表插入失败
     *          0 代表用户输入为空
     *          1 代表删除成功
     */
    @Override
    public String deleteKeyPagesPattern(KeyPages keyPages) {
        String flag = "-1";
        if (keyPages.getKeyPagesId() == null){
            flag = "0";
            return flag;
        }
        if (keyPagesActionDao.deleteKeyPagesPattern(keyPages)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
