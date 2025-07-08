package com.shakemate.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知調度服務接口
 * 
 * 提供定時通知、延遲通知和週期性通知的調度管理功能
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
public interface NotificationSchedulerService {

    /**
     * 調度延遲通知
     * 
     * @param userId 用戶ID
     * @param templateId 模板ID
     * @param templateData 模板數據
     * @param deliveryMethods 發送方式列表
     * @param delayMinutes 延遲分鐘數
     * @return 調度任務ID
     */
    String scheduleDelayedNotification(
        Integer userId,
        String templateId,
        Map<String, Object> templateData,
        List<String> deliveryMethods,
        int delayMinutes
    );

    /**
     * 調度定時通知
     * 
     * @param userId 用戶ID
     * @param templateId 模板ID
     * @param templateData 模板數據
     * @param deliveryMethods 發送方式列表
     * @param scheduledTime 調度時間
     * @return 調度任務ID
     */
    String scheduleTimedNotification(
        Integer userId,
        String templateId,
        Map<String, Object> templateData,
        List<String> deliveryMethods,
        LocalDateTime scheduledTime
    );

    /**
     * 調度週期性通知
     * 
     * @param userId 用戶ID
     * @param templateId 模板ID
     * @param templateData 模板數據
     * @param deliveryMethods 發送方式列表
     * @param cronExpression Cron表達式
     * @param endTime 結束時間（可選）
     * @return 調度任務ID
     */
    String scheduleRecurringNotification(
        Integer userId,
        String templateId,
        Map<String, Object> templateData,
        List<String> deliveryMethods,
        String cronExpression,
        LocalDateTime endTime
    );

    /**
     * 批量調度通知
     * 
     * @param notifications 通知列表
     * @return 調度任務ID列表
     */
    List<String> batchScheduleNotifications(List<ScheduledNotification> notifications);

    /**
     * 取消調度通知
     * 
     * @param taskId 任務ID
     * @return 是否成功取消
     */
    boolean cancelScheduledNotification(String taskId);

    /**
     * 取消用戶的所有調度通知
     * 
     * @param userId 用戶ID
     * @return 取消的任務數量
     */
    int cancelAllUserScheduledNotifications(Integer userId);

    /**
     * 獲取調度任務信息
     * 
     * @param taskId 任務ID
     * @return 任務信息
     */
    Map<String, Object> getScheduledNotificationInfo(String taskId);

    /**
     * 獲取用戶的調度通知列表
     * 
     * @param userId 用戶ID
     * @param status 狀態篩選（可選）
     * @return 調度通知列表
     */
    List<Map<String, Object>> getUserScheduledNotifications(Integer userId, String status);

    /**
     * 獲取調度統計信息
     * 
     * @param timeRange 時間範圍（HOUR, DAY, WEEK, MONTH）
     * @return 統計信息
     */
    Map<String, Object> getSchedulingStatistics(String timeRange);

    /**
     * 暫停調度任務
     * 
     * @param taskId 任務ID
     * @return 是否成功暫停
     */
    boolean pauseScheduledNotification(String taskId);

    /**
     * 恢復調度任務
     * 
     * @param taskId 任務ID
     * @return 是否成功恢復
     */
    boolean resumeScheduledNotification(String taskId);

    /**
     * 更新調度任務
     * 
     * @param taskId 任務ID
     * @param updateData 更新數據
     * @return 是否成功更新
     */
    boolean updateScheduledNotification(String taskId, Map<String, Object> updateData);

    /**
     * 檢查調度任務健康狀態
     * 
     * @return 健康狀態報告
     */
    Map<String, Object> checkSchedulerHealth();

    /**
     * 清理過期的調度任務
     * 
     * @param daysToKeep 保留天數
     * @return 清理的任務數量
     */
    int cleanupExpiredScheduledNotifications(int daysToKeep);

    /**
     * 獲取即將執行的調度任務
     * 
     * @param nextMinutes 未來分鐘數
     * @return 即將執行的任務列表
     */
    List<Map<String, Object>> getUpcomingScheduledNotifications(int nextMinutes);

    /**
     * 調度通知數據類
     */
    class ScheduledNotification {
        private Integer userId;
        private String templateId;
        private Map<String, Object> templateData;
        private List<String> deliveryMethods;
        private LocalDateTime scheduledTime;
        private String cronExpression;
        private LocalDateTime endTime;
        private String priority;
        private Map<String, Object> metadata;

        // 構造函數
        public ScheduledNotification() {}

        public ScheduledNotification(Integer userId, String templateId, Map<String, Object> templateData,
                                   List<String> deliveryMethods, LocalDateTime scheduledTime) {
            this.userId = userId;
            this.templateId = templateId;
            this.templateData = templateData;
            this.deliveryMethods = deliveryMethods;
            this.scheduledTime = scheduledTime;
        }

        // Getters and Setters
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }

        public Map<String, Object> getTemplateData() { return templateData; }
        public void setTemplateData(Map<String, Object> templateData) { this.templateData = templateData; }

        public List<String> getDeliveryMethods() { return deliveryMethods; }
        public void setDeliveryMethods(List<String> deliveryMethods) { this.deliveryMethods = deliveryMethods; }

        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

        public String getCronExpression() { return cronExpression; }
        public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
} 