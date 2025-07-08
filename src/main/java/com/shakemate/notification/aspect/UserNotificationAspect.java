package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.user.model.Users;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.service.UserService.LoginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用戶模組通知AOP切面
 * 
 * 透過AOP機制攔截用戶相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 用戶註冊歡迎（USER_REGISTRATION_WELCOME）
 * - 用戶資料更新（USER_PROFILE_UPDATED）
 * - 密碼變更確認（USER_PASSWORD_CHANGED）
 * - 異常登入警告（USER_LOGIN_ANOMALY）
 * - 帳號狀態變更（USER_ACCOUNT_STATUS_CHANGED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;

    /**
     * 定義切點：攔截用戶註冊操作
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.signIn(..))")
    public void userRegistrationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截用戶登入操作
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.login(..))")
    public void userLoginPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截用戶資料更新操作
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.updateUser(..))")
    public void userUpdatePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截用戶密碼變更操作
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.changePassword(..))")
    public void passwordChangePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截用戶狀態變更操作
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.updateUserStatus(..))")
    public void userStatusChangePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理用戶註冊後的歡迎通知
     * 
     * 當用戶成功註冊後，發送歡迎通知
     * 
     * @param result 註冊結果
     * @param user 註冊的用戶對象
     * @param interestsArr 興趣陣列
     * @param personalityArr 性格陣列
     * @param imgUrl 頭像URL
     */
    @AfterReturning(pointcut = "userRegistrationPointcut() && args(user, interestsArr, personalityArr, imgUrl)", returning = "result")
    public void handleUserRegistration(Object result, Users user, Object interestsArr, Object personalityArr, String imgUrl) {
        try {
            if (result instanceof Users) {
                Users registeredUser = (Users) result;
                
                log.info("AOP攔截到用戶註冊，用戶ID: {}, 用戶名: {}", 
                        registeredUser.getUserId(), registeredUser.getUsername());

                // 準備歡迎通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("user_id", registeredUser.getUserId());
                templateVariables.put("user_name", registeredUser.getUsername());
                templateVariables.put("user_email", registeredUser.getEmail());
                templateVariables.put("registration_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                templateVariables.put("welcome_message", "歡迎加入ShakeMate交友平台！");
                
                // 如果有頭像，加入頭像信息
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    templateVariables.put("has_avatar", true);
                    templateVariables.put("avatar_url", imgUrl);
                } else {
                    templateVariables.put("has_avatar", false);
                }

                // 發送註冊歡迎通知
                notificationService.sendTemplateNotification(
                    "USER_REGISTRATION_WELCOME",
                    registeredUser.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送註冊歡迎通知失敗，用戶ID: {}", registeredUser.getUserId(), throwable);
                    return false;
                });

                log.info("註冊歡迎通知已發送，用戶ID: {}", registeredUser.getUserId());
            }
            
        } catch (Exception e) {
            log.error("處理用戶註冊通知時發生錯誤", e);
        }
    }

    /**
     * 處理用戶登入後的異常檢測
     * 
     * 當用戶登入時，檢測是否有異常登入行為
     * 
     * @param result 登入結果
     * @param userAcc 用戶帳號
     * @param userPwd 用戶密碼
     */
    @AfterReturning(pointcut = "userLoginPointcut() && args(userAcc, userPwd)", returning = "result")
    public void handleUserLogin(Object result, String userAcc, String userPwd) {
        try {
            if (result instanceof LoginResult) {
                LoginResult loginResult = (LoginResult) result;
                
                // 只有登入成功才進行檢測
                if (loginResult == LoginResult.SUCCESS) {
                    // 通過email查詢用戶
                    Users user = usersRepository.findByEmail(userAcc);
                    if (user == null) {
                        log.warn("無法找到用戶數據: userAcc={}", userAcc);
                        return;
                    }
                    
                    log.info("AOP攔截到用戶登入，用戶ID: {}, 用戶名: {}", 
                            user.getUserId(), user.getUsername());

                    // 檢測異常登入行為（這裡可以實現複雜的異常檢測邏輯）
                    if (isAnomalousLogin(user)) {
                        // 準備異常登入警告通知模板變數
                        Map<String, Object> templateVariables = new HashMap<>();
                        templateVariables.put("user_id", user.getUserId());
                        templateVariables.put("user_name", user.getUsername());
                        templateVariables.put("login_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        templateVariables.put("login_ip", "未知IP"); // 可以從請求中獲取實際IP
                        templateVariables.put("login_location", "未知地點"); // 可以基於IP獲取地理位置

                        // 發送異常登入警告通知
                        notificationService.sendTemplateNotification(
                            "USER_LOGIN_ANOMALY",
                            user.getUserId(),
                            templateVariables
                        ).exceptionally(throwable -> {
                            log.error("發送異常登入警告通知失敗，用戶ID: {}", user.getUserId(), throwable);
                            return false;
                        });

                        log.info("異常登入警告通知已發送，用戶ID: {}", user.getUserId());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("處理用戶登入通知時發生錯誤", e);
        }
    }

    /**
     * 處理用戶資料更新後的通知
     * 
     * 當用戶更新個人資料時，發送更新確認通知
     * 
     * @param result 更新結果
     * @param user 更新的用戶對象
     */
    @AfterReturning(pointcut = "userUpdatePointcut() && args(user)", returning = "result")
    public void handleUserProfileUpdate(Object result, Users user) {
        try {
            if (result instanceof Users) {
                Users updatedUser = (Users) result;
                
                log.info("AOP攔截到用戶資料更新，用戶ID: {}, 用戶名: {}", 
                        updatedUser.getUserId(), updatedUser.getUsername());

                // 準備資料更新通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("user_id", updatedUser.getUserId());
                templateVariables.put("user_name", updatedUser.getUsername());
                templateVariables.put("user_email", updatedUser.getEmail());
                templateVariables.put("updated_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                templateVariables.put("update_message", "您的個人資料已成功更新");

                // 發送資料更新確認通知
                notificationService.sendTemplateNotification(
                    "USER_PROFILE_UPDATED",
                    updatedUser.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送資料更新通知失敗，用戶ID: {}", updatedUser.getUserId(), throwable);
                    return false;
                });

                log.info("資料更新確認通知已發送，用戶ID: {}", updatedUser.getUserId());
            }
            
        } catch (Exception e) {
            log.error("處理用戶資料更新通知時發生錯誤", e);
        }
    }

    /**
     * 處理密碼變更後的確認通知
     * 
     * 當用戶變更密碼時，發送變更確認通知
     * 
     * @param result 變更結果
     * @param userId 用戶ID
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     */
    @AfterReturning(pointcut = "passwordChangePointcut() && args(userId, oldPassword, newPassword)", returning = "result")
    public void handlePasswordChange(Object result, Integer userId, String oldPassword, String newPassword) {
        try {
            if (Boolean.TRUE.equals(result)) {
                // 查詢用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                if (user == null) {
                    log.warn("無法找到用戶數據: userId={}", userId);
                    return;
                }
                
                log.info("AOP攔截到密碼變更，用戶ID: {}, 用戶名: {}", userId, user.getUsername());

                // 準備密碼變更確認通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("user_id", userId);
                templateVariables.put("user_name", user.getUsername());
                templateVariables.put("user_email", user.getEmail());
                templateVariables.put("changed_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                templateVariables.put("security_message", "如果這不是您本人的操作，請立即聯繫客服");

                // 發送密碼變更確認通知
                notificationService.sendTemplateNotification(
                    "USER_PASSWORD_CHANGED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送密碼變更確認通知失敗，用戶ID: {}", userId, throwable);
                    return false;
                });

                log.info("密碼變更確認通知已發送，用戶ID: {}", userId);
            }
            
        } catch (Exception e) {
            log.error("處理密碼變更通知時發生錯誤，用戶ID: {}", userId, e);
        }
    }

    /**
     * 處理用戶狀態變更後的通知
     * 
     * 當管理員變更用戶狀態時，發送狀態變更通知
     * 
     * @param result 變更結果
     * @param userId 用戶ID
     * @param newStatus 新狀態
     */
    @AfterReturning(pointcut = "userStatusChangePointcut() && args(userId, newStatus)", returning = "result")
    public void handleUserStatusChange(Object result, Integer userId, Integer newStatus) {
        try {
            if (Boolean.TRUE.equals(result)) {
                // 查詢用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                if (user == null) {
                    log.warn("無法找到用戶數據: userId={}", userId);
                    return;
                }
                
                log.info("AOP攔截到用戶狀態變更，用戶ID: {}, 新狀態: {}", userId, newStatus);

                // 準備狀態變更通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("user_id", userId);
                templateVariables.put("user_name", user.getUsername());
                templateVariables.put("user_email", user.getEmail());
                templateVariables.put("new_status", getStatusDescription(newStatus));
                templateVariables.put("changed_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                templateVariables.put("contact_message", "如有疑問，請聯繫客服");

                // 發送帳號狀態變更通知
                notificationService.sendTemplateNotification(
                    "USER_ACCOUNT_STATUS_CHANGED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送帳號狀態變更通知失敗，用戶ID: {}", userId, throwable);
                    return false;
                });

                log.info("帳號狀態變更通知已發送，用戶ID: {}, 新狀態: {}", userId, newStatus);
            }
            
        } catch (Exception e) {
            log.error("處理用戶狀態變更通知時發生錯誤，用戶ID: {}", userId, e);
        }
    }

    /**
     * 檢測是否為異常登入
     * 
     * 這裡可以實現複雜的異常檢測邏輯，例如：
     * - 異常時間登入
     * - 異常地點登入
     * - 短時間內多次登入失敗後成功
     * - 新設備登入
     * 
     * @param user 用戶對象
     * @return 是否為異常登入
     */
    private boolean isAnomalousLogin(Users user) {
        // 示例檢測邏輯：凌晨2-6點登入視為異常
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        if (hour >= 2 && hour <= 6) {
            log.info("檢測到凌晨時段登入，用戶ID: {}", user.getUserId());
            return true;
        }
        
        // 可以添加更多檢測邏輯
        // 例如：檢查上次登入時間、登入地點等
        
        return false;
    }

    /**
     * 獲取用戶狀態描述
     * 
     * @param status 狀態代碼
     * @return 狀態描述
     */
    private String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知狀態";
        }
        
        return switch (status) {
            case 0 -> "正常";
            case 1 -> "暫停";
            case 2 -> "停用";
            case 3 -> "封鎖";
            default -> "未知狀態";
        };
    }
} 