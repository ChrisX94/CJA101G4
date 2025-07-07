package com.shakemate.user.controller;

import org.springframework.web.servlet.HandlerInterceptor;

import com.shakemate.user.model.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VipUserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Users user = (Users) request.getSession().getAttribute("loggedInUser");

        if (user == null) {
            return true;
        }

        int status = user.getUserStatus();

        switch (status) {
            case 0: // not verified
                response.sendRedirect(request.getContextPath() + "/user/notVerified");
                return false;
            case 1: // General member
                response.sendRedirect(request.getContextPath() + "/");
                return false;
            case 2: // banned
                response.sendRedirect(request.getContextPath() + "/user/accountSuspended");
                return false;
            case 3: // deleted
                response.sendRedirect(request.getContextPath() + "/user/accountDeleted");
                return false;
            default:
                response.sendRedirect(request.getContextPath() + "/");
                return false;
        }
    }
}
