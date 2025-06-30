package com.shakemate.notification.entity;

import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "NOTIFICATION_PREFERENCE")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PREFERENCE_ID")
    private Integer preferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @Column(name = "NOTIFICATION_CATEGORY", nullable = false, length = 50)
    private String notificationCategory;

    @Column(name = "EMAIL_ENABLED")
    private Boolean emailEnabled;

    @Column(name = "SMS_ENABLED")
    private Boolean smsEnabled;

    @Column(name = "PUSH_ENABLED")
    private Boolean pushEnabled;

    @Column(name = "IN_APP_ENABLED")
    private Boolean inAppEnabled;

    @Column(name = "QUIET_HOURS_ENABLED")
    private Boolean quietHoursEnabled;

    @Column(name = "QUIET_HOURS_START")
    private LocalTime quietHoursStart;

    @Column(name = "QUIET_HOURS_END")
    private LocalTime quietHoursEnd;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
} 