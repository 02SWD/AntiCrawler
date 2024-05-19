package com.qianyishen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于用户登出
 * @author user
 */
@Controller("logoutController")
public class LogoutController {

    /**
     * @param cookieValue 名为 “ogin” 的cookie的值
     * @param response 响应对象
     * @param request 请求对象
     * @return 返回的页面
     * 步骤：
     *      1. 若名为oign的cookie的值为空，或不存在cookie值对应的session，则都将页面重定向到登录页面
     *      2. 若存在，则将cookie删除
     */
    @RequestMapping("/logoutController/logout")
    public String logout(@CookieValue(value = "ogin") String cookieValue, HttpServletResponse response, HttpServletRequest request){
        if (cookieValue == null || request.getSession().getAttribute(cookieValue) == null){
            return "redirect:/index.jsp";
        }
        Cookie cookie = new Cookie("ogin", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/index.jsp";
    }

    /**
     * 专门为system提供的登出方法
     * @param cookieValue 名为 “ogin” 的cookie的值
     * @param response 响应对象
     * @param request 请求对象
     * @return 返回的页面
     * 步骤：
     *      1. 若名为oign的cookie的值为空，或不存在cookie值对应的session，则都将页面重定向到登录页面
     *      2. 若存在，则将cookie删除
     */
    @RequestMapping("/systemPages/systemLogout")
    public String systemLogout(@CookieValue(value = "ogin") String cookieValue, HttpServletResponse response, HttpServletRequest request){
        if (cookieValue == null || request.getSession().getAttribute(cookieValue) == null){
            return "redirect:/index.jsp";
        }
        Cookie cookie = new Cookie("ogin", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/index.jsp";
    }
}
