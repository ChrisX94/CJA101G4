package com.shakemate.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class MemberNotificationDto {
    private Long memberNotificationId;
    private Long notificationId;
    private String title;
    private String message;
    private String link;
    private Boolean isRead;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    // 預設建構子
    public MemberNotificationDto() {}

    // 建構子
    public MemberNotificationDto(Long memberNotificationId, Long notificationId, String title, String message, String link, Boolean isRead, LocalDateTime readTime, LocalDateTime createdTime) {
        this.memberNotificationId = memberNotificationId;
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.link = link;
        this.isRead = isRead;
        this.readTime = readTime;
        this.createdTime = createdTime;
    }

    // Getters and Setters
    public Long getMemberNotificationId() {
        return memberNotificationId;
    }

    public void setMemberNotificationId(Long memberNotificationId) {
        this.memberNotificationId = memberNotificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
} 