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
	public ResponseEntity<Map<String, Object>> getUserAvatar(HttpSession session) {
	    Map<String, Object> response = new HashMap<>();

	    Object userIdObj = session.getAttribute("account");
	    if (userIdObj == null) {
	        response.put("login", false);
	        response.put("userAvatar", null);
	        return ResponseEntity.ok(response);
	    }

	    String avatarUrl = (String) session.getAttribute("userAvatar");

	    response.put("login", true);
	    response.put("userAvatar", avatarUrl);
	    return ResponseEntity.ok(response);
	}

    
}
