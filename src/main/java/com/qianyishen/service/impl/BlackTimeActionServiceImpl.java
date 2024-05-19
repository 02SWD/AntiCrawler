package com.qianyishen.service.impl;

import com.qianyishen.dao.IBlackTimeActionDao;
import com.qianyishen.domain.BlackTime;
import com.qianyishen.service.IBlackTimeActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 封禁时间操作
 *  查询、修改
 */
@Service("blackTimeActionService")
public class BlackTimeActionServiceImpl implements IBlackTimeActionService {

    //引入入blackTimeActionDao
    @Resource(name = "blackTimeActionDao")
    private IBlackTimeActionDao blackTimeActionDao;

    /**
     * 查询目前所设置的封禁时间
     * @return 返回查询的封禁时间
     */
    @Override
    public List<BlackTime> findBlackTime() {
        return blackTimeActionDao.findBlackTime();
    }

    /**
     * 修改封禁时间
     * @param blackTime 封装有将要被修改成的封禁时间
     * @return -2 代表用户输入了 <=0 的数（这是不允许的）
     *         -1 代表修改失败
     *          0 代表用户没有输入提交
     *          1 代表修改成功
     */
    @Override
    public String updateBlackTime(BlackTime blackTime) {
        String flag = "-1";
        if (blackTime.getBlackTimeInterval() == null){
            flag = "0";
            return flag;
        }
        if (blackTime.getBlackTimeInterval() <= 0 ){
            flag = "-2";
            return flag;
        }
        if (blackTimeActionDao.updateBlackTime(blackTime)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
