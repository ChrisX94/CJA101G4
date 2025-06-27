package com.shakemate.news.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NhomeController {

	// 用來導向後台頁面 manage.html
	@GetMapping("/manage")
	public String managePage() {
		return "back-end/admin/manage";
	}

	// 用來導向前台頁面 form.html
	@GetMapping("/nindex")
	public String newsPage() {
		return "front-end/news/nindex";
	}
}
