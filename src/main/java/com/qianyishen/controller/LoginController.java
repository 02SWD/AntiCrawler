package com.qianyishen.controller;

import com.qianyishen.domain.User;
import com.qianyishen.service.impl.LoginServiceImpl;
import com.qianyishen.utils.HashUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

/**
 * 登录controller，在验证用户的同时，若登陆成功就将该用户存储道session域中
 * @author user
 */
@Controller("loginController")
@RequestMapping("login")
@SessionAttributes(value = {"user"}, types = {User.class})
public class LoginController {

    @Resource(name = "loginService")
    private LoginServiceImpl loginService;

    @Resource(name = "random")
    private Random random;

    /**
     * @param username 用户名
     * @param password 密码
     * @param model 将查询的user对象存储在session域中
     * @return string
     *
     * 步骤：
     *      1. 先判断前台是否有数据传输过来，若没有，则直接重定向到登录页面
     *      2. 生成一个随机数
     *      3. 调用service层方法根据前台传输来的用户名和密码判断是否存在该用户
     *      4. 将前台传来的用户名+生成的随机数拼接起来，与查询出的对象一同存储session中
     *      5. 将前台传来的用户名+生成的随机数拼接起来，与查询出的对象一同设置到cookie中
     *      6. 若未查询到用户，则进行重定向
     *      7. 若查到该用户，判断该用户是否是system用户，以便重定向到对应的页面
     */
    @RequestMapping("/loginByName")
    public String loginByName(String username, String password, Model model, HttpServletResponse response){
        if (username == null || password == null){
            return "redirect:/index.html";
        }
        User user = loginService.login(username, password);
        if (user == null){
            return "redirect:/index.html";
        }else {
            //生成随机数以防止越权访问（若不加随机数的话，攻击者只需要猜解出正确的用户名，然后sha256加密就实现越权了）
            int randomNum = random.nextInt(100000);
            //将user对象存储session
            model.addAttribute(HashUtil.shaMd5(username + randomNum),user);
            //将 “username+随机数”的sha256摘要 写入名为"ogin"的cookie
            Cookie cookie = new Cookie("ogin", HashUtil.shaMd5(username +randomNum));
            cookie.setMaxAge(10*60);
            cookie.setPath("/");
            response.addCookie(cookie);
            if("system".equals(user.getUsername())){
                return "redirect:/systemPages/systemView.html";
            }
            return "redirect:/userView.html";
        }
    }

    /**
     * 查询当前用户的用户名
     * @param request 封装有请求
     * @return 用户名
     */
    @ResponseBody
    @RequestMapping("/findCurrentUserAjax")
    public String findCurrentUserAjax(HttpServletRequest request){
        User user = null;
        for (Cookie cookie : request.getCookies()) {
            if ("ogin".equals(cookie.getName())){
                user = (User)request.getSession().getAttribute(cookie.getValue());
            }
        }
        if (user == null){
            return "";
        }
        return user.getUsername();
    }

}
