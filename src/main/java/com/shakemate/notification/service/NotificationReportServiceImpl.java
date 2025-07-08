package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationStatsDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.enums.DeliveryStatus;
import com.shakemate.notification.enums.NotificationStatus;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.notification.exception.NotificationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationReportServiceImpl implements NotificationReportService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationStatsDto getNotificationStats(Integer notificationId) {
        log.info("生成通知報告，ID: {}", notificationId);
        
        // 取得通知主表資訊
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("通知不存在，ID: " + notificationId));

        // 僅統計已發布的通知
        NotificationStatus status = NotificationStatus.fromCode(notification.getStatus());
        if (status != NotificationStatus.PUBLISHED) {
            throw new IllegalStateException("僅能查詢已發布的通知報表");
        }

        // 總發送數
        long totalSent = memberNotificationRepository.countByNotification(notificationId);
        
        // 成功數
        long successCount = memberNotificationRepository.countByNotificationAndDeliveryStatus(
            notificationId, DeliveryStatus.SUCCESS);
        
        // 失敗數
        long failureCount = memberNotificationRepository.countByNotificationAndDeliveryStatus(
            notificationId, DeliveryStatus.FAILED);
        
        // 已讀數
        long readCount = memberNotificationRepository.countByNotificationAndIsReadTrue(notificationId);
        
        // 點擊數
        long clickCount = memberNotificationRepository.countByNotificationAndUserInteraction(notificationId, 1);

        // 計算比率
        double readRate = totalSent > 0 ? (double) readCount / totalSent : 0.0;
        double clickRate = totalSent > 0 ? (double) clickCount / totalSent : 0.0;

        // 生成報告
        NotificationStatsDto statsDto = new NotificationStatsDto(
            notificationId,
            notification.getTitle(),
            totalSent,
            successCount,
            failureCount,
            readCount,
            readRate,
            clickCount,
            clickRate
        );

        log.info("報告生成完成，統計結果：{}", statsDto);
        return statsDto;
    }
} 