package com.shakemate.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class NotificationDto {

    private Integer notificationId;
    private String title;
    private String message;
    private String link;
    private String targetType;
    private List<Integer> targetIds;
    private LocalDateTime scheduledTime;
    private Integer createdBy;
    private String status;
    private Map<String, Object> renderParams;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime sentTime; // 用於顯示發送時間，可能需要從其他地方計算
    
    // 這些欄位來自 MemberNotification，用於會員端
    private Boolean isRead;
    private LocalDateTime readTime;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private String notificationType;
    private String notificationCategory;
    private Integer notificationLevel;
    private Boolean isBroadcast;
    private Map<String, Object> targetCriteria;
} 