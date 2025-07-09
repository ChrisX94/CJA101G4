package com.shakemate.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.shakemate.notification.util.MapToJsonConverter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Integer notificationId;

    @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "NOTIFICATION_CATEGORY", nullable = false, length = 50)
    private String notificationCategory;

    @Column(name = "NOTIFICATION_LEVEL", nullable = false)
    private Integer notificationLevel;

    @Column(name = "NOTIFICATION_TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "NOTIFICATION_CONTENT", nullable = false, length = 800)
    private String message;

    @Column(name = "IS_BROADCAST", nullable = false)
    private Boolean isBroadcast;

    @Column(name = "TARGET_CRITERIA", columnDefinition = "json")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> targetCriteria;

    @Column(name = "VALID_FROM")
    private LocalDateTime validFrom;

    @Column(name = "VALID_UNTIL")
    private LocalDateTime validUntil;

    @Column(name = "ADM_ID", nullable = false)
    private Integer createdBy;

    @Column(name = "NOTI_STATUS", nullable = false)
    private Integer status;

    @CreationTimestamp
    @Column(name = "CREATED_TIME", updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "UPDATED_TIME")
    private LocalDateTime updatedTime;

    // @Column(name = "TARGET_TYPE")
    // private String targetType;

    // @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<MemberNotification> memberNotifications = new HashSet<>();

    // 為了與前端兼容，添加一些計算欄位的 getter
    public String getTargetType() {
        // 暫時根據isBroadcast判斷targetType
        return isBroadcast != null && isBroadcast ? "ALL" : "SPECIFIC";
    }
    
    public void setTargetType(String targetType) {
        // 暫時同步更新isBroadcast值，保持兼容性
        this.isBroadcast = "ALL".equals(targetType);
    }
    
    public List<Integer> getTargetIds() {
        // TODO: 從 TARGET_CRITERIA 中提取用戶 ID 列表
        return null;
    }
    
    public LocalDateTime getScheduledTime() {
        return validFrom;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.validFrom = scheduledTime;
    } // 🔧 添加設置排程時間的方法
    
    public LocalDateTime getSentTime() {
        // TODO: 需要從其他地方獲取實際發送時間
        return null;
    }
    
    public String getLink() {
        // TODO: 如果需要 link 欄位，可能需要從其他地方獲取
        return null;
    }
    
    public Map<String, Object> getRenderParams() {
        // TODO: 如果需要渲染參數，可能需要從其他地方獲取
        return null;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(Boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public Map<String, Object> getTargetCriteria() {
        return targetCriteria;
    }

    public void setTargetCriteria(Map<String, Object> targetCriteria) {
        this.targetCriteria = targetCriteria;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    // public Set<MemberNotification> getMemberNotifications() {
    //     return memberNotifications;
    // }

    // public void setMemberNotifications(Set<MemberNotification> memberNotifications) {
    //     this.memberNotifications = memberNotifications;
    // }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", isBroadcast=" + isBroadcast +
                ", targetCriteria=" + targetCriteria +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", createdBy=" + createdBy +
                ", status=" + status +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
} 