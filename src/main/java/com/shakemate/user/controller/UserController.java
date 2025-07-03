package com.shakemate.user.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import com.shakemate.util.PasswordConvert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

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

        List<String> photoList = new ArrayList<>();
        if (user.getImg1() != null)
            photoList.add(user.getImg1());
        if (user.getImg2() != null)
            photoList.add(user.getImg2());
        if (user.getImg3() != null)
            photoList.add(user.getImg3());
        if (user.getImg4() != null)
            photoList.add(user.getImg4());
        if (user.getImg5() != null)
            photoList.add(user.getImg5());
        user.setPhotos(photoList); // 這要你在 Users.java 裡有 photos 屬性和 setter

        return "/front-end/user/profile";

    }

    // 顯示修改個人資料頁面
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

        // 所有選項清單（可自由擴充）
        List<String> allInterests = List.of(
                "打籃球", "旅遊", "烹飪", "閱讀", "健身", "看電影", "聽音樂", "攝影", "登山", "衝浪", "游泳", "瑜伽",
                "繪畫", "寫作", "遊戲", "美食", "咖啡", "貓奴", "狗奴", "逛街", "動漫", "園藝", "樂器", "唱歌", "跳舞", "志工");


        List<String> allTraits = List.of(
                "外向", "樂觀", "開朗", "內向", "沈穩", "細心", "幽默", "有耐心", "有活力", "隨和", "獨立", "創意",
                "負責", "認真", "友善", "善良", "熱情", "冷靜");

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

        // 把勾選的 list 轉回字串欄位存入資料庫
        user.setInterests(String.join(",", user.getInterestsList()));
        user.setPersonality(String.join(",", user.getPersonalityList()));

        Users original = userService.getUserById(user.getUserId());
        user.setPwd(original.getPwd());
        user.setCreatedTime(original.getCreatedTime());
        user.setUpdatedTime(Timestamp.from(Instant.now()));
        user.setUserStatus(original.getUserStatus());
        user.setPostStatus(original.getPostStatus());
        user.setAtAcStatus(original.getAtAcStatus());
        user.setSellStatus(original.getSellStatus());

        // 圖片處理
        MultipartFile[] files = user.getUploadedImages();
        String uploadDir = "C:/shakemate/uploads/"; // ← 改成你自己的資料夾

        for (int i = 0; i < Math.min(files.length, 5); i++) {
            MultipartFile file = files[i];
            if (!file.isEmpty()) {
                try {
                    String filename = "user_" + user.getUserId() + "_img" + (i + 1) + "_" + System.currentTimeMillis()
                            + ".jpg";
                    File saveFile = new File(uploadDir + filename);
                    file.transferTo(saveFile);
                    String dbPath = "/uploads/" + filename; // 存在 DB 的相對路徑

                    switch (i) {
                        case 0 -> user.setImg1(dbPath);
                        case 1 -> user.setImg2(dbPath);
                        case 2 -> user.setImg3(dbPath);
                        case 3 -> user.setImg4(dbPath);
                        case 4 -> user.setImg5(dbPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "圖片上傳失敗");
                    return "redirect:/user/updateProfile";
                }
            }
        }
        // 實際更新 DB
        userService.updateUser(user);

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

    // 刪除帳號
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null)
            return "redirect:/user/login";

        userService.deleteUser(userId);
        session.invalidate();
        return "redirect:/login";
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
