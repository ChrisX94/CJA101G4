package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.MemberNotificationDto;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /**
     * 發送模板通知給單一用戶
     * @param templateCode 模板代碼
     * @param userId 用戶ID
     * @param variables 模板變數
     * @return 發送結果
     */
    CompletableFuture<Boolean> sendTemplateNotification(String templateCode, Integer userId, Map<String, Object> variables);

    /**
     * 發送模板通知給多個用戶
     * @param templateCode 模板代碼
     * @param userIds 用戶ID列表
     * @param variables 模板變數
     * @return 發送結果
     */
    CompletableFuture<Boolean> sendTemplateNotificationToUsers(String templateCode, List<Integer> userIds, Map<String, Object> variables);

    /**
     * 發送即時通知
     * @param userId 用戶ID
     * @param title 通知標題
     * @param content 通知內容
     * @param notificationType 通知類型
     * @param deliveryMethods 發送方式列表
     * @return 發送結果
     */
    CompletableFuture<Boolean> sendInstantNotification(Integer userId, String title, String content, String notificationType, List<String> deliveryMethods);

    /**
     * 廣播模板通知
     * @param templateCode 模板代碼
     * @param variables 模板變數
     * @param targetCriteria 目標篩選條件
     * @return 發送結果
     */
    CompletableFuture<Boolean> broadcastTemplateNotification(String templateCode, Map<String, Object> variables, Map<String, Object> targetCriteria);

    /**
     * 條件發送通知
     * @param templateCode 模板代碼
     * @param variables 模板變數
     * @param targetCriteria 目標篩選條件
     * @return 發送結果
     */
    CompletableFuture<Boolean> sendConditionalNotification(String templateCode, Map<String, Object> variables, Map<String, Object> targetCriteria);

    /**
     * 為單一使用者生成通知 (通常由事件觸發，如新配對、新訊息)
     * @param userId 使用者ID
     * @param templateKey 通知的模板Key，如 "NEW_MATCH"
     * @param params 模板中需要的動態參數
     */
    void createNotificationForUser(Integer userId, String templateKey, Map<String, Object> params);

    /**
     * 為多個使用者生成通知 (通常用於批次任務，如活動提醒)
     * @param userIds 使用者ID列表
     * @param templateKey 通知的模板Key
     * @param params 模板中需要的動態參數
     */
    void createNotificationForUsers(List<Integer> userIds, String templateKey, Map<String, Object> params);

    /**
     * 手動發送通知 (通常由後台管理員操作)
     * @param notificationId 已在資料庫中建立的 Notification 實體 ID
     */
    void sendManualNotification(Integer notificationId);

    /**
     * @return 分頁後的通知 DTO
     */
    Page<NotificationDto> getNotificationsForUser(Integer userId, Pageable pageable);

    /**
     * 將指定的通知標記為已讀
     * @param userId 當前使用者ID，用於驗證權限
     * @param notificationId 要標記為已讀的通知ID
     */
    void markAsRead(Integer userId, Integer notificationId);

    /**
     * 將指定使用者的所有通知標記為已讀
     * @param userId 當前使用者ID
     */
    void markAllAsRead(Integer userId);

    /**
     * 獲取指定使用者的未讀通知數量
     * @param userId 當前使用者ID
     * @return 未讀通知的數量
     */
    long getUnreadCount(Integer userId);

    /**
     * 獲取會員通知列表 (包含詳細資訊)
     * @param userId 會員ID
     * @param pageable 分頁參數
     * @return 會員通知DTO分頁結果
     */
    Page<MemberNotificationDto> getMemberNotifications(Integer userId, Pageable pageable);

    /**
     * 獲取會員未讀通知列表
     * @param userId 會員ID
     * @param pageable 分頁參數
     * @return 未讀會員通知DTO分頁結果
     */
    Page<MemberNotificationDto> getUnreadMemberNotifications(Integer userId, Pageable pageable);

} 