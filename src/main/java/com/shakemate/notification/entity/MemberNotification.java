package com.shakemate.notification.entity;

import com.shakemate.notification.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBER_NOTIFICATION")
@IdClass(MemberNotificationId.class)
public class MemberNotification {

    @Id
    @Column(name = "NOTIFICATION_ID", nullable = false)
    private Integer notification;

    @Id
    @Column(name = "USER_ID", nullable = false)
    private Integer user;

    @Column(name = "IS_READ", nullable = false)
    private Boolean isRead = false;

    @Column(name = "READ_TIME")
    private LocalDateTime readTime;

    @CreationTimestamp
    @Column(name = "SENT_TIME", updatable = false)
    private LocalDateTime sentTime;

    @Column(name = "DELIVERY_METHOD", nullable = false, length = 20)
    private String deliveryMethod;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "DELIVERY_STATUS", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "RETRY_COUNT")
    private Integer retryCount = 0;

    @Column(name = "LAST_RETRY_TIME")
    private LocalDateTime lastRetryTime;

    @Column(name = "ERROR_MESSAGE", length = 400)
    private String errorMessage;

    @Column(name = "USER_INTERACTION", nullable = false)
    private Integer userInteraction;

    @Column(name = "INTERACTION_TIME")
    private LocalDateTime interactionTime;

    @Column(name = "TRACKING_DATA", columnDefinition = "json")
    private String trackingData;

    @Column(name = "DEVICE_INFO")
    private String deviceInfo;
} 