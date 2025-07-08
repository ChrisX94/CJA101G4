package com.shakemate.notification.dto;

import lombok.Data;

@Data
public class NotificationReportDto {

    private Integer notificationId;
    private String notificationTitle;
    private long totalSent;
    private long successCount;
    private long failureCount;
    private double successRate;
    private long readCount;
    private double readRate;
    private long clickCount;
    private double clickRate;
    private Object sendTrend;

} 