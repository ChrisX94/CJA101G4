package com.shakemate.notification.service;

import com.shakemate.notification.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通知調度服務實現類
 * 
 * 提供定時通知、延遲通知和週期性通知的調度管理功能
 * 基於Redis存儲和Spring Task Scheduler實現
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerServiceImpl implements NotificationSchedulerService {

    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 內存中的任務管理器，用於管理定時任務
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<String, java.util.concurrent.ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    // Redis Key前綴
    private static final String SCHEDULED_TASK_PREFIX = "scheduled:task:";
    private static final String SCHEDULED_STATS_PREFIX = "scheduled:stats:";
    private static final String SCHEDULED_USER_PREFIX = "scheduled:user:";
    
    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String scheduleDelayedNotification(
            Integer userId,
            String templateId,
            Map<String, Object> templateData,
            List<String> deliveryMethods,
            int delayMinutes) {
        
        try {
            log.info("調度延遲通知: userId={}, templateId={}, delay={}分鐘", userId, templateId, delayMinutes);
            
            // 計算執行時間
            LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(delayMinutes);
            
            // 生成任務ID
            String taskId = generateTaskId("DELAY", userId, templateId);
            
            // 創建調度任務數據
            Map<String, Object> taskData = createTaskData(
                taskId, userId, templateId, templateData, deliveryMethods, 
                scheduledTime, null, null, "DELAY", "PENDING"
            );
            
            // 存儲到Redis
            storeTaskToRedis(taskId, taskData);
            
            // 調度任務
            scheduleTask(taskId, scheduledTime, () -> {
                executeScheduledNotification(taskId);
            });
            
            // 更新統計
            updateSchedulingStatistics("DELAY", 1);
            
            log.info("延遲通知調度成功: taskId={}, 執行時間={}", taskId, scheduledTime.format(DATE_FORMATTER));
            return taskId;
            
        } catch (Exception e) {
            log.error("調度延遲通知失敗: userId={}, templateId={}", userId, templateId, e);
            return null;
        }
    }

    @Override
    public String scheduleTimedNotification(
            Integer userId,
            String templateId,
            Map<String, Object> templateData,
            List<String> deliveryMethods,
            LocalDateTime scheduledTime) {
        
        try {
            log.info("調度定時通知: userId={}, templateId={}, time={}", 
                    userId, templateId, scheduledTime.format(DATE_FORMATTER));
            
            // 檢查時間是否有效
            if (scheduledTime.isBefore(LocalDateTime.now())) {
                log.warn("調度時間不能早於當前時間: {}", scheduledTime.format(DATE_FORMATTER));
                return null;
            }
            
            // 生成任務ID
            String taskId = generateTaskId("TIMED", userId, templateId);
            
            // 創建調度任務數據
            Map<String, Object> taskData = createTaskData(
                taskId, userId, templateId, templateData, deliveryMethods, 
                scheduledTime, null, null, "TIMED", "PENDING"
            );
            
            // 存儲到Redis
            storeTaskToRedis(taskId, taskData);
            
            // 調度任務
            scheduleTask(taskId, scheduledTime, () -> {
                executeScheduledNotification(taskId);
            });
            
            // 更新統計
            updateSchedulingStatistics("TIMED", 1);
            
            log.info("定時通知調度成功: taskId={}, 執行時間={}", taskId, scheduledTime.format(DATE_FORMATTER));
            return taskId;
            
        } catch (Exception e) {
            log.error("調度定時通知失敗: userId={}, templateId={}", userId, templateId, e);
            return null;
        }
    }

    @Override
    public String scheduleRecurringNotification(
            Integer userId,
            String templateId,
            Map<String, Object> templateData,
            List<String> deliveryMethods,
            String cronExpression,
            LocalDateTime endTime) {
        
        try {
            log.info("調度週期性通知: userId={}, templateId={}, cron={}", 
                    userId, templateId, cronExpression);
            
            // 驗證Cron表達式
            if (!isValidCronExpression(cronExpression)) {
                log.warn("無效的Cron表達式: {}", cronExpression);
                return null;
            }
            
            // 生成任務ID
            String taskId = generateTaskId("RECURRING", userId, templateId);
            
            // 創建調度任務數據
            Map<String, Object> taskData = createTaskData(
                taskId, userId, templateId, templateData, deliveryMethods, 
                null, cronExpression, endTime, "RECURRING", "PENDING"
            );
            
            // 存儲到Redis
            storeTaskToRedis(taskId, taskData);
            
            // 調度週期性任務
            scheduleRecurringTask(taskId, cronExpression, endTime, () -> {
                executeScheduledNotification(taskId);
            });
            
            // 更新統計
            updateSchedulingStatistics("RECURRING", 1);
            
            log.info("週期性通知調度成功: taskId={}, cron={}", taskId, cronExpression);
            return taskId;
            
        } catch (Exception e) {
            log.error("調度週期性通知失敗: userId={}, templateId={}", userId, templateId, e);
            return null;
        }
    }

    @Override
    public List<String> batchScheduleNotifications(List<ScheduledNotification> notifications) {
        try {
            log.info("批量調度通知: count={}", notifications.size());
            
            List<String> taskIds = new ArrayList<>();
            
            for (ScheduledNotification notification : notifications) {
                String taskId = null;
                
                // 根據通知類型決定調度方式
                if (notification.getScheduledTime() != null) {
                    taskId = scheduleTimedNotification(
                        notification.getUserId(),
                        notification.getTemplateId(),
                        notification.getTemplateData(),
                        notification.getDeliveryMethods(),
                        notification.getScheduledTime()
                    );
                } else if (notification.getCronExpression() != null) {
                    taskId = scheduleRecurringNotification(
                        notification.getUserId(),
                        notification.getTemplateId(),
                        notification.getTemplateData(),
                        notification.getDeliveryMethods(),
                        notification.getCronExpression(),
                        notification.getEndTime()
                    );
                }
                
                if (taskId != null) {
                    taskIds.add(taskId);
                }
            }
            
            log.info("批量調度通知完成: 成功={}, 失敗={}", taskIds.size(), notifications.size() - taskIds.size());
            return taskIds;
            
        } catch (Exception e) {
            log.error("批量調度通知失敗", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean cancelScheduledNotification(String taskId) {
        try {
            log.info("取消調度通知: taskId={}", taskId);
            
            // 停止調度任務
            java.util.concurrent.ScheduledFuture<?> future = scheduledTasks.get(taskId);
            if (future != null) {
                future.cancel(false);
                scheduledTasks.remove(taskId);
            }
            
            // 更新Redis中的任務狀態
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null) {
                taskData.put("status", "CANCELLED");
                taskData.put("cancelledTime", LocalDateTime.now().format(DATE_FORMATTER));
                redisTemplate.opsForValue().set(redisKey, taskData);
                
                // 更新統計
                updateSchedulingStatistics("CANCELLED", 1);
                
                log.info("調度通知取消成功: taskId={}", taskId);
                return true;
            }
            
            log.warn("調度通知不存在: taskId={}", taskId);
            return false;
            
        } catch (Exception e) {
            log.error("取消調度通知失敗: taskId={}", taskId, e);
            return false;
        }
    }

    @Override
    public int cancelAllUserScheduledNotifications(Integer userId) {
        try {
            log.info("取消用戶的所有調度通知: userId={}", userId);
            
            // 獲取用戶的所有調度通知
            List<Map<String, Object>> userNotifications = getUserScheduledNotifications(userId, "PENDING");
            
            int cancelledCount = 0;
            for (Map<String, Object> notification : userNotifications) {
                String taskId = (String) notification.get("taskId");
                if (cancelScheduledNotification(taskId)) {
                    cancelledCount++;
                }
            }
            
            log.info("取消用戶調度通知完成: userId={}, count={}", userId, cancelledCount);
            return cancelledCount;
            
        } catch (Exception e) {
            log.error("取消用戶調度通知失敗: userId={}", userId, e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getScheduledNotificationInfo(String taskId) {
        try {
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null) {
                log.debug("獲取調度任務信息: taskId={}", taskId);
                return new HashMap<>(taskData);
            }
            
            log.warn("調度任務不存在: taskId={}", taskId);
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("獲取調度任務信息失敗: taskId={}", taskId, e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getUserScheduledNotifications(Integer userId, String status) {
        try {
            log.debug("獲取用戶調度通知: userId={}, status={}", userId, status);
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 獲取用戶的任務ID列表
            String userKey = SCHEDULED_USER_PREFIX + userId;
            Set<Object> taskIds = redisTemplate.opsForSet().members(userKey);
            
            if (taskIds != null) {
                for (Object taskIdObj : taskIds) {
                    String taskId = (String) taskIdObj;
                    Map<String, Object> taskData = getScheduledNotificationInfo(taskId);
                    
                    if (!taskData.isEmpty()) {
                        // 狀態篩選
                        if (status == null || status.equals(taskData.get("status"))) {
                            result.add(taskData);
                        }
                    }
                }
            }
            
            // 按創建時間排序
            result.sort((a, b) -> {
                String timeA = (String) a.get("createdTime");
                String timeB = (String) b.get("createdTime");
                return timeB.compareTo(timeA); // 最新的在前
            });
            
            log.debug("獲取用戶調度通知完成: userId={}, count={}", userId, result.size());
            return result;
            
        } catch (Exception e) {
            log.error("獲取用戶調度通知失敗: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getSchedulingStatistics(String timeRange) {
        try {
            log.debug("獲取調度統計信息: timeRange={}", timeRange);
            
            Map<String, Object> stats = new HashMap<>();
            String statsKey = SCHEDULED_STATS_PREFIX + timeRange;
            
            // 獲取統計數據
            Map<Object, Object> statsData = redisTemplate.opsForHash().entries(statsKey);
            
            // 轉換為字符串鍵值對
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : statsData.entrySet()) {
                result.put((String) entry.getKey(), entry.getValue());
            }
            
            // 添加實時統計
            result.put("totalScheduledTasks", getTotalScheduledTasks());
            result.put("activeTasks", getActiveTasks());
            result.put("completedTasks", getCompletedTasks());
            result.put("failedTasks", getFailedTasks());
            result.put("lastUpdateTime", LocalDateTime.now().format(DATE_FORMATTER));
            
            log.debug("調度統計信息獲取完成: timeRange={}, stats={}", timeRange, result);
            return result;
            
        } catch (Exception e) {
            log.error("獲取調度統計信息失敗: timeRange={}", timeRange, e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean pauseScheduledNotification(String taskId) {
        try {
            log.info("暫停調度任務: taskId={}", taskId);
            
            // 停止調度任務
            java.util.concurrent.ScheduledFuture<?> future = scheduledTasks.get(taskId);
            if (future != null) {
                future.cancel(false);
                scheduledTasks.remove(taskId);
            }
            
            // 更新Redis中的任務狀態
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null) {
                taskData.put("status", "PAUSED");
                taskData.put("pausedTime", LocalDateTime.now().format(DATE_FORMATTER));
                redisTemplate.opsForValue().set(redisKey, taskData);
                
                log.info("調度任務暫停成功: taskId={}", taskId);
                return true;
            }
            
            log.warn("調度任務不存在: taskId={}", taskId);
            return false;
            
        } catch (Exception e) {
            log.error("暫停調度任務失敗: taskId={}", taskId, e);
            return false;
        }
    }

    @Override
    public boolean resumeScheduledNotification(String taskId) {
        try {
            log.info("恢復調度任務: taskId={}", taskId);
            
            // 獲取任務數據
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null && "PAUSED".equals(taskData.get("status"))) {
                // 恢復調度
                String taskType = (String) taskData.get("taskType");
                
                if ("RECURRING".equals(taskType)) {
                    // 週期性任務恢復
                    String cronExpression = (String) taskData.get("cronExpression");
                    String endTimeStr = (String) taskData.get("endTime");
                    LocalDateTime endTime = endTimeStr != null ? 
                        LocalDateTime.parse(endTimeStr, DATE_FORMATTER) : null;
                    
                    scheduleRecurringTask(taskId, cronExpression, endTime, () -> {
                        executeScheduledNotification(taskId);
                    });
                } else {
                    // 定時任務恢復
                    String scheduledTimeStr = (String) taskData.get("scheduledTime");
                    LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr, DATE_FORMATTER);
                    
                    // 檢查是否已過期
                    if (scheduledTime.isAfter(LocalDateTime.now())) {
                        scheduleTask(taskId, scheduledTime, () -> {
                            executeScheduledNotification(taskId);
                        });
                    } else {
                        // 過期任務標記為失敗
                        taskData.put("status", "FAILED");
                        taskData.put("failureReason", "任務已過期");
                        redisTemplate.opsForValue().set(redisKey, taskData);
                        return false;
                    }
                }
                
                // 更新狀態
                taskData.put("status", "PENDING");
                taskData.put("resumedTime", LocalDateTime.now().format(DATE_FORMATTER));
                redisTemplate.opsForValue().set(redisKey, taskData);
                
                log.info("調度任務恢復成功: taskId={}", taskId);
                return true;
            }
            
            log.warn("調度任務不存在或狀態不正確: taskId={}", taskId);
            return false;
            
        } catch (Exception e) {
            log.error("恢復調度任務失敗: taskId={}", taskId, e);
            return false;
        }
    }

    @Override
    public boolean updateScheduledNotification(String taskId, Map<String, Object> updateData) {
        try {
            log.info("更新調度任務: taskId={}, updateData={}", taskId, updateData);
            
            // 獲取現有任務數據
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null) {
                // 更新數據
                taskData.putAll(updateData);
                taskData.put("updatedTime", LocalDateTime.now().format(DATE_FORMATTER));
                
                // 如果更新了調度時間，需要重新調度
                if (updateData.containsKey("scheduledTime") || updateData.containsKey("cronExpression")) {
                    // 取消原有調度
                    java.util.concurrent.ScheduledFuture<?> future = scheduledTasks.get(taskId);
                    if (future != null) {
                        future.cancel(false);
                        scheduledTasks.remove(taskId);
                    }
                    
                    // 重新調度
                    String taskType = (String) taskData.get("taskType");
                    if ("RECURRING".equals(taskType)) {
                        String cronExpression = (String) taskData.get("cronExpression");
                        String endTimeStr = (String) taskData.get("endTime");
                        LocalDateTime endTime = endTimeStr != null ? 
                            LocalDateTime.parse(endTimeStr, DATE_FORMATTER) : null;
                        
                        scheduleRecurringTask(taskId, cronExpression, endTime, () -> {
                            executeScheduledNotification(taskId);
                        });
                    } else {
                        String scheduledTimeStr = (String) taskData.get("scheduledTime");
                        LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr, DATE_FORMATTER);
                        
                        scheduleTask(taskId, scheduledTime, () -> {
                            executeScheduledNotification(taskId);
                        });
                    }
                }
                
                // 保存更新後的數據
                redisTemplate.opsForValue().set(redisKey, taskData);
                
                log.info("調度任務更新成功: taskId={}", taskId);
                return true;
            }
            
            log.warn("調度任務不存在: taskId={}", taskId);
            return false;
            
        } catch (Exception e) {
            log.error("更新調度任務失敗: taskId={}", taskId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> checkSchedulerHealth() {
        try {
            log.debug("檢查調度器健康狀態");
            
            Map<String, Object> healthReport = new HashMap<>();
            
            // 基本信息
            healthReport.put("schedulerStatus", "HEALTHY");
            healthReport.put("checkTime", LocalDateTime.now().format(DATE_FORMATTER));
            healthReport.put("activeThreads", scheduler.isShutdown() ? 0 : 10);
            
            // 任務統計
            healthReport.put("totalTasks", getTotalScheduledTasks());
            healthReport.put("activeTasks", getActiveTasks());
            healthReport.put("pausedTasks", getPausedTasks());
            healthReport.put("completedTasks", getCompletedTasks());
            healthReport.put("failedTasks", getFailedTasks());
            
            // Redis連接檢查
            try {
                redisTemplate.opsForValue().get("health:check");
                healthReport.put("redisStatus", "HEALTHY");
            } catch (Exception e) {
                healthReport.put("redisStatus", "UNHEALTHY");
                healthReport.put("redisError", e.getMessage());
            }
            
            // 內存使用情況
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            healthReport.put("memoryUsage", Map.of(
                "max", maxMemory,
                "total", totalMemory,
                "used", usedMemory,
                "free", freeMemory,
                "usagePercentage", (usedMemory * 100) / maxMemory
            ));
            
            log.debug("調度器健康檢查完成: {}", healthReport);
            return healthReport;
            
        } catch (Exception e) {
            log.error("調度器健康檢查失敗", e);
            Map<String, Object> errorReport = new HashMap<>();
            errorReport.put("schedulerStatus", "UNHEALTHY");
            errorReport.put("error", e.getMessage());
            errorReport.put("checkTime", LocalDateTime.now().format(DATE_FORMATTER));
            return errorReport;
        }
    }

    @Override
    public int cleanupExpiredScheduledNotifications(int daysToKeep) {
        try {
            log.info("清理過期調度通知: 保留{}天", daysToKeep);
            
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
            int cleanedCount = 0;
            
            // 獲取所有調度任務
            Set<String> taskKeys = redisTemplate.keys(SCHEDULED_TASK_PREFIX + "*");
            
            if (taskKeys != null) {
                for (String taskKey : taskKeys) {
                    Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(taskKey);
                    
                    if (taskData != null) {
                        String createdTimeStr = (String) taskData.get("createdTime");
                        LocalDateTime createdTime = LocalDateTime.parse(createdTimeStr, DATE_FORMATTER);
                        
                        // 檢查是否已過期且已完成或失敗
                        String status = (String) taskData.get("status");
                        if (createdTime.isBefore(cutoffTime) && 
                            ("COMPLETED".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status))) {
                            
                            // 刪除任務
                            redisTemplate.delete(taskKey);
                            
                            // 從用戶集合中刪除
                            Integer userId = (Integer) taskData.get("userId");
                            if (userId != null) {
                                String userKey = SCHEDULED_USER_PREFIX + userId;
                                String taskId = taskKey.replace(SCHEDULED_TASK_PREFIX, "");
                                redisTemplate.opsForSet().remove(userKey, taskId);
                            }
                            
                            cleanedCount++;
                        }
                    }
                }
            }
            
            log.info("清理過期調度通知完成: 清理{}個任務", cleanedCount);
            return cleanedCount;
            
        } catch (Exception e) {
            log.error("清理過期調度通知失敗", e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getUpcomingScheduledNotifications(int nextMinutes) {
        try {
            log.debug("獲取即將執行的調度任務: 未來{}分鐘", nextMinutes);
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureTime = now.plusMinutes(nextMinutes);
            
            List<Map<String, Object>> upcomingTasks = new ArrayList<>();
            
            // 獲取所有調度任務
            Set<String> taskKeys = redisTemplate.keys(SCHEDULED_TASK_PREFIX + "*");
            
            if (taskKeys != null) {
                for (String taskKey : taskKeys) {
                    Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(taskKey);
                    
                    if (taskData != null && "PENDING".equals(taskData.get("status"))) {
                        String scheduledTimeStr = (String) taskData.get("scheduledTime");
                        
                        if (scheduledTimeStr != null) {
                            LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr, DATE_FORMATTER);
                            
                            // 檢查是否在指定時間範圍內
                            if (scheduledTime.isAfter(now) && scheduledTime.isBefore(futureTime)) {
                                upcomingTasks.add(new HashMap<>(taskData));
                            }
                        }
                    }
                }
            }
            
            // 按執行時間排序
            upcomingTasks.sort((a, b) -> {
                String timeA = (String) a.get("scheduledTime");
                String timeB = (String) b.get("scheduledTime");
                return timeA.compareTo(timeB);
            });
            
            log.debug("即將執行的調度任務: count={}", upcomingTasks.size());
            return upcomingTasks;
            
        } catch (Exception e) {
            log.error("獲取即將執行的調度任務失敗", e);
            return new ArrayList<>();
        }
    }

    // ==================== 私有輔助方法 ====================

    /**
     * 生成任務ID
     */
    private String generateTaskId(String type, Integer userId, String templateId) {
        return String.format("%s_%d_%s_%d", type, userId, templateId, System.currentTimeMillis());
    }

    /**
     * 創建任務數據
     */
    private Map<String, Object> createTaskData(
            String taskId, Integer userId, String templateId, 
            Map<String, Object> templateData, List<String> deliveryMethods,
            LocalDateTime scheduledTime, String cronExpression, LocalDateTime endTime,
            String taskType, String status) {
        
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("taskId", taskId);
        taskData.put("userId", userId);
        taskData.put("templateId", templateId);
        taskData.put("templateData", templateData);
        taskData.put("deliveryMethods", deliveryMethods);
        taskData.put("taskType", taskType);
        taskData.put("status", status);
        taskData.put("createdTime", LocalDateTime.now().format(DATE_FORMATTER));
        
        if (scheduledTime != null) {
            taskData.put("scheduledTime", scheduledTime.format(DATE_FORMATTER));
        }
        if (cronExpression != null) {
            taskData.put("cronExpression", cronExpression);
        }
        if (endTime != null) {
            taskData.put("endTime", endTime.format(DATE_FORMATTER));
        }
        
        return taskData;
    }

    /**
     * 存儲任務到Redis
     */
    private void storeTaskToRedis(String taskId, Map<String, Object> taskData) {
        // 存儲任務數據
        String taskKey = SCHEDULED_TASK_PREFIX + taskId;
        redisTemplate.opsForValue().set(taskKey, taskData);
        
        // 添加到用戶集合
        Integer userId = (Integer) taskData.get("userId");
        String userKey = SCHEDULED_USER_PREFIX + userId;
        redisTemplate.opsForSet().add(userKey, taskId);
    }

    /**
     * 調度任務
     */
    private void scheduleTask(String taskId, LocalDateTime scheduledTime, Runnable task) {
        long delay = java.time.Duration.between(LocalDateTime.now(), scheduledTime).toMillis();
        
        if (delay > 0) {
            java.util.concurrent.ScheduledFuture<?> future = scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
            scheduledTasks.put(taskId, future);
        } else {
            log.warn("調度時間已過期: taskId={}, scheduledTime={}", taskId, scheduledTime);
        }
    }

    /**
     * 調度週期性任務
     */
    private void scheduleRecurringTask(String taskId, String cronExpression, LocalDateTime endTime, Runnable task) {
        // 簡化的週期性任務調度實現
        // 實際項目中可以使用更複雜的Cron解析器
        
        // 這裡使用固定間隔作為示例
        long interval = parseCronToInterval(cronExpression);
        
        if (interval > 0) {
            java.util.concurrent.ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> {
                    if (endTime == null || LocalDateTime.now().isBefore(endTime)) {
                        task.run();
                    } else {
                        // 結束時間已到，取消任務
                        cancelScheduledNotification(taskId);
                    }
                },
                interval, interval, TimeUnit.MILLISECONDS
            );
            scheduledTasks.put(taskId, future);
        }
    }

    /**
     * 執行調度通知
     */
    private void executeScheduledNotification(String taskId) {
        try {
            log.info("執行調度通知: taskId={}", taskId);
            
            // 獲取任務數據
            String redisKey = SCHEDULED_TASK_PREFIX + taskId;
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            
            if (taskData != null) {
                // 提取任務信息
                Integer userId = (Integer) taskData.get("userId");
                String templateId = (String) taskData.get("templateId");
                Map<String, Object> templateData = (Map<String, Object>) taskData.get("templateData");
                
                // 發送通知
                notificationService.sendTemplateNotification(templateId, userId, templateData);
                
                // 更新任務狀態
                taskData.put("status", "COMPLETED");
                taskData.put("executedTime", LocalDateTime.now().format(DATE_FORMATTER));
                redisTemplate.opsForValue().set(redisKey, taskData);
                
                // 更新統計
                updateSchedulingStatistics("COMPLETED", 1);
                
                log.info("調度通知執行成功: taskId={}", taskId);
            }
            
        } catch (Exception e) {
            log.error("執行調度通知失敗: taskId={}", taskId, e);
            
            // 更新任務狀態為失敗
            try {
                String redisKey = SCHEDULED_TASK_PREFIX + taskId;
                Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
                if (taskData != null) {
                    taskData.put("status", "FAILED");
                    taskData.put("failureReason", e.getMessage());
                    taskData.put("failedTime", LocalDateTime.now().format(DATE_FORMATTER));
                    redisTemplate.opsForValue().set(redisKey, taskData);
                }
            } catch (Exception ex) {
                log.error("更新任務失敗狀態時發生異常", ex);
            }
        }
    }

    /**
     * 驗證Cron表達式
     */
    private boolean isValidCronExpression(String cronExpression) {
        // 簡化的Cron表達式驗證
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return false;
        }
        
        // 基本格式檢查：至少包含5個部分（秒 分 時 日 月）
        String[] parts = cronExpression.trim().split("\\s+");
        return parts.length >= 5;
    }

    /**
     * 解析Cron表達式為間隔時間（毫秒）
     */
    private long parseCronToInterval(String cronExpression) {
        // 簡化實現，實際項目中應使用專業的Cron解析器
        
        // 默認間隔（1小時）
        long defaultInterval = 60 * 60 * 1000;
        
        try {
            // 這裡可以根據Cron表達式解析出具體的間隔時間
            // 為了簡化，我們返回默認間隔
            return defaultInterval;
        } catch (Exception e) {
            log.warn("解析Cron表達式失敗: {}", cronExpression);
            return defaultInterval;
        }
    }

    /**
     * 更新調度統計信息
     */
    private void updateSchedulingStatistics(String type, int count) {
        try {
            String statsKey = SCHEDULED_STATS_PREFIX + "daily";
            redisTemplate.opsForHash().increment(statsKey, type, count);
            
            // 設置過期時間（保留7天）
            redisTemplate.expire(statsKey, 7, TimeUnit.DAYS);
            
        } catch (Exception e) {
            log.warn("更新調度統計失敗: type={}, count={}", type, count, e);
        }
    }

    /**
     * 獲取總調度任務數
     */
    private long getTotalScheduledTasks() {
        try {
            Set<String> taskKeys = redisTemplate.keys(SCHEDULED_TASK_PREFIX + "*");
            return taskKeys != null ? taskKeys.size() : 0;
        } catch (Exception e) {
            log.warn("獲取總調度任務數失敗", e);
            return 0;
        }
    }

    /**
     * 獲取活動任務數
     */
    private long getActiveTasks() {
        return getTaskCountByStatus("PENDING");
    }

    /**
     * 獲取暫停任務數
     */
    private long getPausedTasks() {
        return getTaskCountByStatus("PAUSED");
    }

    /**
     * 獲取完成任務數
     */
    private long getCompletedTasks() {
        return getTaskCountByStatus("COMPLETED");
    }

    /**
     * 獲取失敗任務數
     */
    private long getFailedTasks() {
        return getTaskCountByStatus("FAILED");
    }

    /**
     * 按狀態獲取任務數
     */
    private long getTaskCountByStatus(String status) {
        try {
            Set<String> taskKeys = redisTemplate.keys(SCHEDULED_TASK_PREFIX + "*");
            if (taskKeys == null) {
                return 0;
            }
            
            return taskKeys.stream()
                    .map(key -> (Map<String, Object>) redisTemplate.opsForValue().get(key))
                    .filter(Objects::nonNull)
                    .filter(taskData -> status.equals(taskData.get("status")))
                    .count();
                    
        } catch (Exception e) {
            log.warn("獲取{}任務數失敗", status, e);
            return 0;
        }
    }

    /**
     * 定期清理過期任務
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2點執行
    public void scheduledCleanup() {
        log.info("開始定期清理過期調度任務");
        int cleanedCount = cleanupExpiredScheduledNotifications(7); // 保留7天
        log.info("定期清理完成，清理了{}個過期任務", cleanedCount);
    }
} 