package com.shakemate.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginFilter implements Filter {
    private FilterConfig config;

    @Override
    public void init(FilterConfig config) {
        this.config = config;
    }

    @Override
    public void destroy() {
        config = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request; // ServletRequest 轉成 HttpServletRequest
        HttpServletResponse res = (HttpServletResponse) response; // ServletResponse 轉成 HttpServletResponse

        HttpSession session = req.getSession(); // 取得 Session 物件
        Object account = session.getAttribute("account"); // 取得 account 物件

        if (account == null) { // 如果 account 沒有值
            session.setAttribute("location", req.getRequestURI()); // 取得前頁的資訊, req.getRequestURI() 是 Servlet 中用來取得 HTTP 請求的 URI 路徑
            res.sendRedirect(req.getContextPath() + "/login"); // 要重導的網址
            return;
        } else {
            // 有登入就放行
            chain.doFilter(request, response);
        }
    }
}
