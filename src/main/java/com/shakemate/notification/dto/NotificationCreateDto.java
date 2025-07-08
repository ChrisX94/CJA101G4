package com.shakemate.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NotificationCreateDto {

    @NotBlank(message = "標題不能為空")
    @Size(max = 200, message = "標題不能超過200個字符")
    private String title;

    @NotBlank(message = "內容不能為空")
    @Size(max = 800, message = "內容不能超過800個字符")
    private String content;

    @NotNull(message = "通知類型不能為空")
    private String type;

    @NotNull(message = "目標類型不能為空")
    private String targetType; // ALL, SPECIFIC, TAG (新增標籤投放類型)

    private Map<String, Object> targetCriteria; // 目標條件，可以包含用戶ID列表或標籤篩選條件

    private LocalDateTime scheduledTime; // 排程發送時間

    private LocalDateTime startTime; // 通知有效起始時間
    private LocalDateTime endTime;   // 通知有效結束時間

    // Getters and Setters
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "NotificationCreateDto{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetCriteria=" + targetCriteria +
                ", scheduledTime=" + scheduledTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
} 