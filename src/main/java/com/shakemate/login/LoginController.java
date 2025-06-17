package com.shakemate.login;

import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {


    @Autowired
    private UserService userService;

    // 處理登入
    @PostMapping("/loginHandler")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {

        Users user = userService.getUserByEmail(account);
        if (user == null || !userService.login(user.getEmail(), password)) {
            model.addAttribute("errorMsg", "登入失敗，請檢查帳號或密碼！");
            return "login"; // 回到 login.html
        }

        // 登入成功
        HttpSession session = request.getSession();
        session.setAttribute("account", user.getUserId());

        String location = (String) session.getAttribute("location");
        return "redirect:" + (location != null ? location : "/match_chatroom/match.html");
    }

    // 處理登出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: 如果沒 session 就不建立
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login"; // 回登入畫面
    }

    @GetMapping("/testlogin")
    public String testLogin(HttpServletRequest request) {

        return "redirect:/testlogin/testlogin"; // 回登入畫面
    }


}
