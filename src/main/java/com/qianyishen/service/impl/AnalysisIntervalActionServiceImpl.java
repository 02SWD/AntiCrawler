package com.qianyishen.service.impl;

import com.qianyishen.dao.IAnalysisIntervalActionDao;
import com.qianyishen.domain.AnalysisInterval;
import com.qianyishen.service.IAnalysisIntervalActionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * [获取原始数据的时间间隔]操作
 *  查询、修改
 *  "获取原始数据的时间间隔"的作用：查询 now()-时间间隔 ~ now() 之内的原始数据，单位：秒
 */
@Service("analysisIntervalActionService")
public class AnalysisIntervalActionServiceImpl implements IAnalysisIntervalActionService {

    @Resource(name = "analysisIntervalActionDao")
    private IAnalysisIntervalActionDao analysisIntervalActionDao;

    /**
     * 查询 “获取原始数据的时间间隔”
     * @return 返回时间间隔
     */
    @Override
    public List<AnalysisInterval> findTimeInterval() {
        return analysisIntervalActionDao.findTimeInterval();
    }


    /**
     * 修改 “获取原始数据的时间间隔”
     * @param analysisInterval 时间间隔
     * @return  -2 代表用户输入了 <=0 的数（这是不允许的）
     *          -1 代表修改失败
     *           0 代表用户没有输入
     *           1 代表修改成功
     */
    @Override
    public String updateTimeInterval(AnalysisInterval analysisInterval) {
        String flag = "-1";
        if (analysisInterval.getIntervalTime() == null){
            flag = "0";
            return flag;
        }
        if (analysisInterval.getIntervalTime() <= 0){
            flag = "-2";
            return flag;
        }
        if (analysisIntervalActionDao.updateTimeInterval(analysisInterval)){
            flag = "1";
            return flag;
        }
        return flag;
    }
}
