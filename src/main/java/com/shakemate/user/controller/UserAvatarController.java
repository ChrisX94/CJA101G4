package com.shakemate.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shakemate.shshop.dto.ApiResponseFactory;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/avatar")
public class UserAvatarController {
	
    @GetMapping("/userAvatar")
    public Map<String, Object> getCurrentUserId(HttpSession session) {
    	
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return null;
        }
        Integer id = (Integer) userIdObj;
        String avatarUrl = (String) session.getAttribute("userAvatar");
        Map<String, Object> result = new HashMap<>();
        result.put("userAvatar", avatarUrl);
        return result;
    }
    
}
