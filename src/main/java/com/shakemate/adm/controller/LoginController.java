package com.shakemate.adm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shakemate.adm.model.AdmService;
import com.shakemate.adm.model.AdmVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private AdmService admService;

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam String admAcc, @RequestParam String admPwd, HttpSession session, Model model) {

		AdmVO adm = admService.findByAcc(admAcc); // 你要在 service 寫這個方法
		if (adm != null && adm.getAdmPwd().equals(admPwd)) {

			boolean isSuperAdmin = adm.getAuthFuncs().stream().anyMatch(auth -> auth.getAuthName().equals("最高管理員"));

			if (isSuperAdmin) {
				session.setAttribute("loggedInAdmin", adm); // 儲存登入狀態
				return "redirect:/adm/listAllAdm";
			} else {
				model.addAttribute("error", "你沒有權限查看此頁面");
				return "login";
			}

		} else {
			model.addAttribute("error", "帳號或密碼錯誤");
			return "login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}
