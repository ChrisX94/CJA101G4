package com.shakemate.user.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.shakemate.user.model.Users;

public class UserLoginFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // 初始化
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 允許的公開路徑（不需要登入）
        if (isPublicPath(uri, contextPath)) {
            chain.doFilter(request, response);
            return;
        }

        // 驗證是否登入
        HttpSession session = request.getSession(false);
        Object loggedInUser = (session != null) ? session.getAttribute("loggedInUser") : null;
        Object account = (session != null) ? session.getAttribute("account") : null;

        // 如果沒有登入，重定向到登入頁面
        if (loggedInUser == null && account == null) {
            // 保存當前請求的 URI，登入後可以重定向回來
            if (session != null) {
                session.setAttribute("location", uri);
            }
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // 檢查用戶狀態（如果用戶已登入）
        if (loggedInUser instanceof Users) {
            Users user = (Users) loggedInUser;

            // 檢查用戶狀態是否正常（userStatus = 1 表示正常）
            if (user.getUserStatus() != 1) {
                // 用戶狀態異常，清除 session 並重定向到登入頁面
                session.invalidate();
                response.sendRedirect(contextPath + "/login?error=account_disabled");
                return;
            }
        }

        // 為所有用戶頁面添加防快取標頭
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 登入成功且狀態正常 → 放行
        chain.doFilter(request, response);
    }

    /**
     * 判斷是否為公開路徑（不需要登入驗證）
     */
    private boolean isPublicPath(String uri, String contextPath) {
        // 登入相關頁面
        if (uri.endsWith("/login") ||
                uri.endsWith("/logout") ||
                uri.contains("/loginHandler") ||
                uri.contains("/signup") ||
                uri.contains("/signupHandler")) {
            return true;
        }

        // 靜態資源
        if (uri.contains("/css/") ||
                uri.contains("/js/") ||
                uri.contains("/img/") ||
                uri.contains("/images/") ||
                uri.contains("/static/")) {
            return true;
        }

        // 首頁和公開頁面
        if (uri.equals(contextPath + "/") ||
                uri.equals(contextPath + "/index") ||
                uri.endsWith("/index.html") ||
                uri.contains("/public/")) {
            return true;
        }

        // 新聞和服務案例的公開瀏覽
        if (uri.contains("/news/nindex") ||
                uri.contains("/servicecase/sindex") ||
                uri.contains("/news/list") ||
                uri.contains("/servicecase/list")) {
            return true;
        }

        // 商品頁面的公開瀏覽
        if (uri.contains("/shshop/front_end/product_page") ||
                uri.contains("/shshop/front_end/select_page")) {
            return true;
        }

        return false;
    }

    @Override
    public void destroy() {
        // 清理資源
    }
}