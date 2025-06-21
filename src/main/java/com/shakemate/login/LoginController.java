package com.shakemate.login;

import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import com.shakemate.user.util.UserPostMultipartFileUploader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/login")
public class LoginController {


    @Autowired
    private UserService userService;

    @Autowired
    private UserPostMultipartFileUploader imgHandler;

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
        System.out.println(session.getAttribute("account"));
        String location = (String) session.getAttribute("location");
        return "redirect:" + (location != null ? location : "testlogin");
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

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new Users());
        return "front-end/user/signup";
    }

    @PostMapping("/signupHandler")
    public String signupHandler(@Valid Users user,
                                ModelMap model,
                                BindingResult bindingResult,
                                @RequestParam("confirm_password") String confirmPassword,
                                @RequestParam("interests") String[] interestsArr,
                                @RequestParam("personality") String[] personalityArr,
                                @RequestParam("image") MultipartFile[] parts) throws IOException {

        if (!user.getPwd().equals(confirmPassword)) {
            model.addAttribute("pwdMismatch", "密碼與確認密碼不一致！");
            return "front-end/user/signup";
        }
        bindingResult = removeFieldError(user, bindingResult, "image");
        String imgUrl = "";
        if (parts == null || parts.length == 0 || parts[0].isEmpty()) {
            model.addAttribute("errorMessage", "請上傳照片");
            return "front-end/user/signup";
        }
        try {
            imgUrl = imgHandler.uploadImageToImgbb(parts[0]);
            if (imgUrl == null || imgUrl.isBlank()) {
                model.addAttribute("errorMessage", "照片上傳失敗");
                return "front-end/user/signup";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "系統錯誤：" + e.getMessage());
            return "front-end/user/signup";
        }
        if (bindingResult.hasErrors() || parts[0].isEmpty()) {
            return "front-end/user/signup";
        }
        userService.signIn(user, interestsArr, personalityArr, imgUrl);

        model.addAttribute("success", "- (新增成功)");


        return "testlogin/testlogin";
    }

    @GetMapping("/testlogin")
    public String testLogin(HttpServletRequest request) {
        return "testlogin/testlogin"; // 回登入畫面
    }

    public BindingResult removeFieldError(Users user, BindingResult result, String removedFieldname) {
        List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
                .filter(fieldname -> !fieldname.getField().equals(removedFieldname))
                .collect(Collectors.toList());
        result = new BeanPropertyBindingResult(user, "user");
        for (FieldError fieldError : errorsListToKeep) {
            result.addError(fieldError);
        }
        return result;
    }

}
