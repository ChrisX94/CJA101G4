package com.shakemate.notification.entity;

import com.shakemate.adm.model.AdmVO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.shakemate.notification.util.MapToJsonConverter;

@Data
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

    // 暫時註解掉，因為資料庫中沒有 template_id 欄位
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "template_id")
    // private NotificationTemplate notificationTemplate;
    
    // 為了與前端兼容，添加一些計算欄位的 getter
    public String getTargetType() {
        return isBroadcast != null && isBroadcast ? "BROADCAST" : "SELECTIVE";
    }
    
    public List<Integer> getTargetIds() {
        // TODO: 從 TARGET_CRITERIA 中提取用戶 ID 列表
        return null;
    }
    
    public LocalDateTime getScheduledTime() {
        return validFrom;
    }
    
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
} 