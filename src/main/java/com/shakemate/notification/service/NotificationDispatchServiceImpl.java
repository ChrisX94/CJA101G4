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
import java.util.HashMap;
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
                        
                        // 檢查勿擾時段
                        boolean inDND = isInDoNotDisturb(prefs);
                        if (inDND) {
                            log.info("用戶 {} 處於勿擾時段，跳過發送", userId);
                            return list.stream();
                        }
                        
                        // 根據通知類別找到對應的偏好設定
                        NotificationPreference categoryPref = findPreferenceByCategory(prefs, notification.getNotificationCategory());
                        
                        if (categoryPref == null) {
                            // 🔧 修復：如果找不到對應類別的偏好設定，檢查是否有預設的系統通知設定
                            NotificationPreference defaultPref = findPreferenceByCategory(prefs, "系統通知");
                            if (defaultPref != null) {
                                log.info("用戶 {} 沒有 {} 類別的偏好設定，使用系統通知類別設定", 
                                        userId, notification.getNotificationCategory());
                                categoryPref = defaultPref;
                            } else {
                                // 完全沒有偏好設定，使用系統預設值（僅站內通知）
                                log.info("用戶 {} 沒有任何偏好設定，使用系統預設值（僅站內通知）", userId);
                                list.add(buildMemberNotification(notification, userId, "IN_APP"));
                                return list.stream();
                            }
                        }
                        
                        // 根據該類別的偏好設定決定發送方式
                        if (Boolean.TRUE.equals(categoryPref.getEmailEnabled())) {
                            list.add(buildMemberNotification(notification, userId, "EMAIL"));
                            log.debug("用戶 {} 針對 {} 類別啟用了 Email 通知", userId, notification.getNotificationCategory());
                        }
                        
                        if (Boolean.TRUE.equals(categoryPref.getPushEnabled())) {
                            list.add(buildMemberNotification(notification, userId, "PUSH"));
                            log.debug("用戶 {} 針對 {} 類別啟用了推播通知", userId, notification.getNotificationCategory());
                        }
                        
                        if (Boolean.TRUE.equals(categoryPref.getSmsEnabled())) {
                            list.add(buildMemberNotification(notification, userId, "SMS"));
                            log.debug("用戶 {} 針對 {} 類別啟用了簡訊通知", userId, notification.getNotificationCategory());
                        }
                        
                        if (Boolean.TRUE.equals(categoryPref.getInAppEnabled())) {
                            list.add(buildMemberNotification(notification, userId, "IN_APP"));
                            log.debug("用戶 {} 針對 {} 類別啟用了站內通知", userId, notification.getNotificationCategory());
                        }
                        
                        // 如果用戶關閉了該類別的所有通知方式
                        if (list.isEmpty()) {
                            log.info("用戶 {} 已關閉 {} 類別的所有通知接收", userId, notification.getNotificationCategory());
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
                        // 構建結構化的通知JSON訊息
                        Map<String, Object> wsMessage = new HashMap<>();
                        wsMessage.put("type", "NOTIFICATION");
                        wsMessage.put("notificationId", notification.getNotificationId());
                        wsMessage.put("title", notification.getTitle());
                        wsMessage.put("message", notification.getMessage());
                        wsMessage.put("category", notification.getNotificationCategory());
                        wsMessage.put("timestamp", java.time.LocalDateTime.now().toString());
                        
                        String jsonMessage = objectMapper.writeValueAsString(wsMessage);
                        NotificationWebSocketHandler.sendMessageToUser(mn.getUser(), jsonMessage);
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

    // 根據通知類別查找偏好設定
    private NotificationPreference findPreferenceByCategory(List<NotificationPreference> prefs, String category) {
        if (category == null || category.isEmpty()) {
            // 如果沒有指定類別，返回第一個偏好設定作為預設
            return prefs.isEmpty() ? null : prefs.get(0);
        }
        
        return prefs.stream()
                .filter(pref -> category.equals(pref.getNotificationCategory()))
                .findFirst()
                .orElse(null); // 🔧 修復：找不到匹配類別時返回null，而不是回退到第一個偏好設定
    }

    /**
     * 發送通知給單一用戶 (業務模組對接專用)
     * 實施分級發送機制：EMAIL/PUSH/SMS為模擬發送，IN_APP為真實發送
     * 新增：根據用戶偏好設定檢查是否應該發送該類型的通知
     */
    @Async("asyncTaskExecutor")
    @Override
    public void sendNotification(Integer userId, String title, String content, String deliveryMethod) {
        sendNotificationWithCategory(userId, title, content, deliveryMethod, "系統通知");
    }
    
    /**
     * 發送通知給單一用戶（帶通知類別）
     * @param userId 用戶ID
     * @param title 通知標題
     * @param content 通知內容
     * @param deliveryMethod 發送方式
     * @param category 通知類別
     */
    @Async("asyncTaskExecutor")
    public void sendNotificationWithCategory(Integer userId, String title, String content, String deliveryMethod, String category) {
        log.info("發送通知給用戶 ID: {}, 方式: {}, 類別: {}, 標題: {}", userId, deliveryMethod, category, title);
        
        try {
            // 1. 檢查用戶偏好設定
            List<NotificationPreference> userPrefs = preferenceRepository.findByUser_UserId(userId);
            if (!userPrefs.isEmpty()) {
                // 檢查勿擾時段
                if (isInDoNotDisturb(userPrefs)) {
                    log.info("用戶 {} 處於勿擾時段，跳過發送通知", userId);
                    return;
                }
                
                // 根據通知類別檢查偏好設定
                NotificationPreference categoryPref = findPreferenceByCategory(userPrefs, category);
                if (categoryPref != null && !isDeliveryMethodEnabled(categoryPref, deliveryMethod)) {
                    log.info("用戶 {} 已關閉 {} 類別的 {} 通知接收", userId, category, deliveryMethod);
                    return;
                }
                
                // 🔧 修復：如果找不到匹配類別的偏好設定，檢查系統通知類別
                if (categoryPref == null && !"系統通知".equals(category)) {
                    NotificationPreference defaultPref = findPreferenceByCategory(userPrefs, "系統通知");
                    if (defaultPref != null && !isDeliveryMethodEnabled(defaultPref, deliveryMethod)) {
                        log.info("用戶 {} 沒有 {} 類別的偏好設定，但已關閉系統通知類別的 {} 通知接收", userId, category, deliveryMethod);
                        return;
                    }
                }
            }
            
            // 2. 執行發送邏輯
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
                    // 構建結構化的通知JSON訊息
                    Map<String, Object> wsMessage = new HashMap<>();
                    wsMessage.put("type", "NOTIFICATION");
                    wsMessage.put("title", title);
                    wsMessage.put("message", content);
                    wsMessage.put("category", category);
                    wsMessage.put("timestamp", java.time.LocalDateTime.now().toString());
                    
                    String jsonMessage = objectMapper.writeValueAsString(wsMessage);
                    NotificationWebSocketHandler.sendMessageToUser(userId, jsonMessage);
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
    
    /**
     * 檢查指定的發送方式是否在偏好設定中啟用
     */
    private boolean isDeliveryMethodEnabled(NotificationPreference pref, String deliveryMethod) {
        switch (deliveryMethod) {
            case "EMAIL":
                return Boolean.TRUE.equals(pref.getEmailEnabled());
            case "SMS":
                return Boolean.TRUE.equals(pref.getSmsEnabled());
            case "PUSH":
                return Boolean.TRUE.equals(pref.getPushEnabled());
            case "IN_APP":
                return Boolean.TRUE.equals(pref.getInAppEnabled());
            default:
                return true; // 未知類型預設允許
        }
    }
} 