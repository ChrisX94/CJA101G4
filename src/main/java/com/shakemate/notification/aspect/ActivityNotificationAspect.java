package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.activity.dto.response.ActivityCreateResponse;
import com.shakemate.activity.dto.response.ActivityParticipantResponse;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.dao.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 活動通知AOP切面
 * 
 * 透過AOP機制攔截活動相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 活動報名申請（ACTIVITY_APPLICATION_RECEIVED）
 * - 活動申請核准（ACTIVITY_APPLICATION_APPROVED）
 * - 活動申請拒絕（ACTIVITY_APPLICATION_REJECTED）
 * - 新活動發布（ACTIVITY_PUBLISHED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;
    private final ActivityRepository activityRepository;

    /**
     * 定義切點：攔截活動創建操作
     */
    @Pointcut("execution(* com.shakemate.activity.service.*Service.createActivityAllInOne(..))")
    public void activityCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截活動報名申請操作
     */
    @Pointcut("execution(* com.shakemate.activity.service.*Service.createApply(..))")
    public void activityApplicationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截活動申請核准操作
     */
    @Pointcut("execution(* com.shakemate.activity.service.*Service.approveApplicant(..))")
    public void activityApprovalPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截活動申請拒絕操作
     */
    @Pointcut("execution(* com.shakemate.activity.service.*Service.rejectApplicant(..))")
    public void activityRejectionPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理活動創建後的通知
     * 
     * 當活動創建成功後，可選擇性發送活動發布通知給相關用戶
     * 
     * @param result 活動創建響應
     */
    @AfterReturning(pointcut = "activityCreationPointcut()", returning = "result")
    public void handleActivityCreated(Object result) {
        try {
            if (result instanceof ActivityCreateResponse) {
                ActivityCreateResponse response = (ActivityCreateResponse) result;
                
                log.info("AOP攔截到活動創建成功，活動ID: {}, 標題: {}", 
                        response.getActivityId(), response.getCreateRequest().getTitle());

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("activity_id", response.getActivityId());
                templateVariables.put("activity_title", response.getCreateRequest().getTitle());
                templateVariables.put("activity_location", response.getCreateRequest().getLocation());
                templateVariables.put("organizer_name", "活動主辦者");
                
                // 這裡可以發送活動發布通知給特定的用戶群組
                // 例如：對該類型活動感興趣的用戶、附近區域的用戶等
                // 為了演示，這裡暫時記錄日志
                log.info("活動發布通知準備就緒，活動ID: {}", response.getActivityId());
                
                // TODO: 實現活動發布通知邏輯
                // 可以根據活動標籤、地區等條件篩選目標用戶
                // notificationService.sendTemplateNotification("ACTIVITY_PUBLISHED", targetUserId, templateVariables);
            }
            
        } catch (Exception e) {
            log.error("處理活動創建通知時發生錯誤", e);
        }
    }

    /**
     * 處理活動報名申請後的通知
     * 
     * 當用戶報名活動後，向活動主辦者發送新申請通知
     * 
     * @param result 報名響應
     * @param activityId 活動ID
     * @param userId 申請用戶ID
     */
    @AfterReturning(pointcut = "activityApplicationPointcut() && args(activityId, userId)", returning = "result")
    public void handleActivityApplication(Object result, Integer activityId, Integer userId) {
        try {
            if (result instanceof ActivityParticipantResponse) {
                ActivityParticipantResponse response = (ActivityParticipantResponse) result;
                
                log.info("AOP攔截到活動報名申請，活動ID: {}, 申請用戶ID: {}, 狀態: {}", 
                        activityId, userId, response.getPStatusLabel());

                // 查詢真實的用戶和活動數據
                Users applicantUser = usersRepository.findById(userId).orElse(null);
                Activity activity = activityRepository.findById(activityId).orElse(null);
                
                if (applicantUser == null || activity == null) {
                    log.warn("無法找到用戶或活動數據: userId={}, activityId={}", userId, activityId);
                    return;
                }

                // 準備通知模板變數 - 使用真實數據
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("activity_id", activityId);
                templateVariables.put("activity_title", activity.getTitle());
                templateVariables.put("applicant_user_id", userId);
                templateVariables.put("applicant_name", applicantUser.getUsername());
                templateVariables.put("application_status", response.getPStatusLabel());
                templateVariables.put("activity_location", activity.getLocation());
                templateVariables.put("activity_start_time", activity.getActivStartTime().toString());

                // 獲取活動主辦者ID
                Integer organizerId = activity.getUser().getUserId();
                
                log.info("活動報名申請通知準備發送，活動ID: {}, 申請用戶: {}", activityId, userId);
                
                // 發送通知給活動主辦者
                // notificationService.sendTemplateNotification("ACTIVITY_APPLICATION_RECEIVED", organizerId, templateVariables);
            }
            
        } catch (Exception e) {
            log.error("處理活動報名申請通知時發生錯誤，活動ID: {}, 用戶ID: {}", activityId, userId, e);
        }
    }

    /**
     * 處理活動申請核准後的通知
     * 
     * 當主辦者核准申請後，向申請者發送核准通知
     * 
     * @param userId 申請用戶ID
     * @param activityId 活動ID
     */
    @AfterReturning("activityApprovalPointcut() && args(userId, activityId)")
    public void handleActivityApproval(Integer userId, Integer activityId) {
        try {
            log.info("AOP攔截到活動申請核准，用戶ID: {}, 活動ID: {}", userId, activityId);

            // 查詢真實的用戶和活動數據
            Users user = usersRepository.findById(userId).orElse(null);
            Activity activity = activityRepository.findById(activityId).orElse(null);
            
            if (user == null || activity == null) {
                log.warn("無法找到用戶或活動數據: userId={}, activityId={}", userId, activityId);
                return;
            }

            // 準備通知模板變數 - 使用真實數據
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("activity_id", activityId);
            templateVariables.put("user_name", user.getUsername());
            templateVariables.put("activity_title", activity.getTitle());
            templateVariables.put("activity_start_time", activity.getActivStartTime().toString());
            templateVariables.put("activity_location", activity.getLocation());
            templateVariables.put("organizer_name", activity.getUser().getUsername());

            // 發送申請核准通知給申請者
            notificationService.sendTemplateNotification(
                "ACTIVITY_APPLICATION_APPROVED",
                userId,
                templateVariables
            ).exceptionally(throwable -> {
                log.error("發送活動申請核准通知失敗，用戶ID: {}, 活動ID: {}", userId, activityId, throwable);
                return false;
            });

            log.info("活動申請核准通知已發送，用戶ID: {}, 活動ID: {}", userId, activityId);
            
        } catch (Exception e) {
            log.error("處理活動申請核准通知時發生錯誤，用戶ID: {}, 活動ID: {}", userId, activityId, e);
        }
    }

    /**
     * 處理活動申請拒絕後的通知
     * 
     * 當主辦者拒絕申請後，向申請者發送拒絕通知
     * 
     * @param userId 申請用戶ID
     * @param activityId 活動ID
     */
    @AfterReturning("activityRejectionPointcut() && args(userId, activityId)")
    public void handleActivityRejection(Integer userId, Integer activityId) {
        try {
            log.info("AOP攔截到活動申請拒絕，用戶ID: {}, 活動ID: {}", userId, activityId);

            // 查詢真實的用戶和活動數據
            Users user = usersRepository.findById(userId).orElse(null);
            Activity activity = activityRepository.findById(activityId).orElse(null);
            
            if (user == null || activity == null) {
                log.warn("無法找到用戶或活動數據: userId={}, activityId={}", userId, activityId);
                return;
            }

            // 準備通知模板變數 - 使用真實數據
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("activity_id", activityId);
            templateVariables.put("user_name", user.getUsername());
            templateVariables.put("activity_title", activity.getTitle());
            templateVariables.put("activity_location", activity.getLocation());
            templateVariables.put("organizer_name", activity.getUser().getUsername());
            templateVariables.put("rejection_reason", "不符合活動要求"); // 可以傳遞拒絕原因

            // 發送申請拒絕通知給申請者
            notificationService.sendTemplateNotification(
                "ACTIVITY_APPLICATION_REJECTED",
                userId,
                templateVariables
            ).exceptionally(throwable -> {
                log.error("發送活動申請拒絕通知失敗，用戶ID: {}, 活動ID: {}", userId, activityId, throwable);
                return false;
            });

            log.info("活動申請拒絕通知已發送，用戶ID: {}, 活動ID: {}", userId, activityId);
            
        } catch (Exception e) {
            log.error("處理活動申請拒絕通知時發生錯誤，用戶ID: {}, 活動ID: {}", userId, activityId, e);
        }
    }
} 