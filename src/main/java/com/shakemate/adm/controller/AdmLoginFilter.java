package com.shakemate.adm.controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = "/adm/*")
public class AdmLoginFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String uri = request.getRequestURI();

		if (uri.endsWith("/adm/admLogin") ||
				uri.endsWith("/adm/logout") ||
				uri.contains("/css/") ||
				uri.contains("/js/") ||
				uri.contains("/images/")) {
			chain.doFilter(request, response);
			return;
		}

		// 驗證是否登入
		HttpSession session = request.getSession(false);
		Object loggedInAdm = (session != null) ? session.getAttribute("loggedInAdm") : null;

		// if not login yet, redirect to login page
		if (loggedInAdm == null) {
			response.sendRedirect(request.getContextPath() + "/adm/admLogin");
			return;
		}

		// 為所有管理員頁面添加防快取標頭
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		// 登入成功 → 放行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
