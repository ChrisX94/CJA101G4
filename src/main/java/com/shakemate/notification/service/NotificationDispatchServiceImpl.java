package com.shakemate.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.NotificationPreference;
import com.shakemate.notification.enums.DeliveryStatus;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.notification.repository.NotificationPreferenceRepository;
import com.shakemate.notification.util.UserSpecificationBuilder;
import com.shakemate.notification.ws.NotificationWebSocketHandler;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationDispatchServiceImpl implements NotificationDispatchService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Async("asyncTaskExecutor")
    @Override
    @Transactional
    public void dispatchNotification(Notification notification) {
        log.info("開始派送通知 ID: {}", notification.getNotificationId());
        try {
            // 暫時直接使用 notification 的 title 和 message，不使用模板渲染
            String renderedTitle = notification.getTitle();
            String renderedContent = notification.getMessage();

            List<Integer> targetUserIds;

            if (notification.getTargetIds() != null && !notification.getTargetIds().isEmpty()) {
                targetUserIds = notification.getTargetIds();
                log.info("指定使用者模式，目標使用者數量: {}", targetUserIds.size());
            } else {
                 targetUserIds = usersRepository.findAllUserIds();
                 log.info("廣播模式，目標使用者數量: {}", targetUserIds.size());
            }

            if (targetUserIds.isEmpty()) {
                log.warn("通知 ID: {} 的目標使用者為空，標記為已完成但無人接收。", notification.getNotificationId());
                notification.setStatus(3); // 3: SENT_EMPTY
                notificationRepository.save(notification);
                return;
            }

            // DND 過濾邏輯
            List<Integer> finalUserIds = filterUsersByDND(targetUserIds);
            log.info("DND 過濾後，最終目標使用者數量: {}", finalUserIds.size());

            if (finalUserIds.isEmpty()) {
                log.warn("通知 ID: {} 的所有目標使用者均處於勿擾時段，標記為已完成但無人接收。", notification.getNotificationId());
                notification.setStatus(4); // 4: SENT_DND
                notificationRepository.save(notification);
                return;
            }

            List<MemberNotification> memberNotifications = finalUserIds.stream()
                .map(userId -> {
                    MemberNotification mn = new MemberNotification();
                    mn.setUser(usersRepository.getReferenceById(userId)); 
                    mn.setNotification(notification);
                    mn.setIsRead(false);
                    // 初始狀態設為 PENDING
                    mn.setDeliveryStatus(DeliveryStatus.PENDING);
                    mn.setDeliveryMethod("SYSTEM"); // 基礎派送方式
                    mn.setUserInteraction(0); // 0 for no interaction
                    mn.setRetryCount(0);
                    return mn;
                })
                .collect(Collectors.toList());

            List<MemberNotification> savedMemberNotifications = memberNotificationRepository.saveAll(memberNotifications);

            // WebSocket push logic
            sendNotificationsViaWebSocket(savedMemberNotifications);

            // Email push logic
            sendNotificationsViaEmail(savedMemberNotifications);

            notification.setStatus(2); // 1: SENT
            notificationRepository.save(notification);
            log.info("成功派送通知 ID: {}", notification.getNotificationId());

        } catch (Exception e) {
            log.error("派送通知 ID: {} 時發生錯誤", notification.getNotificationId(), e);
            notification.setStatus(3); // 2: FAILED
            notificationRepository.save(notification);
        }
    }

    private String renderTemplate(String template, Map<String, Object> params) {
        if (template == null) return "";
        if (params == null || params.isEmpty()) return template;

        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private void sendNotificationsViaWebSocket(List<MemberNotification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }
        log.info("準備透過 WebSocket 推送 {} 則通知", notifications.size());
        notifications.forEach(mn -> {
            try {
                NotificationDto dto = new NotificationDto();
                // 手動映射
                dto.setNotificationId(mn.getNotification().getNotificationId());
                dto.setTitle(mn.getNotification().getTitle());
                dto.setMessage(mn.getNotification().getMessage());
                dto.setLink(mn.getNotification().getLink());
                dto.setCreatedTime(mn.getNotification().getCreatedTime());
                dto.setIsRead(mn.getIsRead());
                dto.setReadTime(mn.getReadTime());

                String jsonMessage = objectMapper.writeValueAsString(dto);
                NotificationWebSocketHandler.sendMessageToUser(mn.getUser().getUserId(), jsonMessage);
            } catch (JsonProcessingException e) {
                log.error("序列化 NotificationDto 失敗 for user: {}", mn.getUser().getUserId(), e);
            } catch (Exception e) {
                log.error("透過 WebSocket 推送通知給使用者 {} 時發生未知錯誤", mn.getUser().getUserId(), e);
            }
        });
    }

    private void sendNotificationsViaEmail(List<MemberNotification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        List<Integer> userIds = notifications.stream().map(mn -> mn.getUser().getUserId()).distinct().collect(Collectors.toList());
        Map<Integer, NotificationPreference> prefMap = preferenceRepository.findByUser_UserIdIn(userIds).stream()
                .collect(Collectors.toMap(p -> p.getUser().getUserId(), Function.identity()));

        log.info("準備透過 Email 推送 {} 則通知", notifications.size());
        notifications.forEach(mn -> {
            try {
                NotificationPreference pref = prefMap.get(mn.getUser().getUserId());
                // 檢查使用者是否啟用了 Email 通知
                if (pref != null && pref.getEmailEnabled() != null && pref.getEmailEnabled()) {
                    String userEmail = mn.getUser().getEmail();
                    if (userEmail != null && !userEmail.isEmpty()) {
                        
                        try {
                            // 此方法現在會自動重試
                            emailService.sendHtmlEmail(mn, mn.getNotification().getTitle(), mn.getNotification().getMessage());

                            // 如果沒有拋出異常，代表發送成功 (包含重試後成功)
                            mn.setDeliveryStatus(DeliveryStatus.SUCCESS);
                            mn.setDeliveryMethod(mn.getDeliveryMethod() + ",EMAIL");
                            mn.setErrorMessage(null); // 清除舊的錯誤訊息
                            memberNotificationRepository.save(mn);

                        } catch (Exception e) {
                            // 這裡的異常是重試最終失敗後，由 @Recover 方法拋出的 (如果它拋出的話)
                            // 或者，如果 sendHtmlEmail 本身有 @Recover，異常可能不會傳播到這裡。
                            // @Recover 已經處理了失敗狀態的更新，所以這裡只需要記錄協調層級的錯誤。
                            log.error("Email 派送給使用者 {} (通知ID: {}) 最終失敗。詳細錯誤已在 EmailService 中記錄。", mn.getUser().getUserId(), mn.getNotification().getNotificationId(), e);
                        }
                        
                    } else {
                        log.warn("使用者 {} 已啟用郵件通知，但未設定 Email 地址。", mn.getUser().getUserId());
                    }
                }
            } catch (Exception e) {
                log.error("在 Email 推送循環中為使用者 {} 處理通知時發生非預期錯誤。", mn.getUser().getUserId(), e);
            }
        });
    }

    private List<Integer> filterUsersByDND(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<NotificationPreference> preferences = preferenceRepository.findByUser_UserIdIn(userIds);
        log.debug("為 {} 位使用者獲取了 {} 條偏好設定", userIds.size(), preferences.size());
        
        Map<Integer, NotificationPreference> prefMap = preferences.stream()
                .collect(Collectors.toMap(p -> p.getUser().getUserId(), Function.identity()));

        LocalTime now = LocalTime.now();

        return userIds.stream().filter(userId -> {
            NotificationPreference pref = prefMap.get(userId);
            // 預設行為：如果使用者沒有設定偏好，或者明確禁用了DND，則允許通知。
            if (pref == null || pref.getQuietHoursEnabled() == null || !pref.getQuietHoursEnabled()) {
                return true; 
            }

            LocalTime start = pref.getQuietHoursStart();
            LocalTime end = pref.getQuietHoursEnd();

            // 如果DND時間未完整設定，則視為無效，允許通知。
            if (start == null || end == null) {
                log.warn("使用者 {} 的DND時間設定不完整，本次將放行", userId);
                return true; 
            }
            
            // 判斷當前時間是否在勿擾時段內。
            // 處理跨午夜的情況 (e.g., start=22:00, end=06:00)。
            // 在這種情況下，勿擾時段被定義為 start 到午夜，以及午夜到 end。
            // 也就是說，"非勿擾"時段是從 end 到 start。
            if (start.isAfter(end)) {
                // 如果現在的時間在 end 之後，並且在 start 之前，那麼就不是勿擾時段。
                // 例如，end=06:00, start=22:00, now=10:00 -> 10:00在06:00之後且在22:00之前 -> 放行。
                return now.isAfter(end) && now.isBefore(start);
            } else {
                // 處理不跨午夜的情況 (e.g., start=09:00, end=17:00)。
                // "非勿擾"時段是 start 之前或 end 之後。
                return now.isBefore(start) || now.isAfter(end);
            }
        }).collect(Collectors.toList());
    }
} 