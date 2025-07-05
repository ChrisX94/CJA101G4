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

} 