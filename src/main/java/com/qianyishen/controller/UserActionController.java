package com.qianyishen.controller;

import com.qianyishen.domain.User;
import com.qianyishen.service.impl.UserActionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能：
 *      用户的添加、修改、删除、查询全部
 * @author user
 */
@Controller("userActionController")
@RequestMapping("systemPages/userActionController")
public class UserActionController {

    @Resource(name = "userActionServiceImpl")
    private UserActionServiceImpl userActionService;


    /**
     * 查询除system用户外的所有用户（用于处理Ajax类型请求）
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/findAllAjax")
    public Object findAllAjax(){
        //查询除system用户外的所有用户
        List<User> userList = userActionService.findAll();
        if (userList == null || userList.size() < 1){
            return "您还未创建普通用户";
        }else {
            return userList;
        }
    }


    /**
     * 添加用户（用于处理Ajax类型请求）
     * @param user 封装有用户数据的对象
     * @return Json数据
     */
    @ResponseBody
    @RequestMapping("/insertUserAjax")
    public String insertUserAjax(User user){
        //执行添加操作
        String flag = userActionService.insertUser(user);
        if ("-2".equals(flag)){
            return "用户创建失败";
        }else if ("0".equals(flag)){
            return "用户信息未填写完整";
        }else if ("-1".equals(flag)){
            return "用户已被创建";
        }else if ("-3".equals(flag)){
            return "用户名长度最大为20";
        }else if ("-4".equals(flag)){
            return "请输入合法电话";
        }else if ("-5".equals(flag)){
            return "密码长度过长";
        }else if ("-6".equals(flag)){
            return "邮箱长度过长";
        }else if ("1".equals(flag)){
            return "用户创建成功";
        }
        return "用户创建失败";
    }


    /**
     * 修改用户信息（用于处理Ajax类型请求）
     * @param user 封装有用户数据的对象
     * @return Json数据
     *      -7 代码用户没有输入用户名
     *      -6 代表邮箱长度超过256
     *      -5 代表电话长度超过20
     *      -4 代表密码长度超过256
     *      -3 代表用户名长度超过20
     *      -2 代表表单一次性提交了多个信息，但是这里仅支持一次修改一个信息
     *      -1 代表修改失败
     *       1 代表修改成功
     */
    @ResponseBody
    @RequestMapping("/updateUserAjax")
    public String updateUserAjax(User user){
        //执行修改操作
        String flag = userActionService.updateUser(user);
        if ("-2".equals(flag)){
            return "一次性只能修改一个信息哦";
        }else if ("-1".equals(flag)){
            return "修改失败";
        }else if ("-7".equals(flag)){
            return "请输入用户名";
        }else if ("-6".equals(flag)){
            return "邮箱长度不能超过256";
        }else if ("-5".equals(flag)){
            return "电话长度不能超过20";
        }else if ("-4".equals(flag)){
            return "密码长度不能超过256";
        }else if ("-3".equals(flag)){
            return "用户名长度不能超过20";
        }else if ("1".equals(flag)){
            return "修改成功";
        }
        return "修改失败";
    }


    /**
     * 根据用户名删除用户（用于处理Ajax类型请求）
     * @param user 封装有用户数据的对象
     * @return Json数据
     *      -1 表示用户提交的是空表单
     *      -2 表示删除失败
     *      -3 表示用户名过长
     *       0 表示用户要删除的用户在数据库中不存在
     *       1 表示删除成功
     */
    @ResponseBody
    @RequestMapping("/deleteUserAjax")
    public String deleteUserAjax(User user){
        //执行删除操作
        String flag = userActionService.deleteUser(user);
        if ("-1".equals(flag)){
            return "请不要提交空表单";
        }else if ("-3".equals(flag)){
            return "表示用户名过长";
        }else if ("-2".equals(flag)){
            return "删除失败";
        }else if ("0".equals(flag)){
            return "您要删除的用户不存在";
        }else if ("1".equals(flag)){
            return "删除成功";
        }
        return "删除失败";
    }


}
