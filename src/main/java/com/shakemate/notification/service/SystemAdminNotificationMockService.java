package com.shakemate.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系統管理模擬通知服務
 * 模擬系統管理模塊的各種通知場景，僅用於集成測試與示例，不影響真實業務邏輯
 * @author ShakeMate團隊
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAdminNotificationMockService {

    private final NotificationDispatchService dispatchService;

    /** 系統維護公告通知 */
    public void notifySystemMaintenance(Integer userId, String detail) {
        dispatchService.sendNotification(userId, "系統維護公告", detail, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 系統維護公告 - {}", userId, detail);
    }

    /** 系統更新通知 */
    public void notifySystemUpdate(Integer userId, String updateInfo) {
        dispatchService.sendNotification(userId, "系統更新通知", updateInfo, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 系統更新 - {}", userId, updateInfo);
    }

    /** 條款政策更新通知 */
    public void notifyPolicyUpdate(Integer userId, String policyInfo) {
        dispatchService.sendNotification(userId, "條款政策更新", policyInfo, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 條款政策更新 - {}", userId, policyInfo);
    }

    /** 管理員廣播功能（模擬） */
    public void broadcastAdminMessage(String message, Integer... userIds) {
        for (Integer userId : userIds) {
            dispatchService.sendNotification(userId, "管理員廣播", message, "IN_APP");
            log.info("[模擬] 管理員廣播給用戶{}: {}", userId, message);
        }
    }

    /** 緊急通知機制 */
    public void notifyEmergency(Integer userId, String emergencyInfo) {
        dispatchService.sendNotification(userId, "緊急通知", emergencyInfo, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 緊急通知 - {}", userId, emergencyInfo);
    }
} 