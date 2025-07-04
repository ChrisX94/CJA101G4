package com.shakemate.user.controller;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
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

	@GetMapping("/cookie")
	public ResponseEntity<Void> getActivityCookie(HttpSession session, HttpServletResponse res) {
		Object userIdObj = session.getAttribute("account");

		/*  測試完再打開
		if (userIdObj == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 回傳 401 無身分驗證
		}
		String userId = userIdObj.toString();
		 */

		String userId = "1"; // 這行測試用

		Cookie cookie = new Cookie("selectedUserId", userId);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
//		cookie.setAttribute("selectedUserId", userId);
		cookie.setSecure(false);
		cookie.setMaxAge(3600);
		res.addCookie(cookie); // 將 Cookie 加入 Response
		return ResponseEntity.ok().build(); // ✅ 沒有 body，只傳 cookie
	}
}
