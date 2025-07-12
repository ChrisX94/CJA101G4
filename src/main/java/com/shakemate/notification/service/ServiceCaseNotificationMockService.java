package com.shakemate.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服務案件模擬通知服務
 * 
 * 模擬服務案件模組的通知發送場景，用於測試和演示
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Service
@Slf4j
public class ServiceCaseNotificationMockService {

    @Autowired
    private NotificationDispatchService notificationDispatchService;

    /**
     * 模擬：服務案件建立通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     * @param caseTitle 案件標題
     */
    public void sendCaseCreatedNotification(Integer userId, String caseId, String caseTitle) {
        try {
            String title = "服務案件建立成功";
            String content = String.format("您的服務案件「%s」(案件編號：%s) 已成功建立，我們會儘快為您處理。", 
                caseTitle, caseId);
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ 服務案件建立通知已發送 - 用戶ID: {}, 案件ID: {}", userId, caseId);
            
        } catch (Exception e) {
            log.error("❌ 發送服務案件建立通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /**
     * 模擬：案件處理進度通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     * @param status 處理狀態
     * @param note 進度說明
     */
    public void sendCaseProgressNotification(Integer userId, String caseId, String status, String note) {
        try {
            String title = "案件處理進度更新";
            String content = String.format("您的案件 %s 狀態已更新為：%s。%s", 
                caseId, status, note != null ? note : "");
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ 案件進度通知已發送 - 用戶ID: {}, 案件ID: {}, 狀態: {}", userId, caseId, status);
            
        } catch (Exception e) {
            log.error("❌ 發送案件進度通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /**
     * 模擬：案件處理完成通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     * @param solution 解決方案
     */
    public void sendCaseCompletedNotification(Integer userId, String caseId, String solution) {
        try {
            String title = "服務案件處理完成";
            String content = String.format("您的案件 %s 已處理完成。解決方案：%s。感謝您的耐心等待！", 
                caseId, solution);
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ 案件完成通知已發送 - 用戶ID: {}, 案件ID: {}", userId, caseId);
            
        } catch (Exception e) {
            log.error("❌ 發送案件完成通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /**
     * 模擬：AI回覆就緒通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     */
    public void sendAiResponseReadyNotification(Integer userId, String caseId) {
        try {
            String title = "AI智能回覆已準備就緒";
            String content = String.format("針對您的案件 %s，AI已分析完成並提供了初步建議，請查看。", caseId);
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ AI回覆就緒通知已發送 - 用戶ID: {}, 案件ID: {}", userId, caseId);
            
        } catch (Exception e) {
            log.error("❌ 發送AI回覆就緒通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /**
     * 模擬：客服回覆通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     * @param staffName 客服人員姓名
     */
    public void sendStaffReplyNotification(Integer userId, String caseId, String staffName) {
        try {
            String title = "客服人員已回覆";
            String content = String.format("客服人員 %s 已針對您的案件 %s 提供了回覆，請及時查看。", 
                staffName, caseId);
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ 客服回覆通知已發送 - 用戶ID: {}, 案件ID: {}, 客服: {}", userId, caseId, staffName);
            
        } catch (Exception e) {
            log.error("❌ 發送客服回覆通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /**
     * 模擬：案件優先級通知
     * 
     * @param userId 用戶ID
     * @param caseId 案件ID
     * @param oldPriority 原優先級
     * @param newPriority 新優先級
     */
    public void sendCasePriorityChangeNotification(Integer userId, String caseId, 
                                                  String oldPriority, String newPriority) {
        try {
            String title = "案件優先級調整";
            String content = String.format("您的案件 %s 優先級已從 %s 調整為 %s。", 
                caseId, oldPriority, newPriority);
            
            notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
            log.info("✅ 案件優先級變更通知已發送 - 用戶ID: {}, 案件ID: {}, {} -> {}", 
                userId, caseId, oldPriority, newPriority);
            
        } catch (Exception e) {
            log.error("❌ 發送案件優先級變更通知失敗 - 用戶ID: {}, 案件ID: {}, 錯誤: {}", 
                userId, caseId, e.getMessage());
        }
    }

    /** 服務案例狀態更新通知 */
    public void notifyServiceCaseStatusUpdate(Integer userId, String caseId, String oldStatus, String newStatus) {
        String title = "服務案例狀態更新";
        String content = String.format("您的服務案例 %s 狀態已從「%s」更新為「%s」", caseId, oldStatus, newStatus);
        
        log.info("[模擬] 發送服務案例狀態更新通知: 用戶ID={}, 案例ID={}, 狀態變更: {} -> {}", 
                userId, caseId, oldStatus, newStatus);
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /** 服務案例分配通知 */
    public void notifyServiceCaseAssignment(Integer userId, String caseId, String assignedTo) {
        String title = "服務案例分配通知";
        String content = String.format("您的服務案例 %s 已分配給 %s 處理", caseId, assignedTo);
        
        log.info("[模擬] 發送服務案例分配通知: 用戶ID={}, 案例ID={}, 分配給: {}", 
                userId, caseId, assignedTo);
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /** 服務案例回覆通知 */
    public void notifyServiceCaseReply(Integer userId, String caseId, String replyContent) {
        String title = "服務案例回覆通知";
        String content = String.format("您的服務案例 %s 有新的回覆: %s", caseId, replyContent);
        
        log.info("[模擬] 發送服務案例回覆通知: 用戶ID={}, 案例ID={}, 回覆內容長度: {}", 
                userId, caseId, replyContent.length());
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /** 服務案例完成通知 */
    public void notifyServiceCaseCompletion(Integer userId, String caseId, String resolution) {
        String title = "服務案例完成通知";
        String content = String.format("您的服務案例 %s 已完成處理。處理結果: %s", caseId, resolution);
        
        log.info("[模擬] 發送服務案例完成通知: 用戶ID={}, 案例ID={}, 處理結果: {}", 
                userId, caseId, resolution);
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /** 服務案例評價提醒 */
    public void notifyServiceCaseRatingReminder(Integer userId, String caseId) {
        String title = "服務案例評價提醒";
        String content = String.format("您的服務案例 %s 已完成，請為本次服務進行評價", caseId);
        
        log.info("[模擬] 發送服務案例評價提醒: 用戶ID={}, 案例ID={}", userId, caseId);
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /** 服務案例逾期提醒 */
    public void notifyServiceCaseOverdue(Integer userId, String caseId, int overdueDays) {
        String title = "服務案例逾期提醒";
        String content = String.format("您的服務案例 %s 已逾期 %d 天，請及時處理", caseId, overdueDays);
        
        log.info("[模擬] 發送服務案例逾期提醒: 用戶ID={}, 案例ID={}, 逾期天數: {}", 
                userId, caseId, overdueDays);
        
        notificationDispatchService.sendNotificationWithCategory(userId, title, content, "IN_APP", "系統通知");
    }

    /**
     * 模擬監聽案件狀態變更的方法
     * 實際場景中會監聽數據庫變更或事件
     */
    public void simulateCaseStatusMonitoring() {
        log.info("🔄 開始模擬案件狀態監控...");
        
        // 模擬案件狀態變更事件
        sendCaseProgressNotification(1001, "CASE-20240101-001", "處理中", "技術人員已接手處理");
        sendStaffReplyNotification(1002, "CASE-20240101-002", "客服小王");
        sendCaseCompletedNotification(1003, "CASE-20240101-003", "已重設密碼並發送至您的郵箱");
        
        log.info("✅ 案件狀態監控模擬完成");
    }

    /**
     * 取得用戶案件列表（模擬方法）
     * 
     * @param userId 用戶ID
     * @return 模擬的案件數量
     */
    private int getUserCaseCount(Integer userId) {
        // 模擬根據用戶ID獲取案件數量
        return Math.abs(userId.hashCode() % 10) + 1;
    }

    /**
     * 模擬案件緊急程度評估
     * 
     * @param caseTitle 案件標題
     * @return 是否為緊急案件
     */
    private boolean isUrgentCase(String caseTitle) {
        String[] urgentKeywords = {"緊急", "無法登入", "系統錯誤", "資料遺失", "付款失敗"};
        for (String keyword : urgentKeywords) {
            if (caseTitle.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模擬案件自動分派邏輯
     * 
     * @param caseType 案件類型
     * @return 分派的部門
     */
    private String assignCaseToDepartment(String caseType) {
        switch (caseType) {
            case "技術問題": return "技術支援部";
            case "帳務問題": return "財務部";
            case "商品問題": return "商品部";
            case "其他": return "客服部";
            default: return "客服部";
        }
    }

    /**
     * 格式化時間顯示
     * 
     * @param dateTime 時間
     * @return 格式化的時間字符串
     */
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
} 