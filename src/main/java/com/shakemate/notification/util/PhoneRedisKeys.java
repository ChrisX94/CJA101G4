package com.shakemate.notification.util;

/**
 * Redis Key 管理工具類 - 手機號碼存儲專用
 * 
 * 提供統一的Redis Key命名規範，支援雙向索引：
 * 1. 用戶ID -> 手機號碼 (user:phone:{userId})
 * 2. 手機號碼 -> 用戶ID (phone:user:{hashedPhone})
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
public class PhoneRedisKeys {
    
    // Redis Key 前綴常數
    private static final String USER_PHONE_PREFIX = "user:phone:";
    private static final String PHONE_USER_PREFIX = "phone:user:";
    private static final String PHONE_SYNC_PREFIX = "phone:sync:";
    
    // TTL 常數 (秒)
    public static final long DEFAULT_TTL = 30 * 24 * 60 * 60; // 30天
    public static final long SYNC_LOCK_TTL = 5 * 60; // 5分鐘
    
    /**
     * 獲取用戶手機號碼的Redis Key
     * 格式: user:phone:{userId}
     * 
     * @param userId 用戶ID
     * @return Redis Key
     */
    public static String getUserPhoneKey(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用戶ID不能為空");
        }
        return USER_PHONE_PREFIX + userId;
    }
    
    /**
     * 獲取手機號碼對應用戶的Redis Key  
     * 格式: phone:user:{hashedPhone}
     * 
     * @param hashedPhone 已雜湊的手機號碼
     * @return Redis Key
     */
    public static String getPhoneUserKey(String hashedPhone) {
        if (hashedPhone == null || hashedPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("雜湊手機號碼不能為空");
        }
        return PHONE_USER_PREFIX + hashedPhone;
    }
    
    /**
     * 獲取手機號碼同步鎖的Redis Key
     * 格式: phone:sync:lock
     * 
     * @return Redis Key
     */
    public static String getSyncLockKey() {
        return PHONE_SYNC_PREFIX + "lock";
    }
    
    /**
     * 獲取同步統計資訊的Redis Key
     * 格式: phone:sync:stats:{timestamp}
     * 
     * @param timestamp 時間戳
     * @return Redis Key
     */
    public static String getSyncStatsKey(long timestamp) {
        return PHONE_SYNC_PREFIX + "stats:" + timestamp;
    }
    
    /**
     * 驗證Redis Key格式是否正確
     * 
     * @param key Redis Key
     * @return 是否為有效的手機號碼相關Key
     */
    public static boolean isValidPhoneKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        return key.startsWith(USER_PHONE_PREFIX) || 
               key.startsWith(PHONE_USER_PREFIX) || 
               key.startsWith(PHONE_SYNC_PREFIX);
    }
    
    /**
     * 從用戶手機號碼Key中提取用戶ID
     * 
     * @param userPhoneKey 用戶手機號碼Key
     * @return 用戶ID，解析失敗時返回null
     */
    public static Integer extractUserIdFromKey(String userPhoneKey) {
        if (userPhoneKey == null || !userPhoneKey.startsWith(USER_PHONE_PREFIX)) {
            return null;
        }
        
        try {
            String userIdStr = userPhoneKey.substring(USER_PHONE_PREFIX.length());
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 從手機號碼用戶Key中提取雜湊手機號碼
     * 
     * @param phoneUserKey 手機號碼用戶Key
     * @return 雜湊手機號碼，解析失敗時返回null
     */
    public static String extractHashedPhoneFromKey(String phoneUserKey) {
        if (phoneUserKey == null || !phoneUserKey.startsWith(PHONE_USER_PREFIX)) {
            return null;
        }
        
        return phoneUserKey.substring(PHONE_USER_PREFIX.length());
    }
    
    /**
     * 獲取所有用戶手機號碼Key的通配符模式
     * 
     * @return 通配符模式 "user:phone:*"
     */
    public static String getUserPhonePattern() {
        return USER_PHONE_PREFIX + "*";
    }
    
    /**
     * 獲取所有手機號碼用戶Key的通配符模式
     * 
     * @return 通配符模式 "phone:user:*"
     */
    public static String getPhoneUserPattern() {
        return PHONE_USER_PREFIX + "*";
    }
} 