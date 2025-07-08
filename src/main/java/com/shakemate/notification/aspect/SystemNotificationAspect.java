package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.user.model.Users;
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
 * 系統通知AOP切面
 * 
 * 透過AOP機制攔截系統相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 用戶註冊歡迎（USER_REGISTRATION_WELCOME）
 * - 密碼變更確認（ACCOUNT_PASSWORD_CHANGED）
 * - 異常登入警告（ACCOUNT_LOGIN_ANOMALY）
 * - 帳號狀態變更（ACCOUNT_STATUS_CHANGED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SystemNotificationAspect {

    private final NotificationService notificationService;

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
     * 定義切點：攔截用戶資料更新操作（包含密碼變更）
     */
    @Pointcut("execution(* com.shakemate.user.service.UserService.updateUser(..))")
    public void userUpdatePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截管理員登入操作
     */
    @Pointcut("execution(* com.shakemate.adm.model.AdmService.findByAcc(..))")
    public void adminLoginPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理用戶註冊後的歡迎通知
     * 
     * 當用戶成功註冊後，發送歡迎通知
     * 
     * @param user 註冊的用戶對象
     * @param interestsArr 興趣陣列
     * @param personalityArr 性格陣列
     * @param imgUrl 頭像URL
     */
    @AfterReturning("userRegistrationPointcut() && args(user, interestsArr, personalityArr, imgUrl)")
    public void handleUserRegistration(Users user, String[] interestsArr, String[] personalityArr, String imgUrl) {
        try {
            log.info("AOP攔截到用戶註冊成功，用戶名: {}, Email: {}", 
                    user.getUsername(), user.getEmail());

            // 準備通知模板變數
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("user_name", user.getUsername());
            templateVariables.put("registration_date", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            templateVariables.put("user_email", user.getEmail());
            
            // 如果有興趣和性格資訊，也可以包含在歡迎訊息中
            if (interestsArr != null && interestsArr.length > 0) {
                templateVariables.put("user_interests", String.join(", ", interestsArr));
            }
            if (personalityArr != null && personalityArr.length > 0) {
                templateVariables.put("user_personality", String.join(", ", personalityArr));
            }

            // 發送註冊歡迎通知
            // 注意：這裡需要在用戶保存後獲取用戶ID，可能需要調整攔截時機
            // 暫時記錄日志，實際實現時需要在用戶保存完成後獲取ID
            log.info("用戶註冊歡迎通知準備發送，用戶: {}", user.getUsername());
            
            // TODO: 在用戶保存完成並有ID後發送通知
            // notificationService.sendTemplateNotification("USER_REGISTRATION_WELCOME", user.getUserId(), templateVariables);
            
        } catch (Exception e) {
            log.error("處理用戶註冊通知時發生錯誤，用戶: {}", user.getUsername(), e);
        }
    }

    /**
     * 處理用戶登入後的安全監控
     * 
     * 當用戶登入時，檢查是否有異常登入行為
     * 
     * @param result 登入結果
     * @param email 登入郵箱
     * @param inputPassword 輸入密碼（不記錄）
     */
    @AfterReturning(pointcut = "userLoginPointcut() && args(email, inputPassword)", returning = "result")
    public void handleUserLogin(Object result, String email, String inputPassword) {
        try {
            if (result instanceof LoginResult) {
                LoginResult loginResult = (LoginResult) result;
                
                log.info("AOP攔截到用戶登入嘗試，Email: {}, 結果: {}", email, loginResult);

                if (loginResult == LoginResult.SUCCESS) {
                    // 成功登入的安全監控
                    // 這裡可以實現登入行為分析，檢測異常登入
                    
                    // 準備通知模板變數（如果檢測到異常）
                    // Map<String, Object> templateVariables = new HashMap<>();
                    // templateVariables.put("login_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    // templateVariables.put("login_location", "台北市"); // 實際需要根據IP獲取位置
                    // templateVariables.put("login_device", "Web瀏覽器"); // 實際需要解析User-Agent
                    
                    // 如果檢測到異常登入行為，發送警告通知
                    // if (isAnomalousLogin(email)) {
                    //     notificationService.sendTemplateNotification("ACCOUNT_LOGIN_ANOMALY", userId, templateVariables);
                    // }
                    
                    log.info("用戶登入成功，Email: {}", email);
                } else {
                    // 登入失敗的記錄（不發送通知，避免騷擾）
                    log.warn("用戶登入失敗，Email: {}, 原因: {}", email, loginResult);
                }
            }
            
        } catch (Exception e) {
            log.error("處理用戶登入監控時發生錯誤，Email: {}", email, e);
        }
    }

    /**
     * 處理用戶資料更新後的通知
     * 
     * 當用戶更新資料（特別是密碼變更）時發送確認通知
     * 
     * @param user 更新後的用戶對象
     */
    @AfterReturning("userUpdatePointcut() && args(user)")
    public void handleUserUpdate(Users user) {
        try {
            log.info("AOP攔截到用戶資料更新，用戶ID: {}, 用戶名: {}", 
                    user.getUserId(), user.getUsername());

            // 檢查是否為密碼變更（這裡需要額外的邏輯來判斷是否為密碼更新）
            // 實際實現中可以通過比較更新時間或設置更新類型標記來判斷
            
            // 準備通知模板變數
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("user_name", user.getUsername());
            templateVariables.put("update_time", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            templateVariables.put("user_email", user.getEmail());

            // 如果是密碼變更，發送密碼變更確認通知
            // 這裡需要額外的邏輯來判斷具體的更新類型
            boolean isPasswordChange = isPasswordChangeUpdate(user);
            if (isPasswordChange) {
                notificationService.sendTemplateNotification(
                    "ACCOUNT_PASSWORD_CHANGED",
                    user.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送密碼變更通知失敗，用戶ID: {}", user.getUserId(), throwable);
                    return false;
                });
                
                log.info("密碼變更確認通知已發送，用戶ID: {}", user.getUserId());
            }

            // 檢查是否為帳號狀態變更
            if (isUserStatusChanged(user)) {
                templateVariables.put("new_status", getStatusDescription(user.getUserStatus()));
                
                notificationService.sendTemplateNotification(
                    "ACCOUNT_STATUS_CHANGED",
                    user.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送帳號狀態變更通知失敗，用戶ID: {}", user.getUserId(), throwable);
                    return false;
                });
                
                log.info("帳號狀態變更通知已發送，用戶ID: {}", user.getUserId());
            }
            
        } catch (Exception e) {
            log.error("處理用戶資料更新通知時發生錯誤，用戶ID: {}", user.getUserId(), e);
        }
    }

    /**
     * 判斷是否為密碼變更更新
     * 
     * 實際實現中需要更複雜的判斷邏輯，比如：
     * 1. 比較更新前後的密碼hash
     * 2. 檢查更新標記
     * 3. 檢查更新時間間隔等
     */
    private boolean isPasswordChangeUpdate(Users user) {
        // 實際實現中應該比較更新前後的密碼
        // 這裡提供簡化的判斷邏輯
        return false; // 暫時返回false，實際需要根據業務邏輯實現
    }

    /**
     * 判斷是否為用戶狀態變更
     */
    private boolean isUserStatusChanged(Users user) {
        // 實際實現中應該比較更新前後的狀態
        // 這裡提供簡化的判斷邏輯
        return user.getUserStatus() != 1; // 非正常狀態
    }

    /**
     * 獲取狀態描述
     */
    private String getStatusDescription(byte status) {
        switch (status) {
            case 0:
                return "停用";
            case 1:
                return "正常";
            case 2:
                return "凍結";
            default:
                return "未知狀態";
        }
    }

    /**
     * 檢測是否為異常登入
     * 
     * 實際實現中可以包含：
     * 1. IP位置檢查
     * 2. 登入時間分析
     * 3. 設備指紋比對
     * 4. 登入頻率分析等
     */
    private boolean isAnomalousLogin(String email) {
        // 實際實現中需要複雜的異常檢測邏輯
        // 這裡提供簡化的示例
        return false; // 暫時返回false，實際需要根據安全策略實現
    }
} 