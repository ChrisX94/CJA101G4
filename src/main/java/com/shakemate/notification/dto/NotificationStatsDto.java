package com.shakemate.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatsDto {

    private Integer notificationId;
    private String notificationTitle;

    /**
     * 總發送量 (對應 MemberNotification 總數)
     */
    private long totalSent;

    /**
     * 成功送達數
     */
    private long successCount;

    /**
     * 發送失敗數
     */
    private long failureCount;

    /**
     * 已讀總數
     */
    private long readCount;

    /**
     * 已讀率 (readCount / totalSent)
     */
    private double readRate;

    /**
     * 點擊總數 (假設 userInteraction=1 代表點擊)
     */
    private long clickCount;

    /**
     * 點擊率 (clickCount / successCount)
     */
    private double clickRate;
} 