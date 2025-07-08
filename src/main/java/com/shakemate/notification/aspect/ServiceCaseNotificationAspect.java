package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.servicecase.dto.ServiceCaseDTO;
import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseRepository;
import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
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
 * 服務案件通知AOP切面
 * 
 * 透過AOP機制攔截服務案件相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 案件創建（CASE_CREATED）
 * - 案件狀態更新（CASE_STATUS_UPDATED）
 * - 案件分派（CASE_ASSIGNED）
 * - 案件完成（CASE_COMPLETED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceCaseNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;
    private final ServiceCaseRepository serviceCaseRepository;
    private final CaseTypeRepository caseTypeRepository;

    /**
     * 定義切點：攔截案件創建操作
     */
    @Pointcut("execution(* com.shakemate.servicecase.service.ServiceCaseService.create(..))")
    public void caseCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截案件更新操作
     */
    @Pointcut("execution(* com.shakemate.servicecase.service.ServiceCaseService.update(..))")
    public void caseUpdatePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截案件刪除操作
     */
    @Pointcut("execution(* com.shakemate.servicecase.service.ServiceCaseService.delete(..))")
    public void caseDeletionPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理案件創建後的通知
     * 
     * 當用戶創建服務案件後，發送創建確認通知給用戶，並通知相關管理員
     * 
     * @param result 創建的案件
     * @param serviceCase 案件表單
     */
    @AfterReturning(pointcut = "caseCreationPointcut() && args(serviceCase)", returning = "result")
    public void handleCaseCreated(Object result, ServiceCase serviceCase) {
        try {
            if (result instanceof ServiceCase) {
                ServiceCase createdCase = (ServiceCase) result;
                
                log.info("AOP攔截到案件創建，案件ID: {}, 用戶ID: {}, 標題: {}", 
                        createdCase.getCaseId(), createdCase.getUserId(), createdCase.getTitle());

                // 查詢真實的用戶和案件類型數據
                Users user = null;
                if (createdCase.getUserId() != null) {
                    user = usersRepository.findById(createdCase.getUserId()).orElse(null);
                }
                
                CaseType caseType = null;
                if (createdCase.getCaseTypeId() != null) {
                    caseType = caseTypeRepository.findById(createdCase.getCaseTypeId()).orElse(null);
                }

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("case_id", createdCase.getCaseId());
                templateVariables.put("case_title", createdCase.getTitle());
                templateVariables.put("case_content", truncateContent(createdCase.getContent(), 100));
                templateVariables.put("case_type", caseType != null ? caseType.getTypeName() : "一般諮詢");
                templateVariables.put("case_status", getStatusDescription(createdCase.getCaseStatus()));
                templateVariables.put("user_name", user != null ? user.getUsername() : "訪客");
                templateVariables.put("user_email", createdCase.getEmail());
                templateVariables.put("created_time", createdCase.getCreateTime().toString());

                // 發送案件創建確認通知給用戶（如果有用戶ID）
                if (createdCase.getUserId() != null) {
                    notificationService.sendTemplateNotification(
                        "CASE_CREATED",
                        createdCase.getUserId(),
                        templateVariables
                    ).exceptionally(throwable -> {
                        log.error("發送案件創建通知失敗，案件ID: {}, 用戶ID: {}", 
                                createdCase.getCaseId(), createdCase.getUserId(), throwable);
                        return false;
                    });

                    log.info("案件創建確認通知已發送給用戶，案件ID: {}", createdCase.getCaseId());
                }

                // 發送新案件通知給管理員
                if (createdCase.getAdmId() != null) {
                    templateVariables.put("admin_notification", true);
                    
                    notificationService.sendTemplateNotification(
                        "CASE_NEW_ADMIN",
                        createdCase.getAdmId(),
                        templateVariables
                    ).exceptionally(throwable -> {
                        log.error("發送新案件管理員通知失敗，案件ID: {}, 管理員ID: {}", 
                                createdCase.getCaseId(), createdCase.getAdmId(), throwable);
                        return false;
                    });

                    log.info("新案件管理員通知已發送，案件ID: {}", createdCase.getCaseId());
                }
            }
            
        } catch (Exception e) {
            log.error("處理案件創建通知時發生錯誤", e);
        }
    }

    /**
     * 處理案件更新後的通知
     * 
     * 當管理員更新案件狀態時，發送狀態更新通知給用戶
     * 
     * @param result 更新的案件
     * @param serviceCase 案件表單
     */
    @AfterReturning(pointcut = "caseUpdatePointcut() && args(serviceCase)", returning = "result")
    public void handleCaseUpdated(Object result, ServiceCase serviceCase) {
        try {
            if (result instanceof ServiceCase) {
                ServiceCase updatedCase = (ServiceCase) result;
                
                log.info("AOP攔截到案件更新，案件ID: {}, 新狀態: {}", 
                        updatedCase.getCaseId(), updatedCase.getCaseStatus());

                // 查詢舊案件數據以比較狀態變更
                ServiceCase oldCase = serviceCaseRepository.findById(updatedCase.getCaseId()).orElse(null);
                if (oldCase == null) {
                    log.warn("無法找到舊案件數據: caseId={}", updatedCase.getCaseId());
                    return;
                }

                // 檢查狀態是否有變更
                if (oldCase.getCaseStatus() != null && 
                    oldCase.getCaseStatus().equals(updatedCase.getCaseStatus())) {
                    log.info("案件狀態無變更，不發送通知");
                    return;
                }

                // 查詢用戶和案件類型數據
                Users user = null;
                if (updatedCase.getUserId() != null) {
                    user = usersRepository.findById(updatedCase.getUserId()).orElse(null);
                }
                
                CaseType caseType = null;
                if (updatedCase.getCaseTypeId() != null) {
                    caseType = caseTypeRepository.findById(updatedCase.getCaseTypeId()).orElse(null);
                }

                // 根據新狀態決定通知模板
                String templateCode = getTemplateCodeForStatus(updatedCase.getCaseStatus());
                
                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("case_id", updatedCase.getCaseId());
                templateVariables.put("case_title", updatedCase.getTitle());
                templateVariables.put("case_type", caseType != null ? caseType.getTypeName() : "一般諮詢");
                templateVariables.put("old_status", getStatusDescription(oldCase.getCaseStatus()));
                templateVariables.put("new_status", getStatusDescription(updatedCase.getCaseStatus()));
                templateVariables.put("user_name", user != null ? user.getUsername() : "訪客");
                templateVariables.put("updated_time", updatedCase.getUpdateTime().toString());

                // 發送狀態更新通知給用戶（如果有用戶ID）
                if (updatedCase.getUserId() != null) {
                    notificationService.sendTemplateNotification(
                        templateCode,
                        updatedCase.getUserId(),
                        templateVariables
                    ).exceptionally(throwable -> {
                        log.error("發送案件狀態更新通知失敗，案件ID: {}, 用戶ID: {}", 
                                updatedCase.getCaseId(), updatedCase.getUserId(), throwable);
                        return false;
                    });

                    log.info("案件狀態更新通知已發送，案件ID: {}, 新狀態: {}", 
                            updatedCase.getCaseId(), updatedCase.getCaseStatus());
                }
            }
            
        } catch (Exception e) {
            log.error("處理案件更新通知時發生錯誤", e);
        }
    }

    /**
     * 處理案件刪除後的通知
     * 
     * 當管理員刪除案件時，記錄操作日誌
     * 
     * @param caseId 案件ID
     */
    @AfterReturning("caseDeletionPointcut() && args(caseId)")
    public void handleCaseDeleted(Integer caseId) {
        try {
            log.info("AOP攔截到案件刪除，案件ID: {}", caseId);

            // 案件刪除通常不需要通知用戶
            // 這裡主要用於記錄管理操作日誌
            // 可以選擇性地通知管理員或相關人員
            
            log.info("案件刪除操作已記錄，案件ID: {}", caseId);
            
        } catch (Exception e) {
            log.error("處理案件刪除通知時發生錯誤，案件ID: {}", caseId, e);
        }
    }

    /**
     * 根據案件狀態獲取對應的通知模板代碼
     * 
     * @param status 案件狀態
     * @return 通知模板代碼
     */
    private String getTemplateCodeForStatus(Integer status) {
        if (status == null) {
            return "CASE_STATUS_UPDATED";
        }
        
        return switch (status) {
            case 0 -> "CASE_STATUS_PENDING";
            case 1 -> "CASE_STATUS_PROCESSING";
            case 2 -> "CASE_STATUS_COMPLETED";
            default -> "CASE_STATUS_UPDATED";
        };
    }

    /**
     * 獲取案件狀態描述
     * 
     * @param status 案件狀態
     * @return 狀態描述
     */
    private String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知狀態";
        }
        
        return switch (status) {
            case 0 -> "未處理";
            case 1 -> "處理中";
            case 2 -> "已完成";
            default -> "未知狀態";
        };
    }

    /**
     * 截斷內容的輔助方法
     * 
     * @param content 原始內容
     * @param maxLength 最大長度
     * @return 截斷後的內容
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
} 