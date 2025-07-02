package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.MemberNotificationDto;
import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private NotificationDispatchService dispatchService;

    // This is a placeholder. Real implementation should be based on business logic.
    @Override
    @Transactional
    public void createNotificationForUser(Integer userId, String templateKey, Map<String, Object> params) {
        //  Left blank intentionally until business logic integration
    }

    // This is a placeholder. Real implementation should be based on business logic.
    @Override
    @Transactional
    public void createNotificationForUsers(List<Integer> userIds, String templateKey, Map<String, Object> params) {
        //  Left blank intentionally until business logic integration
    }

    @Override
    @Transactional
    public void sendManualNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification ID: " + notificationId));
        dispatchService.dispatchNotification(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsForUser(Integer userId, Pageable pageable) {
        // Corrected to match the actual entity field 'user' and 'createdTime'
        Page<MemberNotification> memberNotifications = memberNotificationRepository.findByUser_UserIdOrderByNotification_CreatedTimeDesc(userId, pageable);
        return memberNotifications.map(this::convertToDto);
    }

    private NotificationDto convertToDto(MemberNotification memberNotification) {
        Notification notification = memberNotification.getNotification();
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setCreatedTime(notification.getCreatedTime());
        dto.setIsRead(memberNotification.getIsRead());
        dto.setReadTime(memberNotification.getReadTime());
        return dto;
    }

    private MemberNotificationDto convertToMemberNotificationDto(MemberNotification memberNotification) {
        Notification notification = memberNotification.getNotification();
        MemberNotificationDto dto = new MemberNotificationDto();
        // 複合主鍵，無單獨ID
        dto.setNotificationId(notification.getNotificationId().longValue());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setIsRead(memberNotification.getIsRead());
        dto.setReadTime(memberNotification.getReadTime());
        dto.setCreatedTime(notification.getCreatedTime());
        return dto;
    }

    @Override
    @Transactional
    public void markAsRead(Integer userId, Integer notificationId) {
        // Corrected to match the actual entity field 'user'
        MemberNotification memberNotification = memberNotificationRepository.findByUser_UserIdAndNotification_NotificationId(userId, notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found for user " + userId));

        if (memberNotification.getIsRead() != null && !memberNotification.getIsRead()) {
            memberNotification.setIsRead(true);
            memberNotification.setReadTime(LocalDateTime.now());
            memberNotificationRepository.save(memberNotification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        // Corrected to match the actual entity field 'user'
        memberNotificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Integer userId) {
        // Corrected to match the actual entity field 'user'
        return memberNotificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberNotificationDto> getMemberNotifications(Integer userId, Pageable pageable) {
        Page<MemberNotification> memberNotifications = memberNotificationRepository.findByUser_UserIdOrderByNotification_CreatedTimeDesc(userId, pageable);
        return memberNotifications.map(this::convertToMemberNotificationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberNotificationDto> getUnreadMemberNotifications(Integer userId, Pageable pageable) {
        // 需要添加對應的Repository方法
        Page<MemberNotification> memberNotifications = memberNotificationRepository.findByUser_UserIdAndIsReadFalseOrderByNotification_CreatedTimeDesc(userId, pageable);
        return memberNotifications.map(this::convertToMemberNotificationDto);
    }
}
