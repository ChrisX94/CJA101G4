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
import java.time.LocalDate;
import java.time.Period;
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
        if (user == null || !userService.login(user.getEmail(), password) || user.getUserStatus() == (byte)4) {
            model.addAttribute("errorMsg", "登入失敗，請檢查帳號或密碼！");
            return "login"; // 回到 login.html
        }
        // 登入成功
        HttpSession session = request.getSession();
        session.setAttribute("account", user.getUserId());
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
    public String signupHandler(@Valid @ModelAttribute("user") Users user,
                                BindingResult bindingResult,
                                ModelMap model,
                                @RequestParam("confirm_password") String confirmPassword,
                                @RequestParam(value="interests", required = false) String[] interestsArr ,
                                @RequestParam(value="personality", required = false) String[] personalityArr,
                                @RequestParam("image") MultipartFile[] parts) throws IOException {

        model.addAttribute("user", user); // ✅ 確保一開始就綁好 user，Thymeleaf 才不會炸掉

        // 1. Email 重複檢查
        if (userService.getUserByEmail(user.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.user", "此 Email 已註冊，請使用其他信箱");
            model.addAttribute("user", user);
            return "front-end/user/signup";
        }

        // 2. 密碼不一致
        if (!user.getPwd().equals(confirmPassword)) {
            model.addAttribute("pwdMismatch", "密碼與確認密碼不一致！");
            return "front-end/user/signup";
        }

        // 3. 檢查年齡
        int age = Period.between(user.getBirthday().toLocalDate(), LocalDate.now()).getYears();
        if(age < 15){
            model.addAttribute("errorMessage", "年齡需滿16歲");
            return "front-end/user/signup";
        }

        // 4. 忽略 MultipartFile 檢查錯誤
        bindingResult = removeFieldError(user, bindingResult, "image");

        // 5. 後端驗證失敗
        if (parts == null || parts.length == 0 || parts[0].isEmpty()) {
            model.addAttribute("errorMessage", "請上傳照片");
            return "front-end/user/signup";
        }

        // 6. 圖片上傳
        String imgUrl;
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
        if(bindingResult.hasErrors()){
            model.addAttribute("user", user);
            return "front-end/user/signup";
        }

        // 7. 註冊成功
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
