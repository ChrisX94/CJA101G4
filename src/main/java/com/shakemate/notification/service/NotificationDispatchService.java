package com.shakemate.notification.service;

import com.shakemate.notification.entity.Notification;
import org.springframework.scheduling.annotation.Async;

public interface NotificationDispatchService {

    /**
     * 非同步派送一個通知給其所有目標使用者。
     * @param notification 要派送的通知實體。
     */
    @Async
    void dispatchNotification(Notification notification);

    /**
     * 發送通知給單一用戶 (業務模組對接專用)
     * @param userId 目標用戶ID
     * @param title 通知標題
     * @param content 通知內容
     * @param deliveryMethod 發送方式 (EMAIL, PUSH, SMS, IN_APP)
     */
    @Async
    void sendNotification(Integer userId, String title, String content, String deliveryMethod);

    /**
     * 發送通知給單一用戶（帶通知類別）
     * @param userId 用戶ID
     * @param title 通知標題
     * @param content 通知內容
     * @param deliveryMethod 發送方式
     * @param category 通知類別
     */
    @Async
    void sendNotificationWithCategory(Integer userId, String title, String content, String deliveryMethod, String category);

} 