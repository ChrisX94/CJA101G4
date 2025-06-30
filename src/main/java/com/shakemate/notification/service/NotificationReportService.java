package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationStatsDto;

public interface NotificationReportService {

    /**
     * 獲取指定通知的成效統計報告
     * @param notificationId 通知的ID
     * @return 包含各項統計數據的 DTO
     */
    NotificationStatsDto getNotificationStats(Integer notificationId);
} 