package com.shakemate.adm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shakemate.adm.model.AdmService;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.adm.util.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/adm")
public class AdmLoginController {

	@Autowired
	private AdmService admService;

	@GetMapping("/admLogin")
	public String showLoginPage() {
		return "back-end/adm/admLogin";
	}

	@PostMapping("/admLogin")
	public String doLogin(@RequestParam String admAcc, @RequestParam String admPwd, HttpSession session, Model model) {

		AdmVO adm = admService.findByAcc(admAcc); // 先查帳號

		if (adm == null) {
			model.addAttribute("error", "您的帳號不存在");
			return "back-end/adm/admLogin";
		}

		// 判斷帳號是否停用
		if (!adm.getAdmSta()) {
			model.addAttribute("error", "您的帳號已被停用");
			return "back-end/adm/admLogin";
		}

		// 密碼錯誤
		if (!PasswordUtil.matchPassword(admPwd, adm.getAdmPwd())) {
			model.addAttribute("error", "您的帳號或密碼錯誤");
			return "back-end/adm/admLogin";
		}

		// 登入成功，判斷是否為超級管理員
		boolean isSuperAdmin = adm.getAuthFuncs() != null &&
				adm.getAuthFuncs().stream().anyMatch(auth -> auth.getAuthId() == 1);

		// 設定 session
		session.setAttribute("loggedInAdm", adm);
		session.setAttribute("isSuperAdmin", isSuperAdmin);

		// 根據角色導向
		if (isSuperAdmin) {
			return "redirect:/adm/adminHome";
		} else {
			return "back-end/adm/adminHome";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		return "redirect:/adm/admLogin"; // back to login page
	}
}
