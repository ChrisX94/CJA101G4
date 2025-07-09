package com.shakemate.user.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
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

    // 獲取當前用戶ID的API端點
    @GetMapping("/api/current-id")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUserId(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "用戶未登錄");
            return ResponseEntity.status(401).body(response);
        }
        
        response.put("success", true);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    // 顯示會員個人資料
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // 從 session 中獲取用戶ID
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null)
            return "redirect:/user/login";
        // 這裡用 userId 再去資料庫撈一次最新資料
        Users user = userService.getUserById(userId);

        // 把原本資料庫的字串欄位，轉成 list
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            user.setInterestsList(Arrays.asList(user.getInterests().split(",")));
        }
        if (user.getPersonality() != null && !user.getPersonality().isEmpty()) {
            user.setPersonalityList(Arrays.asList(user.getPersonality().split(",")));
        }

        model.addAttribute("user", user);

        List<String> allInterests = List.of(
                "打籃球", "健身", "烹飪", "投資", "旅遊", "攝影", "閱讀", "追劇", "打電動", "聽音樂", "寫作", "手作");

        List<String> allTraits = List.of(
                "開朗", "樂觀", "陽光", "文靜", "務實", "溫柔", "體貼", "細心", "幽默",
                "貼心", "浪漫", "靦腆", "感性", "理性", "穩重", "神秘");
        model.addAttribute("allInterests", allInterests);
        model.addAttribute("allTraits", allTraits);

        // 新寫法：帶上欄位位置
        List<Map<String, String>> photoList = new ArrayList<>();
        if (user.getImg1() != null)
            photoList.add(Map.of("url", user.getImg1(), "pos", "0"));
        if (user.getImg2() != null)
            photoList.add(Map.of("url", user.getImg2(), "pos", "1"));
        if (user.getImg3() != null)
            photoList.add(Map.of("url", user.getImg3(), "pos", "2"));
        if (user.getImg4() != null)
            photoList.add(Map.of("url", user.getImg4(), "pos", "3"));
        if (user.getImg5() != null)
            photoList.add(Map.of("url", user.getImg5(), "pos", "4"));
        user.setPhotos(photoList);

        return "front-end/user/profile";
    }

    // 顯示修改個人資料頁面
    @GetMapping("/updateProfile")
    public String showUpdateForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null)
            return "redirect:/user/login";
        Users user = userService.getUserById(userId);

        // 把原本資料庫的字串欄位，轉成 list
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            user.setInterestsList(Arrays.asList(user.getInterests().split(",")));
        }
        if (user.getPersonality() != null && !user.getPersonality().isEmpty()) {
            user.setPersonalityList(Arrays.asList(user.getPersonality().split(",")));
        }

        // 所有選項清單
        List<String> allInterests = List.of(
                "打籃球", "健身", "烹飪", "投資", "旅遊", "攝影", "閱讀", "追劇", "打電動", "聽音樂", "寫作", "手作");

        List<String> allTraits = List.of(
                "開朗", "樂觀", "陽光", "文靜", "務實", "溫柔", "體貼", "細心", "幽默",
                "貼心", "浪漫", "靦腆", "感性", "理性", "穩重", "神秘");

        model.addAttribute("user", user);
        model.addAttribute("allInterests", allInterests);
        model.addAttribute("allTraits", allTraits);

        MultipartFile[] files = user.getUploadedImages();

        for (int i = 0; i < Math.min(files.length, 5); i++) {
            MultipartFile file = files[i];
            if (!file.isEmpty()) {
                try {
                    byte[] imageBytes = file.getBytes(); // 取得圖片 byte 陣列
                    String base64 = Base64.getEncoder().encodeToString(imageBytes); // 編碼成 Base64

                    switch (i) {
                        case 0 -> user.setImg1(base64);
                        case 1 -> user.setImg2(base64);
                        case 2 -> user.setImg3(base64);
                        case 3 -> user.setImg4(base64);
                        case 4 -> user.setImg5(base64);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "圖片上傳失敗");
                    return "redirect:/user/updateProfile";
                }
            }
        }

        return "front-end/user/profile";
    }

    // 提交修改個資
    @PostMapping("/updateProfile")
    public String update(@RequestParam("userId") Integer userId,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("birthday") String birthday,
            @RequestParam("location") String location,
            @RequestParam("intro") String intro,
            @RequestParam(value = "interestsList", required = false) List<String> interestsList,
            @RequestParam(value = "personalityList", required = false) List<String> personalityList,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 從 session 獲取當前用戶ID進行驗證
        Integer sessionUserId = (Integer) session.getAttribute("account");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "無權限修改此用戶資料");
            return "redirect:/user/profile";
        }

        // 獲取原始用戶資料
        Users original = userService.getUserById(userId);
        if (original == null) {
            redirectAttributes.addFlashAttribute("error", "用戶不存在");
            return "redirect:/user/profile";
        }

        // 更新用戶資料
        original.setUsername(username);
        original.setEmail(email);
        original.setLocation(location);
        original.setIntro(intro);
        original.setUpdatedTime(Timestamp.from(Instant.now()));

        // 處理密碼更新
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            // 密碼格式檢查（英數混合+特殊符號+至少8碼）
            if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
                redirectAttributes.addFlashAttribute("error", "密碼需至少8碼，且包含英文字母、數字與特殊符號");
                return "redirect:/user/profile";
            }
            // 密碼加密後更新
            original.setPwd(passwordConvert.hashing(newPassword));
        }

        // 處理興趣和個性特徵
        if (interestsList != null && !interestsList.isEmpty()) {
            original.setInterests(String.join(",", interestsList));
        } else {
            original.setInterests("");
        }

        if (personalityList != null && !personalityList.isEmpty()) {
            original.setPersonality(String.join(",", personalityList));
        } else {
            original.setPersonality("");
        }

        // 實際更新 DB
        userService.updateUser(original);

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
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null)
            return "redirect:/user/login";
        Users user = userService.getUserById(userId);

        if (!passwordConvert.passwordVerify(user.getPwd(), oldPwd)) {
            redirectAttributes.addFlashAttribute("error", "舊密碼錯誤");
            return "redirect:/user/changePassword";
        }

        user.setPwd(passwordConvert.hashing(newPwd));
        user.setUpdatedTime(Timestamp.from(Instant.now()));
        userService.updateUser(user); // 若你未實作 updateUser，可直接用 repository.save(user)
        redirectAttributes.addFlashAttribute("success", "密碼修改成功！");
        return "redirect:/user/profile";
    }

    // 註銷帳號
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteAccount(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("account");
        if (userId != null) {
            Users user = userService.getUserById(userId);
            if (user != null) {
                user.setUserStatus((byte) 3);
                userService.updateUser(user);
                session.invalidate();
                result.put("success", true);
                return result;
            }
        }
        result.put("success", false);
        result.put("message", "找不到用戶");
        return result;
    }

    @PostMapping("/banUser")
    public String banUser(@RequestParam Integer userId) {
        Users user = userService.getUserById(userId);
        if (user != null) {
            user.setUserStatus((byte) 2);
            userService.updateUser(user);

        }
        return "redirect:/login";
    }

}
