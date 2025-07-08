package com.shakemate.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.user.dao.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.entity.NotificationPreference;
import com.shakemate.notification.enums.DeliveryStatus;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationPreferenceRepository;
import com.shakemate.notification.service.EmailService;
import com.shakemate.notification.ws.NotificationWebSocketHandler;
import com.shakemate.user.model.Users;
import com.shakemate.notification.enums.NotificationStatus;

@Slf4j
@Service
public class NotificationDispatchServiceImpl implements NotificationDispatchService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhoneStorageService phoneStorageService;

    @Async("asyncTaskExecutor")
    @Override
    @Transactional
    public void dispatchNotification(Notification notification) {
        log.info("開始派送通知 ID: {} (多渠道完整版)", notification.getNotificationId());
        try {
            // 1. 解析目標用戶
            List<Integer> targetUserIds = resolveTargetUserIds(notification);
            if (targetUserIds.isEmpty()) {
                log.warn("通知 ID: {} 無目標用戶，發送中止", notification.getNotificationId());
                notification.setStatus(DeliveryStatus.FAILED.getCode());
                notificationRepository.save(notification);
                return;
            }

            // 2. 查詢所有目標用戶的通知偏好
            List<NotificationPreference> allPreferences = preferenceRepository.findByUser_UserIdIn(targetUserIds);
            Map<Integer, List<NotificationPreference>> userPrefMap = allPreferences.stream()
                    .collect(Collectors.groupingBy(p -> p.getUser().getUserId()));

            // 3. 根據偏好與勿擾時段，決定每位用戶的發送管道
            List<MemberNotification> memberNotifications = targetUserIds.stream()
                    .flatMap(userId -> {
                        List<MemberNotification> list = new java.util.ArrayList<>();
                        List<NotificationPreference> prefs = userPrefMap.getOrDefault(userId, Collections.emptyList());
                        boolean inDND = isInDoNotDisturb(prefs);
                        if (inDND) {
                            log.info("用戶 {} 處於勿擾時段，跳過發送", userId);
                            return list.stream();
                        }
                        // Email
                        if (prefs.stream().anyMatch(NotificationPreference::getEmailEnabled)) {
                            list.add(buildMemberNotification(notification, userId, "EMAIL"));
                        }
                        // 推播
                        if (prefs.stream().anyMatch(NotificationPreference::getPushEnabled)) {
                            list.add(buildMemberNotification(notification, userId, "PUSH"));
                        }
                        // 站內
                        if (prefs.stream().anyMatch(NotificationPreference::getInAppEnabled)) {
                            list.add(buildMemberNotification(notification, userId, "IN_APP"));
                        }
                        return list.stream();
                    })
                    .collect(Collectors.toList());

            if (memberNotifications.isEmpty()) {
                log.warn("通知 ID: {} 無可發送用戶（全部因勿擾或偏好關閉）", notification.getNotificationId());
                notification.setStatus(DeliveryStatus.FAILED.getCode());
                notificationRepository.save(notification);
                return;
            }

            // 4. 批量保存 MemberNotification
            memberNotificationRepository.saveAll(memberNotifications);

            // 5. 分渠道發送
            for (MemberNotification mn : memberNotifications) {
                try {
                    if ("EMAIL".equals(mn.getDeliveryMethod())) {
                        log.info("[模擬] 發送Email給用戶ID: {}，標題: {}，內容: {}", mn.getUser(), notification.getTitle(), notification.getMessage());
                        mn.setDeliveryStatus(DeliveryStatus.SUCCESS);
                    } else if ("PUSH".equals(mn.getDeliveryMethod())) {
                        log.info("[模擬] 發送推播給用戶ID: {}，標題: {}，內容: {}", mn.getUser(), notification.getTitle(), notification.getMessage());
                        mn.setDeliveryStatus(DeliveryStatus.SUCCESS);
                    } else if ("SMS".equals(mn.getDeliveryMethod())) {
                        // 集成PhoneStorageService獲取手機號碼發送SMS
                        Optional<String> phoneNumber = phoneStorageService.getPhoneByUserId(mn.getUser());
                        if (phoneNumber.isPresent()) {
                            log.info("[模擬] 發送SMS給用戶ID: {}，手機號: {}，標題: {}，內容: {}", 
                                mn.getUser(), phoneStorageService.validatePhoneFormat(phoneNumber.get()) ? "09****" + phoneNumber.get().substring(phoneNumber.get().length()-3) : "格式錯誤", 
                                notification.getTitle(), notification.getMessage());
                        mn.setDeliveryStatus(DeliveryStatus.SUCCESS);
                        } else {
                            log.warn("[SMS] 用戶 {} 沒有綁定手機號碼，跳過SMS發送", mn.getUser());
                            mn.setDeliveryStatus(DeliveryStatus.FAILED);
                            mn.setErrorMessage("用戶沒有綁定手機號碼");
                        }
                    } else if ("IN_APP".equals(mn.getDeliveryMethod())) {
                        NotificationWebSocketHandler.sendMessageToUser(mn.getUser(), notification.getMessage());
                        mn.setDeliveryStatus(DeliveryStatus.SUCCESS);
                    }
                } catch (Exception e) {
                    mn.setDeliveryStatus(DeliveryStatus.FAILED);
                    mn.setErrorMessage(e.getMessage());
                    log.error("通知發送失敗: 用戶 {}，管道 {}，錯誤: {}", mn.getUser(), mn.getDeliveryMethod(), e.getMessage());
                }
            }
            memberNotificationRepository.saveAll(memberNotifications);

            // 6. 更新通知狀態
            notification.setStatus(NotificationStatus.PUBLISHED.getCode());
            notificationRepository.save(notification);
            log.info("通知 ID: {} 已完成多渠道發送", notification.getNotificationId());
        } catch (Exception e) {
            log.error("派送通知 ID: {} 時發生錯誤", notification.getNotificationId(), e);
            notification.setStatus(DeliveryStatus.FAILED.getCode());
            notificationRepository.save(notification);
        }
    }

    // 解析目標用戶ID
    private List<Integer> resolveTargetUserIds(Notification notification) {
        if ("ALL".equals(notification.getTargetType())) {
            return usersRepository.findAll().stream().map(Users::getUserId).collect(Collectors.toList());
        } else if ("SPECIFIC".equals(notification.getTargetType())) {
            Map<String, Object> criteria = notification.getTargetCriteria();
            Object ids = criteria != null ? criteria.get("userIds") : null;
            if (ids instanceof List<?>) {
                return ((List<?>) ids).stream().filter(i -> i instanceof Integer).map(i -> (Integer) i).collect(Collectors.toList());
            }
        } else if ("TAG".equals(notification.getTargetType())) {
            // TODO: 根據標籤條件查詢
            // 這裡假設有 userSpecificationBuilder 可用
            // return usersRepository.findAll(userSpecificationBuilder.buildSpecification(notification.getTargetCriteria())).stream().map(Users::getUserId).collect(Collectors.toList());
            return Collections.emptyList(); // 需根據實際標籤查詢實現
        }
        return Collections.emptyList();
    }

    // 判斷是否在勿擾時段
    private boolean isInDoNotDisturb(List<NotificationPreference> prefs) {
        LocalTime now = LocalTime.now();
        for (NotificationPreference pref : prefs) {
            if (Boolean.TRUE.equals(pref.getQuietHoursEnabled()) && pref.getQuietHoursStart() != null && pref.getQuietHoursEnd() != null) {
                if (isWithinTimeRange(now, pref.getQuietHoursStart(), pref.getQuietHoursEnd())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWithinTimeRange(LocalTime now, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        } else {
            // 跨午夜
            return !now.isBefore(start) || now.isBefore(end);
        }
    }

    // 構建 MemberNotification
    private MemberNotification buildMemberNotification(Notification notification, Integer userId, String method) {
        MemberNotification mn = new MemberNotification();
        mn.setNotification(notification.getNotificationId());
        mn.setUser(userId);
        mn.setDeliveryMethod(method);
        mn.setDeliveryStatus(DeliveryStatus.PENDING);
        mn.setIsRead(false);
        mn.setRetryCount(0);
        mn.setUserInteraction(0);
        return mn;
    }

    /**
     * 發送通知給單一用戶 (業務模組對接專用)
     * 實施分級發送機制：EMAIL/PUSH/SMS為模擬發送，IN_APP為真實發送
     */
    @Async("asyncTaskExecutor")
    @Override
    public void sendNotification(Integer userId, String title, String content, String deliveryMethod) {
        log.info("發送通知給用戶 ID: {}, 方式: {}, 標題: {}", userId, deliveryMethod, title);
        
        try {
            if ("EMAIL".equals(deliveryMethod)) {
                // 模擬發送 EMAIL
                log.info("[模擬] 發送Email給用戶ID: {}，標題: {}，內容: {}", userId, title, content);
                
            } else if ("PUSH".equals(deliveryMethod)) {
                // 模擬發送 PUSH
                log.info("[模擬] 發送推播給用戶ID: {}，標題: {}，內容: {}", userId, title, content);
                
            } else if ("SMS".equals(deliveryMethod)) {
                // 集成PhoneStorageService獲取手機號碼發送SMS
                Optional<String> phoneNumber = phoneStorageService.getPhoneByUserId(userId);
                if (phoneNumber.isPresent()) {
                    log.info("[模擬] 發送SMS給用戶ID: {}，手機號: {}，標題: {}，內容: {}", 
                        userId, phoneStorageService.validatePhoneFormat(phoneNumber.get()) ? "09****" + phoneNumber.get().substring(phoneNumber.get().length()-3) : "格式錯誤", 
                        title, content);
                } else {
                    log.warn("[SMS] 用戶 {} 沒有綁定手機號碼，跳過SMS發送", userId);
                }
                
            } else if ("IN_APP".equals(deliveryMethod)) {
                // 真實發送 IN_APP 通知
                try {
                    NotificationWebSocketHandler.sendMessageToUser(userId, content);
                    log.info("[真實] 發送站內通知給用戶ID: {}，內容: {}", userId, content);
                } catch (Exception e) {
                    log.error("發送站內通知失敗: 用戶 {}，錯誤: {}", userId, e.getMessage(), e);
                }
                
            } else {
                log.warn("未知的發送方式: {}", deliveryMethod);
            }
            
        } catch (Exception e) {
            log.error("發送通知失敗: 用戶 {}，方式 {}，錯誤: {}", userId, deliveryMethod, e.getMessage(), e);
        }
    }
} 