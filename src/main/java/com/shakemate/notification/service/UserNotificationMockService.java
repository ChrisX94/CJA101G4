package com.shakemate.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用戶管理模擬通知服務
 * 模擬用戶管理模塊的各種通知場景，僅用於集成測試與示例，不影響真實業務邏輯
 * @author ShakeMate團隊
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationMockService {

    private final NotificationDispatchService dispatchService;

    /** 註冊歡迎通知 */
    public void notifyRegistrationWelcome(Integer userId, String userName) {
        dispatchService.sendNotification(userId, "註冊歡迎", "親愛的「" + userName + "」，歡迎加入ShakeMate！", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 註冊歡迎", userId);
    }

    /** 郵箱驗證通知 */
    public void notifyEmailVerification(Integer userId, String email) {
        dispatchService.sendNotification(userId, "郵箱驗證", "請驗證您的郵箱地址：「" + email + "」。", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 郵箱驗證 - {}", userId, email);
    }

    /** 密碼變更通知 */
    public void notifyPasswordChanged(Integer userId) {
        dispatchService.sendNotification(userId, "密碼變更通知", "您的帳號密碼已成功變更，如非本人操作請及時聯繫客服。", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 密碼變更", userId);
    }

    /** 異常登入警告通知 */
    public void notifyLoginAnomaly(Integer userId, String location) {
        dispatchService.sendNotification(userId, "異常登入警告", "偵測到您的帳號於「" + location + "」有異常登入，如非本人操作請立即修改密碼。", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 異常登入 - {}", userId, location);
    }

    /** 帳號安全提醒 */
    public void notifyAccountSecurity(Integer userId, String detail) {
        dispatchService.sendNotification(userId, "帳號安全提醒", detail, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 帳號安全提醒 - {}", userId, detail);
    }

    /** 登入行為監控（模擬） */
    public void monitorLoginBehavior(Integer userId, Map<String, Object> loginInfo) {
        log.info("[模擬] 監控用戶{}登入行為: {}", userId, loginInfo);
    }

    /** 用戶狀態變更通知 */
    public void notifyUserStatusChanged(Integer userId, String status) {
        dispatchService.sendNotification(userId, "用戶狀態變更", "您的帳號狀態已變更為「" + status + "」。", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 狀態變更 - {}", userId, status);
    }
} 