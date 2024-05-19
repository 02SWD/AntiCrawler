package com.qianyishen.service.impl.LinkStatusView;

import com.qianyishen.dao.IBlackIpActionDao;
import com.qianyishen.dao.IInquireLinkStatusDao;
import com.qianyishen.domain.BlackIp;
import com.qianyishen.domain.LinkStatus;
import com.qianyishen.service.ILinkStatusMonitorService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用于【链路流量】的查询（链路流量，即服务器的用户请求数量）
 */
@Service("linkStatusMonitorService")
public class LinkStatusMonitorServiceImpl implements ILinkStatusMonitorService {

    //用于查询链路流量
    @Resource(name = "inquireLinkStatusDao")
    private IInquireLinkStatusDao inquireLinkStatusDao;

    //用于查询爬虫IP
    @Resource(name = "blackIpActionDao")
    private IBlackIpActionDao blackIpActionDao;

    //用于获取【多例状态】下的LinkStatus实体类对象
    @Autowired
    private BeanFactory beanFactory;

    /**
     * 查询【链路流量】
     * @param initialData 要查询什么时间的链路流量
     * @return 封装有链路流量的对象
     *          flag 为 -2 ，表示用户的输入为空
     *          flag 为 -1 ，表示用户的输入不符合规范
     *          flag 为 1  ，表示查询成功
     */
    public LinkStatus getTotalFlow(String initialData){
        //使用 bean工厂来获取 LinkStatus 对象（这样获取的才是多例的）
        LinkStatus linkStatus = beanFactory.getBean(LinkStatus.class);
        if (initialData == null || "".equals(initialData)){
            linkStatus.setFlag("-2");
            linkStatus.setMsg("您的输入不能为空字符或null！");
            linkStatus.setRawCount(null);
            linkStatus.setReptileCount(null);
            return linkStatus;
        }
        //判断用户的输入是否符合规范，若不符合规范，便将flag置为-1，并返回
        if (!Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S$",initialData) && !Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S/\\S\\S$",initialData)){
            linkStatus.setFlag("-1");
            linkStatus.setMsg("您的输入不符合规范");
            linkStatus.setRawCount(null);
            linkStatus.setReptileCount(null);
            return linkStatus;
        }
        //若符合规范，查询全部用户的流量 与 查询爬虫IP的流量
        ArrayList<Integer> totalFlowByRawList = getTotalFlowByRaw(initialData);
        ArrayList<Integer> totalFlowByReptileList = getTotalFlowByReptile(initialData);
        //将全部用户的流量、爬虫IP的流量封装进LinkStatus对象，并且将flag置为1
        linkStatus.setRawCount(totalFlowByRawList);
        linkStatus.setReptileCount(totalFlowByReptileList);
        linkStatus.setFlag("1");
        linkStatus.setMsg("查询成功！");
        //返回封装好的 链路流量
        return linkStatus;

    }

    /**
     * 查询 【某一天中每个小时 或 某一天中、某一小时的每5分钟的数据量】的全部用户请求量
     * @param initialData 时间
     * @return 返回 【某一天中每个小时 或 某一天中、某一小时的每5分钟的数据量】 的请求量集合
     *      若用户输入与 ^\S\S\S\S-\S\S-\S\S$ 正则匹配成功，说明用户想要查询的是【某一天中每个小时】的请求流
     *      若用户输入与 ^\S\S\S\S-\S\S-\S\S/\S\S$ 正则匹配成功，说明用户想要查询的是【某一天中、某一小时的每5分钟的数据量】的请求流
     */
    @Override
    public ArrayList<Integer> getTotalFlowByRaw(String initialData) {
        //去掉多余的空格
        initialData = initialData.trim();
        //匹配用户输入的格式，若匹配的是下方的格式，说明用户想要查询的是【某一天中每个小时】的全部用户请求量
        if (Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S$",initialData)){
            //将 时间字符串 拼接为正确的格式
            initialData = initialData + " 0:0:0";
            //创建一个集合，用于存储【某一天中每个小时】的用户请求量（因为一天为24小时，所以集合中应有24个元素）
            ArrayList<Integer> countEveryHourList = new ArrayList<>();
            //在用户所指定的某一天中，查询【某一天中每个小时】的用户请求量（共24个小时）
            int count = 0;
            for (int i = 1; i <= 24; i++) {
                count = inquireLinkStatusDao.getTotalFlowByRawHour(initialData, i-1, i);
                countEveryHourList.add(count);
            }
            //返回用户请求量
            return countEveryHourList;
        //匹配用户输入的格式，若匹配的是下方的格式，说明用户想要查询的是【某一天中、某一小时的每5分钟的数据量】的全部用户请求量
        }else if (Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S/\\S\\S$",initialData)){
            //将 时间字符串 拼接为正确的格式
            String[] splits = initialData.split("/");
            initialData = splits[0] + " " + splits[1] + ":0:0";
            //创建一个集合，用于存储【某一天中、某一小时的每5分钟的数据量】的用户请求量（因为1小时有60分钟，且收集的是每5分钟内的用户请求量，所以集合中应有12个元素）
            ArrayList<Integer> countEveryMinuteList = new ArrayList<>();
            //在用户所指定的某一天中，查询【某一天中、某一小时的每5分钟的数据量】的用户请求量（需要查询12次）
            int count = 0;
            for (int i = 0; i < 60; i=i+5) {
                count = inquireLinkStatusDao.getTotalFlowByRawMinute(initialData, i, i+5);
                countEveryMinuteList.add(count);
            }
            //返回用户请求量
            return countEveryMinuteList;
        }else {
            return null;
        }
    }

    /**
     * 查询 【某一天中每个小时 或 某一天中、某一小时的每5分钟的数据量】 的 “爬虫IP” 请求量
     * 查询某一天内每个小时的全部请求量
     * @param initialData 时间
     * @return 返回 【某一天中每个小时 或 某一天中、某一小时的每5分钟的数据量】 的爬虫IP请求量集合
     *      若用户输入与 ^\S\S\S\S-\S\S-\S\S$ 正则匹配成功，说明用户想要查询的是【某一天中每个小时】的请求流
     *      若用户输入与 ^\S\S\S\S-\S\S-\S\S/\S\S$ 正则匹配成功，说明用户想要查询的是【某一天中、某一小时的每5分钟的数据量】的请求流
     */
    @Override
    public ArrayList<Integer> getTotalFlowByReptile(String initialData) {
        //去掉多余的空格
        initialData = initialData.trim();
        //匹配用户输入的格式，若匹配的是下方的格式，说明用户想要查询的是【某一天中每个小时】的 “爬虫IP” 请求量
        if (Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S$",initialData)){
            //将 时间字符串 拼接为正确的格式
            initialData = initialData + " 0:0:0";
            //创建一个集合，用于存储【某一天中每个小时】的 “爬虫IP” 请求量（因为一天为24小时，所以集合中应有24个元素）
            ArrayList<Integer> countEveryHourList = new ArrayList<>();
            //在用户所指定的某一天中，查询【某一天中每个小时】的 “爬虫IP” 请求量（共24个小时）
            int count = 0;
            for (int i = 1; i <= 24; i++) {
                //查询该时间段内的爬虫IP
                List<BlackIp> blackIps = blackIpActionDao.findBlackIpByHour(initialData, i - 1, i);
                //先判断该时间段内是否具有爬虫IP，若没有，自然也就没有爬虫所产生的流量，就直接将此时间段的爬虫流量置为0
                if (blackIps.size() != 0){
                    count = inquireLinkStatusDao.getTotalFlowByReptileHour(blackIps, initialData, i - 1, i);
                    countEveryHourList.add(count);
                }else {
                    countEveryHourList.add(0);
                }
            }
            //返回 “爬虫IP” 请求量
            return countEveryHourList;
        //匹配用户输入的格式，若匹配的是下方的格式，说明用户想要查询的是【某一天中、某一小时的每5分钟的数据量】的 “爬虫IP” 请求量
        } else if(Pattern.matches("^\\S\\S\\S\\S-\\S\\S-\\S\\S/\\S\\S$",initialData)){
            //将 时间字符串 拼接为正确的格式
            String[] splits = initialData.split("/");
            initialData = splits[0] + " " + splits[1] + ":0:0";
            //创建一个集合，用于存储【某一天中、某一小时的每5分钟的数据量】的 “爬虫IP” 请求量（因为1小时有60分钟，且收集的是每5分钟内的用户请求量，所以集合中应有12个元素）
            ArrayList<Integer> countEveryMinuteList = new ArrayList<>();
            //在用户所指定的某一天中，查询【某一天中、某一小时的每5分钟的数据量】的 “爬虫IP” 请求量（共12个小时）
            int count = 0;
            for (int i = 0; i < 60; i=i+5) {
                //查询该时间段内的爬虫IP
                List<BlackIp> blackIps = blackIpActionDao.findBlackIpByMinute(initialData, i, i + 5);
                //先判断该时间段内是否具有爬虫IP，若没有，自然也就没有爬虫所产生的流量，就直接将此时间段的爬虫流量置为0
                if (blackIps.size() != 0){
                    count = inquireLinkStatusDao.getTotalFlowByReptileMinute(blackIps, initialData, i, i + 5);
                    countEveryMinuteList.add(count);
                }else {
                    countEveryMinuteList.add(0);
                }
            }
            //返回用户请求量
            return countEveryMinuteList;
        }else {
            return null;
        }
    }
}
