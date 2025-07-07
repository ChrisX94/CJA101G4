package com.shakemate.user.service;

import com.shakemate.shshop.util.ShShopRedisUtil;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import com.shakemate.util.MailService;
import com.shakemate.util.PasswordConvert;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private PasswordConvert pc;

    @Autowired
    private ShShopRedisUtil redisUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private UsersRepository usersRepository;

    public Users getUserByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public enum LoginResult {
        SUCCESS, WRONG_PASSWORD, USER_NOT_FOUND, ACCOUNT_SUSPENDED, ACCOUNT_DELETED
    }

    public LoginResult login(String email, String inputPassword) {
        Users user = usersRepo.findByEmail(email);
        if (user == null)
            return LoginResult.USER_NOT_FOUND;

        // 檢查帳戶狀態
        if (user.getUserStatus() == (byte) 2) {
            return LoginResult.ACCOUNT_SUSPENDED;
        }
        if (user.getUserStatus() == (byte) 3) {
            return LoginResult.ACCOUNT_DELETED;
        }
        if (user.getUserStatus() != 1) {
            return LoginResult.USER_NOT_FOUND;
        }

        if (!pc.passwordVerify(user.getPwd(), inputPassword))
            return LoginResult.WRONG_PASSWORD;
        return LoginResult.SUCCESS;
    }

    @Transactional
    public void signIn(Users user, String[] interestsArr, String[] personalityArr, String imgUrl) {
        String rowPassword = user.getPwd();
        user.setPwd(pc.hashing(rowPassword));
        user.setImg1(imgUrl);
        if (interestsArr != null && interestsArr.length > 0) {
            String interests = String.join(",", interestsArr);
            user.setInterests(interests);
        }
        if (personalityArr != null && personalityArr.length > 0) {
            String personality = String.join(",", personalityArr);
            user.setPersonality(personality);
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        user.setUserStatus((byte) 0);
        user.setPostStatus(false);
        user.setAtAcStatus(false);
        user.setSellStatus(false);
        usersRepo.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    public Users getUserById(Integer userId) {
        return usersRepo.findById(userId).orElse(null);
    }

    @Transactional
    public void updateUser(Users user) {
        user.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        usersRepo.save(user); // JPA save() 會自動根據 ID 做更新或新增
    }

    @Transactional
    public void deleteUser(Integer userId) {
        usersRepo.deleteById(userId);
    }

    public void sendResetPasswordEmail(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("此 Email 尚未註冊");
        }

        // 產生 token（UUID）
        String token = UUID.randomUUID().toString();

        // 存入 Redis（key: resetToken:xxx, value: userId, 有效 30 分鐘）
        redisUtil.setObject("resetToken:" + token, user.getUserId(), 1800); // 1800 秒 = 30 分鐘

        // 準備信件內容
        String resetLink = "http://localhost:8080/user/reset-password?token=" + token;
        String subject = "重設密碼通知";
        String content = "請點擊下列連結以重設密碼（30 分鐘內有效）：\n" + resetLink;

        mailService.sendMail(email, subject, content);
    }
}
