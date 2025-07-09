package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.entity.MemberNotificationId;
import com.shakemate.notification.dto.MemberNotificationDto;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationPreferenceRepository;
import com.shakemate.notification.repository.NotificationTemplateRepository;
import com.shakemate.notification.entity.NotificationTemplate;
import com.shakemate.notification.util.TemplateProcessor;
import com.shakemate.notification.entity.NotificationPreference;
import com.shakemate.notification.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Optional;

/**
 * 通知服務實現類 - 簡化版本用於測試數據庫操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberNotificationRepository memberNotificationRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final TemplateProcessor templateProcessor;

    // ==================== 基本數據庫操作測試方法 ====================

    @Override
    public CompletableFuture<Boolean> sendTemplateNotification(String templateCode, Integer userId, Map<String, Object> variables) {
        log.info("發送模板通知: templateCode={}, userId={}", templateCode, userId);
        
        try {
            // 1. 查找通知模板
            Optional<NotificationTemplate> templateOpt = notificationTemplateRepository.findByTemplateCode(templateCode);
            if (!templateOpt.isPresent()) {
                log.error("找不到模板: {}", templateCode);
                return CompletableFuture.completedFuture(false);
            }
            
            NotificationTemplate template = templateOpt.get();
            
            // 檢查模板是否啟用
            if (!template.getIsActive()) {
                log.warn("模板已停用: {}", templateCode);
                return CompletableFuture.completedFuture(false);
            }
            
            // 2. 檢查用戶的站內通知偏好設定
            if (!checkUserInAppNotificationEnabled(userId, template.getTemplateCategory())) {
                log.info("用戶 {} 已關閉 {} 的站內通知接收，跳過發送", userId, template.getTemplateCategory());
                return CompletableFuture.completedFuture(true); // 返回成功但實際不發送
            }
            
            // 3. 處理模板變數替換
            String title = templateProcessor.processTemplate(template.getTitleTemplate(), variables);
            String content = templateProcessor.processTemplate(template.getContentTemplate(), variables);
            
            // 如果沒有純文字內容，嘗試使用HTML內容
            if (content == null || content.trim().isEmpty()) {
                content = templateProcessor.processTemplate(template.getHtmlTemplate(), variables);
                // 簡單去除HTML標籤用於純文字顯示
                if (content != null) {
                    content = content.replaceAll("<[^>]*>", "");
                }
            }
            
            // 4. 創建通知記錄
            Notification notification = Notification.builder()
                .notificationType(template.getTemplateType())
                .notificationCategory(template.getTemplateCategory())
                .notificationLevel(1)
                .title(title)
                .message(content)
                .isBroadcast(false)
                .validFrom(LocalDateTime.now())
                .createdBy(1)
                .status(1)
                .build();
            
            // 5. 保存通知
            notification = notificationRepository.save(notification);
            log.info("通知已保存，ID: {}, 標題: {}", notification.getNotificationId(), title);
            
            // 6. 創建會員通知記錄
            MemberNotification memberNotification = MemberNotification.builder()
                .notification(notification.getNotificationId())
                .user(userId)
                .isRead(false)
                .deliveryMethod("IN_APP")
                .deliveryStatus(DeliveryStatus.SUCCESS)
                .retryCount(0)
                .userInteraction(0)
                .build();
            
            // 7. 保存會員通知記錄
            memberNotification = memberNotificationRepository.save(memberNotification);
            log.info("會員通知記錄已保存: notificationId={}, userId={}", 
                    notification.getNotificationId(), userId);
            
            // 8. 立即發送WebSocket實時推送
            try {
                // 構建結構化的通知JSON訊息，包含完整信息
                Map<String, Object> wsMessage = new HashMap<>();
                wsMessage.put("type", "NOTIFICATION");
                wsMessage.put("notificationId", notification.getNotificationId());
                wsMessage.put("title", title);
                wsMessage.put("message", content);
                wsMessage.put("category", template.getTemplateCategory());
                wsMessage.put("isRead", false);
                wsMessage.put("sentTime", memberNotification.getSentTime() != null ? 
                    memberNotification.getSentTime().toString() : java.time.LocalDateTime.now().toString());
                wsMessage.put("timestamp", java.time.LocalDateTime.now().toString());
                
                String jsonMessage = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(wsMessage);
                com.shakemate.notification.ws.NotificationWebSocketHandler.sendMessageToUser(userId, jsonMessage);
                log.info("WebSocket推送已發送: notificationId={}, userId={}, 標題: {}", 
                        notification.getNotificationId(), userId, title);
            } catch (Exception e) {
                log.error("WebSocket推送失敗: notificationId={}, userId={}", notification.getNotificationId(), userId, e);
            }
            
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("發送模板通知失敗: templateCode={}, userId={}", templateCode, userId, e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public CompletableFuture<Boolean> sendTemplateNotificationToUsers(String templateCode, List<Integer> userIds, Map<String, Object> variables) {
        log.info("批量發送模板通知測試: templateCode={}, userCount={}", templateCode, userIds.size());
        
        try {
            // 1. 創建通知記錄
            Notification notification = Notification.builder()
                .notificationType("BATCH_TEMPLATE")
                .notificationCategory("批量測試分類")
                .notificationLevel(1)
                .title("批量測試通知: " + templateCode)
                .message("批量測試通知內容，發送給 " + userIds.size() + " 個用戶")
                .isBroadcast(true)
                .validFrom(LocalDateTime.now())
                .createdBy(1)
                .status(1)
                .build();
            
            // 2. 保存通知
            notification = notificationRepository.save(notification);
            log.info("批量通知已保存，ID: {}", notification.getNotificationId());
            
            // 3. 為每個用戶創建會員通知記錄並發送WebSocket推送
            for (Integer userId : userIds) {
                // 🔧 新增：檢查用戶的站內通知偏好設定
                if (!checkUserInAppNotificationEnabled(userId, "批量測試分類")) {
                    log.info("用戶 {} 已關閉批量測試分類的站內通知接收，跳過發送", userId);
                    continue; // 跳過此用戶，繼續處理下一個
                }
                
                MemberNotification memberNotification = MemberNotification.builder()
                    .notification(notification.getNotificationId())
                    .user(userId)
                    .isRead(false)
                    .deliveryMethod("IN_APP")
                    .deliveryStatus(DeliveryStatus.SUCCESS)
                    .retryCount(0)
                    .userInteraction(0)
                    .build();
                
                // 保存會員通知記錄
                memberNotification = memberNotificationRepository.save(memberNotification);
                
                // 立即發送WebSocket實時推送
                try {
                    String title = "批量測試通知: " + templateCode;
                    String content = "批量測試通知內容，發送給 " + userIds.size() + " 個用戶";
                    
                    // 構建結構化的通知JSON訊息
                    Map<String, Object> wsMessage = new HashMap<>();
                    wsMessage.put("type", "NOTIFICATION");
                    wsMessage.put("notificationId", notification.getNotificationId());
                    wsMessage.put("title", title);
                    wsMessage.put("message", content);
                    wsMessage.put("category", "批量測試分類");
                    wsMessage.put("isRead", false);
                    wsMessage.put("sentTime", memberNotification.getSentTime() != null ? 
                        memberNotification.getSentTime().toString() : java.time.LocalDateTime.now().toString());
                    wsMessage.put("timestamp", java.time.LocalDateTime.now().toString());
                    
                    String jsonMessage = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(wsMessage);
                    com.shakemate.notification.ws.NotificationWebSocketHandler.sendMessageToUser(userId, jsonMessage);
                    log.debug("批量WebSocket推送已發送: notificationId={}, userId={}", notification.getNotificationId(), userId);
                } catch (Exception e) {
                    log.error("批量WebSocket推送失敗: notificationId={}, userId={}", notification.getNotificationId(), userId, e);
                }
            }
            
            log.info("批量會員通知記錄已保存，共 {} 條", userIds.size());
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("批量發送模板通知失敗: templateCode={}, userIds={}", templateCode, userIds, e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public CompletableFuture<Boolean> sendInstantNotification(Integer userId, String title, String content, String notificationType, List<String> deliveryMethods) {
        log.info("發送即時通知測試: userId={}, type={}", userId, notificationType);
        
        try {
            // 1. 創建即時通知記錄
            Notification notification = Notification.builder()
                .notificationType(notificationType)
                .notificationCategory("即時通知")
                .notificationLevel(1)
                .title(title)
                .message(content)
                .isBroadcast(false)
                .validFrom(LocalDateTime.now())
                .createdBy(1)
                .status(1)
                .build();
            
            // 2. 保存通知
            notification = notificationRepository.save(notification);
            log.info("即時通知已保存，ID: {}", notification.getNotificationId());
            
            // 3. 為每種發送方式創建記錄
            for (String method : deliveryMethods) {
                MemberNotification memberNotification = MemberNotification.builder()
                    .notification(notification.getNotificationId())
                    .user(userId)
                    .isRead(false)
                    .deliveryMethod(method)
                    .deliveryStatus(DeliveryStatus.SUCCESS)
                    .retryCount(0)
                    .userInteraction(0)
                    .build();
                
                memberNotificationRepository.save(memberNotification);
            }
            
            log.info("即時通知記錄已保存: notificationId={}, userId={}, methods={}", 
                notification.getNotificationId(), userId, deliveryMethods);
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("發送即時通知失敗: userId={}", userId, e);
            return CompletableFuture.completedFuture(false);
        }
    }

    // ==================== 查詢操作測試方法 ====================

    @Override
    public Page<MemberNotificationDto> getMemberNotifications(Integer userId, Pageable pageable) {
        log.info("獲取會員通知列表: userId={}", userId);
        
        try {
            // 1. 查詢會員通知記錄 - 使用按ID倒序排列的方法
            Page<MemberNotification> memberNotificationsPage = memberNotificationRepository.findByUserOrderByNotificationDesc(userId, pageable);
            List<MemberNotification> memberNotifications = memberNotificationsPage.getContent();
            log.info("找到 {} 條會員通知記錄", memberNotifications.size());
            
            // 2. 轉換為 DTO 並按未讀狀態和時間排序
            List<MemberNotificationDto> dtos = new ArrayList<>();
            for (MemberNotification mn : memberNotifications) {
                // 查詢對應的通知詳情
                Notification notification = notificationRepository.findById(mn.getNotification()).orElse(null);
                if (notification != null) {
                    MemberNotificationDto dto = new MemberNotificationDto();
                    // 設置基本信息
                    dto.setNotificationId(notification.getNotificationId().longValue());
                    dto.setUserId(mn.getUser());
                    dto.setTitle(notification.getTitle());
                    dto.setMessage(notification.getMessage());
                    dto.setIsRead(mn.getIsRead());
                    dto.setDeliveryMethod(mn.getDeliveryMethod());
                    dto.setSentTime(mn.getSentTime());
                    dto.setReadTime(mn.getReadTime());
                    dto.setCreatedTime(notification.getCreatedTime());
                    dtos.add(dto);
                }
            }
            
            // 3. 排序：未讀通知在前，同狀態按時間倒序
            dtos.sort((a, b) -> {
                // 首先按未讀狀態排序（未讀優先）
                if (!a.getIsRead().equals(b.getIsRead())) {
                    return a.getIsRead() ? 1 : -1;
                }
                // 同狀態按發送時間倒序排序（最新的在前）
                if (a.getSentTime() != null && b.getSentTime() != null) {
                    return b.getSentTime().compareTo(a.getSentTime());
                }
                // 如果發送時間為空，按通知ID倒序
                return b.getNotificationId().compareTo(a.getNotificationId());
            });
            
            return new PageImpl<>(dtos, pageable, memberNotificationsPage.getTotalElements());
            
        } catch (Exception e) {
            log.error("獲取會員通知列表失敗: userId={}", userId, e);
            return Page.empty();
        }
    }

    @Override
    public long getUnreadCount(Integer userId) {
        log.info("獲取未讀通知數量: userId={}", userId);
        try {
            long count = memberNotificationRepository.countByUserAndIsRead(userId, false);
            log.info("用戶 {} 有 {} 條未讀通知", userId, count);
            return count;
        } catch (Exception e) {
            log.error("獲取未讀通知數量失敗: userId={}", userId, e);
            return 0L;
        }
    }

    @Override
    public void markAsRead(Integer userId, Integer notificationId) {
        log.info("標記通知為已讀: notificationId={}, userId={}", notificationId, userId);
        try {
            MemberNotificationId id = new MemberNotificationId(notificationId, userId);
            MemberNotification memberNotification = memberNotificationRepository.findById(id).orElse(null);
            if (memberNotification != null) {
                memberNotification.setIsRead(true);
                memberNotification.setReadTime(LocalDateTime.now());
                memberNotificationRepository.save(memberNotification);
                log.info("通知已標記為已讀: notificationId={}, userId={}", notificationId, userId);
            } else {
                log.warn("找不到會員通知記錄: notificationId={}, userId={}", notificationId, userId);
            }
        } catch (Exception e) {
            log.error("標記通知為已讀失敗: notificationId={}, userId={}", notificationId, userId, e);
        }
    }

    // ==================== 其他必需方法的簡化實現 ====================

    @Override
    public CompletableFuture<Boolean> broadcastTemplateNotification(String templateCode, Map<String, Object> variables, Map<String, Object> targetCriteria) {
        log.info("廣播模板通知測試: templateCode={}", templateCode);
        return sendTemplateNotificationToUsers(templateCode, Arrays.asList(1, 2, 3), variables);
    }

    @Override
    public CompletableFuture<Boolean> sendConditionalNotification(String templateCode, Map<String, Object> variables, Map<String, Object> targetCriteria) {
        log.info("條件發送通知測試: templateCode={}", templateCode);
        return sendTemplateNotificationToUsers(templateCode, Arrays.asList(1, 2), variables);
    }

    // ==================== 其他必需方法 ====================

    @Override
    public void createNotificationForUser(Integer userId, String templateKey, Map<String, Object> params) {
        log.info("創建用戶通知: userId={}, templateKey={}", userId, templateKey);
    }

    @Override
    public void createNotificationForUsers(List<Integer> userIds, String templateKey, Map<String, Object> params) {
        log.info("批量創建用戶通知: userCount={}, templateKey={}", userIds.size(), templateKey);
    }

    @Override
    public void sendManualNotification(Integer notificationId) {
        log.info("手動發送通知: notificationId={}", notificationId);
    }

    @Override
    public Page<NotificationDto> getNotificationsForUser(Integer userId, Pageable pageable) {
        return Page.empty(); // 簡化實現
    }

    @Override
    public void markAllAsRead(Integer userId) {
        log.info("標記所有通知為已讀: userId={}", userId);
        try {
            List<MemberNotification> notifications = memberNotificationRepository.findByUser(userId);
            for (MemberNotification mn : notifications) {
                if (!mn.getIsRead()) {
                    mn.setIsRead(true);
                    mn.setReadTime(LocalDateTime.now());
                    memberNotificationRepository.save(mn);
                }
            }
            log.info("已標記所有通知為已讀: userId={}", userId);
        } catch (Exception e) {
            log.error("標記所有通知為已讀失敗: userId={}", userId, e);
        }
    }

    @Override
    public Page<MemberNotificationDto> getUnreadMemberNotifications(Integer userId, Pageable pageable) {
        log.info("獲取未讀會員通知列表: userId={}", userId);
        try {
            List<MemberNotification> unread = memberNotificationRepository.findByUserAndIsReadFalseOrderByNotificationDesc(userId, pageable).getContent();
            List<MemberNotificationDto> dtos = new ArrayList<>();
            for (MemberNotification mn : unread) {
        Notification notification = notificationRepository.findById(mn.getNotification()).orElse(null);
        if (notification != null) {
                    MemberNotificationDto dto = new MemberNotificationDto();
                    dto.setNotificationId(notification.getNotificationId().longValue());
                    dto.setUserId(mn.getUser());
            dto.setTitle(notification.getTitle());
            dto.setMessage(notification.getMessage());
                    dto.setIsRead(mn.getIsRead());
                    dto.setDeliveryMethod(mn.getDeliveryMethod());
                    dto.setSentTime(mn.getSentTime());
                    dto.setReadTime(mn.getReadTime());
                    dtos.add(dto);
                }
            }
            return new PageImpl<>(dtos, pageable, dtos.size());
        } catch (Exception e) {
            log.error("獲取未讀會員通知列表失敗: userId={}", userId, e);
            return Page.empty();
        }
    }

    /**
     * 檢查用戶的站內通知偏好設定
     * @param userId 用戶ID
     * @param category 通知類別
     * @return true如果用戶啟用了該類別的站內通知，false則不發送
     */
    private boolean checkUserInAppNotificationEnabled(Integer userId, String category) {
        try {
            // 查找用戶針對特定類別的偏好設定
            Optional<NotificationPreference> preferenceOpt = notificationPreferenceRepository
                    .findByUser_UserIdAndNotificationCategory(userId, category);
            
            if (preferenceOpt.isPresent()) {
                NotificationPreference preference = preferenceOpt.get();
                // 檢查站內通知是否啟用，null值視為啟用
                Boolean inAppEnabled = preference.getInAppEnabled();
                boolean enabled = inAppEnabled == null || Boolean.TRUE.equals(inAppEnabled);
                log.debug("用戶 {} 的 {} 類別站內通知設定: {}", userId, category, enabled);
                return enabled;
            } else {
                // 沒有找到偏好設定，預設為啟用
                log.debug("用戶 {} 沒有 {} 類別的偏好設定，預設啟用站內通知", userId, category);
                return true;
            }
        } catch (Exception e) {
            log.error("檢查用戶 {} 的站內通知偏好設定時發生錯誤: {}", userId, e.getMessage(), e);
            // 發生錯誤時預設為啟用，避免影響正常功能
            return true;
        }
    }
} 