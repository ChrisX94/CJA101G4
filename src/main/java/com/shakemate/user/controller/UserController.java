package com.shakemate.user.controller;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import com.shakemate.util.PasswordConvert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordConvert passwordConvert;

    // 顯示註冊頁面
    @GetMapping("/signup")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Users());
        return "front-end/user/signup";
    }

    // 處理註冊提交
    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("user") Users user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (userService.getUserByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error", "此 Email 已被註冊！");
            return "redirect:/user/signup";
        }

        if (user.getPwd() == null || user.getPwd().isBlank()) {
            result.rejectValue("pwd", "error.pwd", "密碼不能空白");
        } else if (user.getPwd().length() < 6) {
            result.rejectValue("pwd", "error.pwd", "密碼至少要 6 碼");
        }

        if (result.hasErrors()) {
            return "front-end/user/signup";
        }

        // 使用 userService.signIn 處理註冊（含密碼加密與欄位處理）
        userService.signIn(user, null, null, null);

        redirectAttributes.addFlashAttribute("success", "註冊成功，請登入！");
        return "redirect:/user/login";
    }

    // 顯示會員個人資料
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/user/login";
        model.addAttribute("user", user);
        return "front-end/user/profile";
    }

    // 顯示修改個人資料頁面
    @GetMapping("/updateProfile")
    public String showUpdateForm(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/user/login";
        model.addAttribute("user", user);
        return "front-end/user/update_profile";
    }

    // 提交修改個資
    @PostMapping("/updateProfile")
    public String update(@Valid @ModelAttribute("user") Users user,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "front-end/user/update_profile";
        }

        Users original = userService.getUserById(user.getUserId());
        user.setPwd(original.getPwd());
        user.setCreatedTime(original.getCreatedTime());
        user.setUpdatedTime(Timestamp.from(Instant.now()));
        user.setUserStatus(original.getUserStatus());
        user.setPostStatus(original.getPostStatus());
        user.setAtAcStatus(original.getAtAcStatus());
        user.setSellStatus(original.getSellStatus());

        // 直接使用 repository 更新（你可以額外包一層 userService.updateUser(user)）
        // userService.updateUser(user);
        // 此處為簡化版，視專案實作調整
        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("success", "修改成功！");
        return "redirect:/user/profile";
    }

    // 顯示修改密碼頁面
    @GetMapping("/changePassword")
    public String showChangePwdForm() {
        return "front-end/user/change_password";
    }

    // 處理密碼修改
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPwd,
            @RequestParam String newPwd,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/user/login";

        if (!passwordConvert.passwordVerify(user.getPwd(), oldPwd)) {
            redirectAttributes.addFlashAttribute("error", "舊密碼錯誤");
            return "redirect:/user/changePassword";
        }

        user.setPwd(passwordConvert.hashing(newPwd));
        user.setUpdatedTime(Timestamp.from(Instant.now()));
        // userService.updateUser(user); // 若你未實作 updateUser，可直接用 repository.save(user)
        redirectAttributes.addFlashAttribute("success", "密碼修改成功！");
        return "redirect:/user/profile";
    }

    // 刪除帳號
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/user/login";

        // userService.deleteUser(user.getUserId());
        session.invalidate();
        return "redirect:/";
    }

    @Transactional
    public void updateUser(Users user) {
        user.setUpdatedTime(new Timestamp(System.currentTimeMillis())); // 更新時間戳記
        usersRepository.save(user); // JPA 自動判斷是更新還是新增（根據主鍵 userId）
    }

    @Transactional
    public void deleteUser(Integer userId) {
        usersRepository.deleteById(userId);
    }

}
