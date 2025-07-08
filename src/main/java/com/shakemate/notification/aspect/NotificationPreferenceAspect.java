package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.notification.dto.NotificationPreferenceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知偏好設定AOP切面
 * 
 * 透過AOP機制攔截偏好設定的變更操作並發送通知
 * 支援的通知場景：
 * - 偏好設定更新確認（NOTIFICATION_PREFERENCES_UPDATED）
 * - 偏好設定建立通知（NOTIFICATION_PREFERENCES_CREATED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationPreferenceAspect {

    private final NotificationService notificationService;

    /**
     * 定義切點：攔截偏好設定更新操作
     */
    @Pointcut("execution(* com.shakemate.notification.service.*PreferenceService.updatePreferences(..))")
    public void preferenceUpdatePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截偏好設定批量更新操作
     */
    @Pointcut("execution(* com.shakemate.notification.service.*PreferenceService.updatePreferencesWithResult(..))")
    public void preferenceUpdateWithResultPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截偏好設定創建操作
     */
    @Pointcut("execution(* com.shakemate.notification.service.*PreferenceService.createDefaultPreferences(..))")
    public void preferenceCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理偏好設定更新後的通知
     * 
     * 當用戶更新通知偏好設定後，發送設定更新確認通知
     * 
     * @param userId 用戶ID
     * @param dto 偏好設定DTO
     */
    @AfterReturning("preferenceUpdatePointcut() && args(userId, dto)")
    public void handlePreferenceUpdate(Integer userId, NotificationPreferenceDto dto) {
        try {
            log.info("AOP攔截到偏好設定更新，用戶ID: {}, 類別: {}", userId, dto.getNotificationCategory());

            // 準備通知模板變數
            Map<String, Object> templateVariables = buildPreferenceUpdateVariables(dto);
            
            // 發送偏好設定更新確認通知
            // 這種通知通常只發送站內通知，避免干擾用戶
            notificationService.sendTemplateNotification(
                "NOTIFICATION_PREFERENCES_UPDATED",
                userId,
                templateVariables
            ).exceptionally(throwable -> {
                log.error("發送偏好設定更新通知失敗，用戶ID: {}, 類別: {}", 
                        userId, dto.getNotificationCategory(), throwable);
                return false;
            });

            log.info("偏好設定更新通知已發送，用戶ID: {}, 類別: {}", userId, dto.getNotificationCategory());
            
        } catch (Exception e) {
            log.error("處理偏好設定更新通知時發生錯誤，用戶ID: {}, 類別: {}", 
                    userId, dto.getNotificationCategory(), e);
        }
    }

    /**
     * 處理偏好設定批量更新後的通知
     * 
     * 當用戶批量更新通知偏好設定後，發送統一的設定更新確認通知
     * 
     * @param result 更新結果DTO
     * @param userId 用戶ID
     * @param dto 偏好設定DTO
     */
    @AfterReturning(pointcut = "preferenceUpdateWithResultPointcut() && args(userId, dto)", returning = "result")
    public void handlePreferenceUpdateWithResult(Object result, Integer userId, NotificationPreferenceDto dto) {
        try {
            if (result instanceof NotificationPreferenceDto) {
                NotificationPreferenceDto updatedDto = (NotificationPreferenceDto) result;
                
                log.info("AOP攔截到偏好設定批量更新成功，用戶ID: {}, 類別: {}", 
                        userId, updatedDto.getNotificationCategory());

                // 準備通知模板變數
                Map<String, Object> templateVariables = buildPreferenceUpdateVariables(updatedDto);
                
                // 發送偏好設定更新確認通知（僅站內通知）
                notificationService.sendTemplateNotification(
                    "NOTIFICATION_PREFERENCES_UPDATED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送偏好設定批量更新通知失敗，用戶ID: {}, 類別: {}", 
                            userId, updatedDto.getNotificationCategory(), throwable);
                    return false;
                });

                log.info("偏好設定批量更新通知已發送，用戶ID: {}, 類別: {}", 
                        userId, updatedDto.getNotificationCategory());
            }
            
        } catch (Exception e) {
            log.error("處理偏好設定批量更新通知時發生錯誤，用戶ID: {}, 類別: {}", 
                    userId, dto.getNotificationCategory(), e);
        }
    }

    /**
     * 處理偏好設定創建後的通知
     * 
     * 當用戶首次建立預設偏好設定後，發送歡迎通知
     * 
     * @param result 創建的偏好設定列表
     * @param userId 用戶ID
     */
    @AfterReturning(pointcut = "preferenceCreationPointcut() && args(userId)", returning = "result")
    public void handlePreferenceCreation(Object result, Integer userId) {
        try {
            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<NotificationPreferenceDto> preferences = (List<NotificationPreferenceDto>) result;
                
                log.info("AOP攔截到偏好設定創建成功，用戶ID: {}, 創建類別數量: {}", 
                        userId, preferences.size());

                // 準備通知模板變數
                Map<String, Object> templateVariables = buildPreferenceCreationVariables(preferences);
                
                // 發送偏好設定創建歡迎通知
                notificationService.sendTemplateNotification(
                    "NOTIFICATION_PREFERENCES_CREATED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送偏好設定創建通知失敗，用戶ID: {}", userId, throwable);
                    return false;
                });

                log.info("偏好設定創建通知已發送，用戶ID: {}, 類別數量: {}", userId, preferences.size());
            }
            
        } catch (Exception e) {
            log.error("處理偏好設定創建通知時發生錯誤，用戶ID: {}", userId, e);
        }
    }

    /**
     * 構建偏好設定更新通知的模板變數
     */
    private Map<String, Object> buildPreferenceUpdateVariables(NotificationPreferenceDto dto) {
        Map<String, Object> variables = new HashMap<>();
        
        // 基本偏好設定資訊
        variables.put("preference_category", dto.getNotificationCategory());
        variables.put("email_enabled", dto.getEmailEnabled() != null ? dto.getEmailEnabled() : false);
        variables.put("sms_enabled", dto.getSmsEnabled() != null ? dto.getSmsEnabled() : false);
        variables.put("push_enabled", dto.getPushEnabled() != null ? dto.getPushEnabled() : false);
        variables.put("in_app_enabled", dto.getInAppEnabled() != null ? dto.getInAppEnabled() : false);
        
        // 勿擾時段設定
        variables.put("quiet_hours_enabled", dto.getQuietHoursEnabled() != null ? dto.getQuietHoursEnabled() : false);
        if (dto.getQuietHoursStart() != null && dto.getQuietHoursEnd() != null) {
            variables.put("quiet_hours_range", 
                    dto.getQuietHoursStart().toString() + " - " + dto.getQuietHoursEnd().toString());
        } else {
            variables.put("quiet_hours_range", "未設定");
        }
        
        // 更新時間
        variables.put("update_time", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        log.debug("構建偏好設定更新通知變數: {}", variables);
        return variables;
    }

    /**
     * 構建偏好設定創建通知的模板變數
     */
    private Map<String, Object> buildPreferenceCreationVariables(List<NotificationPreferenceDto> preferences) {
        Map<String, Object> variables = new HashMap<>();
        
        // 基本資訊
        variables.put("categories_count", preferences.size());
        variables.put("creation_time", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 建立的類別列表
        List<String> categoryNames = preferences.stream()
                .map(NotificationPreferenceDto::getNotificationCategory)
                .collect(java.util.stream.Collectors.toList());
        variables.put("categories_list", String.join("、", categoryNames));
        
        // 預設設定說明
        variables.put("default_email_enabled", true);
        variables.put("default_push_enabled", true);
        variables.put("default_quiet_hours", "22:00 - 08:00");
        
        log.debug("構建偏好設定創建通知變數: {}", variables);
        return variables;
    }
} 