package com.qianyishen.filter;

import com.qianyishen.domain.User;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 强制登录过滤器，并进行权限控制
 * @author user
 */
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        //1.强制转换
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //2.获取请求资源路径
        String requestUri = request.getRequestURI();
        //3.判断是否包含登录相关资源路径,同时排除css,js，图片等
        if (requestUri.contains("/index.html") || requestUri.contains("/login")||requestUri.contains("/static/")) {
            //放行
            chain.doFilter(req, resp);
        } else {
            //若无cookie，则直接重定向到登录页面
            if (request.getCookies() == null){
                response.sendRedirect(request.getContextPath() + "/index.html");
            }else {
                //4.实现强制登录，并且实现访问页面的权限
                User user = null;
                for (Cookie cookie : request.getCookies()) {
                    //判断是否有名为 ogin 的cookie，若有则进一步判断权限，若无，则直接重定向至登录页面
                    if ("ogin".equals(cookie.getName())){
                        user = (User) request.getSession().getAttribute(cookie.getValue());
                    }
                }
                if (user == null) {
                    //未登录，跳转登陆页面
                    request.setAttribute("login_msg","您未登录");
                    response.sendRedirect(request.getContextPath() + "/index.html");
                }else {
                    //若普通用户想要访问system页面，则将其重定向到userView.html页面
                    if (!"system".equals(user.getUsername()) && requestUri.contains("/systemPages")){
                        response.sendRedirect(request.getContextPath() + "/userView.html");
                    }else {
                        chain.doFilter(req,resp);
                    }
                }
            }

        }
    }

    @Override
    public void destroy() {

    }
}
