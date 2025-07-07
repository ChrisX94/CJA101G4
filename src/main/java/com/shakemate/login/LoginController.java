package com.shakemate.login;

import com.shakemate.shshop.util.ShShopRedisUtil;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import com.shakemate.user.service.UserService.LoginResult;
import com.shakemate.user.util.UserPostMultipartFileUploader;
import com.shakemate.util.PasswordConvert;

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

    @Autowired
    private ShShopRedisUtil redisUtil;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordConvert passwordConvert;

    // 處理登入
    @PostMapping("/loginHandler")
    public String login(@RequestParam String account,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        LoginResult result = userService.login(account, password);
        switch (result) {
            case SUCCESS:
                // 登入成功，需要重新獲取用戶資料來設置 session
                Users user = userService.getUserByEmail(account);
                HttpSession session = request.getSession();
                session.setAttribute("account", user.getUserId());
                String location = (String) session.getAttribute("location");
                return "redirect:/";
            case ACCOUNT_SUSPENDED:
                model.addAttribute("error", "您的帳號已被停用，請聯繫客服! <br> <a href='/servicecase/sadd'>聯絡客服</a>");
                return "login";
            case ACCOUNT_DELETED:
                model.addAttribute("error", "您的帳號已被註銷，請重新註冊!");
                return "login";
            case USER_NOT_FOUND:
                model.addAttribute("error", "登入失敗，請檢查帳號密碼，若無帳號請先註冊!");
                return "login";
            default:
                model.addAttribute("error", "登入失敗，請稍後再試");
                return "login";
        }
    }

    // 處理登出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: 如果沒 session 就不建立
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/"; // 回首頁畫面
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new Users());
        return "signup";
    }

    @PostMapping("/signupHandler")
    public String signupHandler(@Valid @ModelAttribute("user") Users user,
            BindingResult bindingResult,
            ModelMap model,
            @RequestParam("confirm_password") String confirmPassword,
            @RequestParam(value = "interests", required = false) String[] interestsArr,
            @RequestParam(value = "personality", required = false) String[] personalityArr,
            @RequestParam("image") MultipartFile[] parts) throws IOException {

        model.addAttribute("user", user); // 確保一開始就綁好 user，Thymeleaf 才不會炸掉

        // 1. Email 重複檢查
        if (userService.getUserByEmail(user.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.user", "此 Email 已註冊，請使用其他信箱");
            model.addAttribute("user", user);
            return "signup";
        }

        // 2. 密碼不一致
        if (!user.getPwd().equals(confirmPassword)) {
            model.addAttribute("pwdMismatch", "密碼與確認密碼不一致！");
            return "signup";
        }

        // 3. 檢查年齡
        int age = Period.between(user.getBirthday().toLocalDate(), LocalDate.now()).getYears();
        if (age < 15) {
            model.addAttribute("errorMessage", "年齡需滿16歲");
            return "signup";
        }

        // 4. 忽略 MultipartFile 檢查錯誤
        bindingResult = removeFieldError(user, bindingResult, "image");

        // 5. 後端驗證失敗
        if (parts == null || parts.length == 0 || parts[0].isEmpty()) {
            model.addAttribute("errorMessage", "請上傳照片");
            return "signup";
        }

        // 6. 圖片上傳
        String imgUrl;
        try {
            imgUrl = imgHandler.uploadImageToImgbb(parts[0]);
            if (imgUrl == null || imgUrl.isBlank()) {
                model.addAttribute("errorMessage", "照片上傳失敗");
                return "signup";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "系統錯誤：" + e.getMessage());
            return "signup";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }

        // 7. 註冊成功
        userService.signIn(user, interestsArr, personalityArr, imgUrl);
        model.addAttribute("success", "- (新增成功)");

        return "/index";
    }

    @GetMapping("/testlogin")
    public String testLogin(HttpServletRequest request) {
        return "/index"; // 回登入畫面
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

    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        model.addAttribute("user", new Users());
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            userService.sendResetPasswordEmail(email);
            model.addAttribute("message", "重設密碼信已發送，請檢查您的信箱");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // 顯示「此 Email 尚未註冊」
        } catch (Exception e) {
            model.addAttribute("error", "系統錯誤，請稍後再試");
        }
        return "forgotPassword";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Integer userId = redisUtil.getObject("resetToken:" + token, Integer.class);
        if (userId == null) {
            model.addAttribute("error", "連結已失效或錯誤");
            return "reset_password_error";
        }

        model.addAttribute("token", token);
        return "reset_password_form"; // 顯示輸入新密碼頁面
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            Model model) {
        Integer userId = redisUtil.getObject("resetToken:" + token, Integer.class);
        if (userId == null) {
            model.addAttribute("error", "連結已過期或無效");
            return "reset_password_error";
        }

        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("error", "帳號不存在");
            return "reset_password_error";
        }

        user.setPwd(passwordConvert.hashing(newPassword));
        usersRepository.save(user);
        redisUtil.delete("resetToken:" + token);

        return "reset_password_success";
    }

}
