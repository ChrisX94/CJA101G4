package com.shakemate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	// 首頁設計完成檔案，各位可參考並直接做修改： index.html

	@GetMapping({ "/", "/shakemate" })
	public String MyMethod() {
		return "index"; // ---> src\main\resources\templates\index1.html
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login"; // 對應 resources/templates/login.html
	}

	// 用來導向客服GPT頁面 sindex.html
	@GetMapping("/shakemate/chat")
	public String chatPage() {
		return "front-end/servicecase/sindex";
	}

	// 用來導向後台頁面 manage.html
	@GetMapping("/shakemate/manage")
	public String managePage() {
		return "back-end/admin/manage";
	}

	// 用來導向頁面 adminHome.html
	@GetMapping("/admin")
	public String adminPage() {
		// 回傳的是 Thymeleaf 的 view 名稱，例如 static/shshop/front_end/select_page.html
		return "back-end/adm/adminHome";
	}
	
	// 用來導向頁面 testlogin.html
	@GetMapping("/testlogin")
	public String testloginPage() {
		return "testlogin/testlogin";
	}
	
	

}