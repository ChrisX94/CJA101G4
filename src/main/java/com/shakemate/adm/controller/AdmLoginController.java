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
		return "back-end/adm/admLogin.html";
	}

	@PostMapping("/admLogin")
	public String doLogin(@RequestParam String admAcc, @RequestParam String admPwd, HttpSession session, Model model) {

		AdmVO adm = admService.findByAcc(admAcc); // 在 service 寫這個方法
		
		if (adm != null && PasswordUtil.matchPassword(admPwd, adm.getAdmPwd())) {

			// 將登入者的資料儲存進session
			session.setAttribute("loggedInAdm", adm);

			// 判斷是否為最高級管理員
			boolean isSuperAdmin = adm.getAuthFuncs() != null &&
				    adm.getAuthFuncs().stream().anyMatch(auth -> "最高管理員".equals(auth.getAuthName()));

			// 若為最高級管理員 導向list all
			if (isSuperAdmin) {
				return "redirect:/adm/listAllAdm";
				// 若不為 導向一般頁面
			} else {
				return "back-end/adm/adminHome";
			}

		} else {
			model.addAttribute("error", "您的帳號或密碼錯誤"); // failed to login
			return "back-end/adm/admLogin";
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
