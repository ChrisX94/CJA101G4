package com.shakemate.notification.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知發送預覽DTO
 * 用於發送前展示通知詳情，包括預估接收人數等
 */
public class NotificationPreviewDto {
    private Integer notificationId;
    private String title;
    private String content;
    private String type;
    private String targetType;
    private Integer estimatedRecipients; // 預估接收人數
    private Map<String, Object> targetCriteria; // 目標篩選條件
    private LocalDateTime scheduledTime; // 排程發送時間
    private String statusDescription; // 狀態描述
    
    // 構造函數
    public NotificationPreviewDto() {
    }

    // Getters and Setters
    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getEstimatedRecipients() {
        return estimatedRecipients;
    }

    public void setEstimatedRecipients(Integer estimatedRecipients) {
        this.estimatedRecipients = estimatedRecipients;
    }

    public Map<String, Object> getTargetCriteria() {
        return targetCriteria;
    }

    public void setTargetCriteria(Map<String, Object> targetCriteria) {
        this.targetCriteria = targetCriteria;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "NotificationPreviewDto{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", targetType='" + targetType + '\'' +
                ", estimatedRecipients=" + estimatedRecipients +
                ", targetCriteria=" + targetCriteria +
                ", scheduledTime=" + scheduledTime +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }
} 