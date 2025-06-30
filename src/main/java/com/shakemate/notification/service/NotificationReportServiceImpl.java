package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationStatsDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.enums.DeliveryStatus;
import com.shakemate.notification.exception.ResourceNotFoundException;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationReportServiceImpl implements NotificationReportService {

    // Assuming these values represent the status in the database
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILURE = 2;
    private static final int INTERACTION_CLICKED = 1;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationStatsDto getNotificationStats(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + notificationId + " 的通知"));

        long totalSent = memberNotificationRepository.countByNotification_NotificationId(notificationId);
        if (totalSent == 0) {
            return new NotificationStatsDto(notificationId, notification.getTitle(), 0, 0, 0, 0, 0, 0, 0);
        }

        long successCount = memberNotificationRepository.countByNotification_NotificationIdAndDeliveryStatus(notificationId, DeliveryStatus.SUCCESS);
        long failureCount = memberNotificationRepository.countByNotification_NotificationIdAndDeliveryStatus(notificationId, DeliveryStatus.FAILED);
        long readCount = memberNotificationRepository.countByNotification_NotificationIdAndIsReadTrue(notificationId);
        long clickCount = memberNotificationRepository.countByNotification_NotificationIdAndUserInteraction(notificationId, INTERACTION_CLICKED);

        double readRate = (double) readCount / totalSent;
        // Click rate is usually calculated based on successful deliveries
        double clickRate = (successCount > 0) ? (double) clickCount / successCount : 0.0;

        return new NotificationStatsDto(
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
    }
} 