package com.shakemate.adm.controller;

import org.springframework.web.servlet.HandlerInterceptor;

import com.shakemate.adm.model.AdmVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class superAdminInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		AdmVO adm = (AdmVO) request.getSession().getAttribute("loggedInAdm");

		boolean isSuperAdmin = adm != null
				&& adm.getAuthFuncs().stream().anyMatch(auth -> "最高管理員".equals(auth.getAuthName()));
		
		
//		if (!isSuperAdmin) {
//		    response.sendRedirect(request.getContextPath() + "/adm/adminHome"); // 或其他你想導的頁
//		    return false;
//		}

		return true;
	}

}
