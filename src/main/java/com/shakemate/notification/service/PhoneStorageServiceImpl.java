package com.shakemate.notification.service;

import com.shakemate.notification.util.EncryptionUtil;
import com.shakemate.notification.util.PhoneRedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 手機號碼存儲服務實現類
 * 
 * 使用Redis實現用戶ID與手機號碼的雙向映射存儲：
 * 1. user:phone:{userId} -> 加密的手機號碼
 * 2. phone:user:{hashedPhone} -> 用戶ID
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Service
@Slf4j
public class PhoneStorageServiceImpl implements PhoneStorageService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private EncryptionUtil encryptionUtil;
    
    // ==================== 基本CRUD操作 ====================
    
    @Override
    public boolean storeUserPhone(Integer userId, String phoneNumber) {
        if (userId == null || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("用戶ID和手機號碼不能為空");
        }
        
        // 驗證手機號碼格式
        if (!encryptionUtil.isValidTaiwanMobile(phoneNumber)) {
            throw new IllegalArgumentException("手機號碼格式不正確：" + encryptionUtil.maskPhone(phoneNumber));
        }
        
        try {
            // 檢查手機號碼是否已被其他用戶使用
            Optional<Integer> existingUserId = getUserIdByPhone(phoneNumber);
            if (existingUserId.isPresent() && !existingUserId.get().equals(userId)) {
                log.warn("手機號碼 {} 已被用戶 {} 使用", encryptionUtil.maskPhone(phoneNumber), existingUserId.get());
                return false;
            }
            
            // 清理該用戶的舊手機號碼記錄
            deleteUserPhone(userId);
            
            // 加密手機號碼和生成雜湊
            String encryptedPhone = encryptionUtil.encryptPhone(phoneNumber);
            String hashedPhone = encryptionUtil.hashPhone(phoneNumber);
            
            // 存儲雙向映射
            String userPhoneKey = PhoneRedisKeys.getUserPhoneKey(userId);
            String phoneUserKey = PhoneRedisKeys.getPhoneUserKey(hashedPhone);
            
            redisTemplate.opsForValue().set(userPhoneKey, encryptedPhone, PhoneRedisKeys.DEFAULT_TTL, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(phoneUserKey, userId, PhoneRedisKeys.DEFAULT_TTL, TimeUnit.SECONDS);
            
            log.info("成功存儲用戶 {} 的手機號碼 {}", userId, encryptionUtil.maskPhone(phoneNumber));
            return true;
            
        } catch (Exception e) {
            log.error("存儲用戶 {} 的手機號碼失敗: {}", userId, e.getMessage(), e);
            throw new RuntimeException("存儲手機號碼失敗", e);
        }
    }
    
    @Override
    public Optional<String> getPhoneByUserId(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        
        try {
            String userPhoneKey = PhoneRedisKeys.getUserPhoneKey(userId);
            Object encryptedPhone = redisTemplate.opsForValue().get(userPhoneKey);
            
            if (encryptedPhone != null) {
                String phoneNumber = encryptionUtil.decryptPhone(encryptedPhone.toString());
                log.debug("查詢到用戶 {} 的手機號碼: {}", userId, encryptionUtil.maskPhone(phoneNumber));
                return Optional.of(phoneNumber);
            }
            
            log.debug("用戶 {} 沒有綁定手機號碼", userId);
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("查詢用戶 {} 的手機號碼失敗: {}", userId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Integer> getUserIdByPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            String hashedPhone = encryptionUtil.hashPhone(phoneNumber);
            String phoneUserKey = PhoneRedisKeys.getPhoneUserKey(hashedPhone);
            Object userId = redisTemplate.opsForValue().get(phoneUserKey);
            
            if (userId != null) {
                Integer userIdInt = Integer.valueOf(userId.toString());
                log.debug("查詢到手機號碼 {} 對應的用戶ID: {}", encryptionUtil.maskPhone(phoneNumber), userIdInt);
                return Optional.of(userIdInt);
            }
            
            log.debug("手機號碼 {} 沒有對應的用戶", encryptionUtil.maskPhone(phoneNumber));
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("查詢手機號碼 {} 對應的用戶ID失敗: {}", encryptionUtil.maskPhone(phoneNumber), e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean updateUserPhone(Integer userId, String newPhoneNumber) {
        if (userId == null || newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("用戶ID和新手機號碼不能為空");
        }
        
        try {
            // 先刪除舊記錄，再添加新記錄
            deleteUserPhone(userId);
            return storeUserPhone(userId, newPhoneNumber);
            
        } catch (Exception e) {
            log.error("更新用戶 {} 的手機號碼失敗: {}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteUserPhone(Integer userId) {
        if (userId == null) {
            return false;
        }
        
        try {
            // 先獲取舊手機號碼用於刪除反向映射
            Optional<String> oldPhone = getPhoneByUserId(userId);
            
            String userPhoneKey = PhoneRedisKeys.getUserPhoneKey(userId);
            boolean userPhoneDeleted = Boolean.TRUE.equals(redisTemplate.delete(userPhoneKey));
            
            boolean phoneUserDeleted = true;
            if (oldPhone.isPresent()) {
                String hashedPhone = encryptionUtil.hashPhone(oldPhone.get());
                String phoneUserKey = PhoneRedisKeys.getPhoneUserKey(hashedPhone);
                phoneUserDeleted = Boolean.TRUE.equals(redisTemplate.delete(phoneUserKey));
            }
            
            if (userPhoneDeleted || phoneUserDeleted) {
                log.info("成功刪除用戶 {} 的手機號碼記錄", userId);
                return true;
            }
            
            log.debug("用戶 {} 沒有手機號碼記錄需要刪除", userId);
            return false;
            
        } catch (Exception e) {
            log.error("刪除用戶 {} 的手機號碼失敗: {}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== 批量操作 ====================
    
    @Override
    public int batchStoreUserPhones(Map<Integer, String> userPhoneMap) {
        if (userPhoneMap == null || userPhoneMap.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Map.Entry<Integer, String> entry : userPhoneMap.entrySet()) {
            try {
                if (storeUserPhone(entry.getKey(), entry.getValue())) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量存儲時處理用戶 {} 失敗: {}", entry.getKey(), e.getMessage(), e);
            }
        }
        
        log.info("批量存儲完成，成功 {} 個，總計 {} 個", successCount, userPhoneMap.size());
        return successCount;
    }
    
    @Override
    public Map<Integer, String> batchGetPhonesByUserIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<Integer, String> result = new HashMap<>();
        for (Integer userId : userIds) {
            Optional<String> phone = getPhoneByUserId(userId);
            phone.ifPresent(phoneNumber -> result.put(userId, phoneNumber));
        }
        
        log.debug("批量查詢用戶手機號碼，查詢 {} 個用戶，找到 {} 個手機號碼", userIds.size(), result.size());
        return result;
    }
    
    @Override
    public Map<String, Integer> batchGetUserIdsByPhones(List<String> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, Integer> result = new HashMap<>();
        for (String phoneNumber : phoneNumbers) {
            Optional<Integer> userId = getUserIdByPhone(phoneNumber);
            userId.ifPresent(id -> result.put(phoneNumber, id));
        }
        
        log.debug("批量查詢手機號碼用戶，查詢 {} 個手機號碼，找到 {} 個用戶", phoneNumbers.size(), result.size());
        return result;
    }
    
    @Override
    public int batchDeleteUserPhones(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Integer userId : userIds) {
            if (deleteUserPhone(userId)) {
                successCount++;
            }
        }
        
        log.info("批量刪除完成，成功 {} 個，總計 {} 個", successCount, userIds.size());
        return successCount;
    }
    
    // ==================== 數據檢查與驗證 ====================
    
    @Override
    public boolean isPhoneExists(String phoneNumber) {
        return getUserIdByPhone(phoneNumber).isPresent();
    }
    
    @Override
    public boolean isUserPhoneBound(Integer userId) {
        return getPhoneByUserId(userId).isPresent();
    }
    
    @Override
    public boolean validatePhoneFormat(String phoneNumber) {
        return encryptionUtil.isValidTaiwanMobile(phoneNumber);
    }
    
    // ==================== 同步與維護 ====================
    
    @Override
    public Map<String, Object> syncDataConsistency() {
        log.info("開始檢查手機號碼存儲的數據一致性...");
        
        Map<String, Object> result = new HashMap<>();
        int inconsistentCount = 0;
        int totalChecked = 0;
        List<String> inconsistentRecords = new ArrayList<>();
        
        try {
            // 獲取所有用戶手機號碼鍵
            Set<String> userPhoneKeys = redisTemplate.keys(PhoneRedisKeys.getUserPhonePattern());
            
            if (userPhoneKeys != null) {
                for (String userPhoneKey : userPhoneKeys) {
                    totalChecked++;
                    Integer userId = PhoneRedisKeys.extractUserIdFromKey(userPhoneKey);
                    
                    if (userId != null) {
                        Optional<String> phone = getPhoneByUserId(userId);
                        if (phone.isPresent()) {
                            Optional<Integer> reverseUserId = getUserIdByPhone(phone.get());
                            if (!reverseUserId.isPresent() || !reverseUserId.get().equals(userId)) {
                                inconsistentCount++;
                                inconsistentRecords.add(String.format("用戶 %d 的手機號碼 %s 反向映射不一致", 
                                    userId, encryptionUtil.maskPhone(phone.get())));
                            }
                        }
                    }
                }
            }
            
            result.put("totalChecked", totalChecked);
            result.put("inconsistentCount", inconsistentCount);
            result.put("inconsistentRecords", inconsistentRecords);
            result.put("isConsistent", inconsistentCount == 0);
            
            log.info("數據一致性檢查完成，總計檢查 {} 個記錄，發現 {} 個不一致", totalChecked, inconsistentCount);
            
        } catch (Exception e) {
            log.error("數據一致性檢查失敗: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public int cleanupExpiredRecords() {
        // Redis會自動處理TTL過期，這裡主要是清理孤立記錄
        log.info("開始清理孤立的手機號碼記錄...");
        
        int cleanedCount = 0;
        try {
            Set<String> phoneUserKeys = redisTemplate.keys(PhoneRedisKeys.getPhoneUserPattern());
            
            if (phoneUserKeys != null) {
                for (String phoneUserKey : phoneUserKeys) {
                    String hashedPhone = PhoneRedisKeys.extractHashedPhoneFromKey(phoneUserKey);
                    if (hashedPhone != null) {
                        Object userId = redisTemplate.opsForValue().get(phoneUserKey);
                        if (userId != null) {
                            String userPhoneKey = PhoneRedisKeys.getUserPhoneKey(Integer.valueOf(userId.toString()));
                            if (!Boolean.TRUE.equals(redisTemplate.hasKey(userPhoneKey))) {
                                // 發現孤立的phone:user記錄，刪除它
                                if (Boolean.TRUE.equals(redisTemplate.delete(phoneUserKey))) {
                                    cleanedCount++;
                                    log.debug("清理了孤立的手機號碼記錄: {}", phoneUserKey);
                                }
                            }
                        }
                    }
                }
            }
            
            log.info("清理完成，共清理 {} 個孤立記錄", cleanedCount);
            
        } catch (Exception e) {
            log.error("清理孤立記錄失敗: {}", e.getMessage(), e);
        }
        
        return cleanedCount;
    }
    
    @Override
    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            Set<String> userPhoneKeys = redisTemplate.keys(PhoneRedisKeys.getUserPhonePattern());
            Set<String> phoneUserKeys = redisTemplate.keys(PhoneRedisKeys.getPhoneUserPattern());
            
            int userPhoneCount = userPhoneKeys != null ? userPhoneKeys.size() : 0;
            int phoneUserCount = phoneUserKeys != null ? phoneUserKeys.size() : 0;
            
            stats.put("userPhoneRecords", userPhoneCount);
            stats.put("phoneUserRecords", phoneUserCount);
            stats.put("isBalanced", userPhoneCount == phoneUserCount);
            stats.put("timestamp", System.currentTimeMillis());
            
            log.debug("存儲統計: 用戶->手機號碼 {} 個, 手機號碼->用戶 {} 個", userPhoneCount, phoneUserCount);
            
        } catch (Exception e) {
            log.error("獲取存儲統計失敗: {}", e.getMessage(), e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    // ==================== 工具方法 ====================
    
    @Override
    public Set<Integer> getAllStoredUserIds() {
        Set<Integer> userIds = new HashSet<>();
        
        try {
            Set<String> userPhoneKeys = redisTemplate.keys(PhoneRedisKeys.getUserPhonePattern());
            if (userPhoneKeys != null) {
                for (String key : userPhoneKeys) {
                    Integer userId = PhoneRedisKeys.extractUserIdFromKey(key);
                    if (userId != null) {
                        userIds.add(userId);
                    }
                }
            }
            
            log.debug("獲取到 {} 個已存儲手機號碼的用戶ID", userIds.size());
            
        } catch (Exception e) {
            log.error("獲取已存儲用戶ID失敗: {}", e.getMessage(), e);
        }
        
        return userIds;
    }
    
    @Override
    public Set<String> getAllStoredPhonesMasked() {
        Set<String> maskedPhones = new HashSet<>();
        
        try {
            Set<Integer> userIds = getAllStoredUserIds();
            for (Integer userId : userIds) {
                Optional<String> phone = getPhoneByUserId(userId);
                phone.ifPresent(phoneNumber -> maskedPhones.add(encryptionUtil.maskPhone(phoneNumber)));
            }
            
            log.debug("獲取到 {} 個已存儲的手機號碼（遮罩）", maskedPhones.size());
            
        } catch (Exception e) {
            log.error("獲取已存儲手機號碼（遮罩）失敗: {}", e.getMessage(), e);
        }
        
        return maskedPhones;
    }
    
    @Override
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        try {
            // 測試Redis連接
            String testKey = "phone:health:test";
            String testValue = "test-value-" + System.currentTimeMillis();
            
            redisTemplate.opsForValue().set(testKey, testValue, 60, TimeUnit.SECONDS);
            Object retrievedValue = redisTemplate.opsForValue().get(testKey);
            boolean redisOk = testValue.equals(retrievedValue);
            redisTemplate.delete(testKey);
            
            healthStatus.put("redisConnection", redisOk);
            
            // 測試加密功能
            String testPhone = "0912345678";
            boolean encryptionOk = encryptionUtil.testEncryption(testPhone);
            healthStatus.put("encryptionFunction", encryptionOk);
            
            // 測試手機號碼驗證
            boolean validationOk = encryptionUtil.isValidTaiwanMobile(testPhone);
            healthStatus.put("phoneValidation", validationOk);
            
            // 整體健康狀態
            boolean overallHealth = redisOk && encryptionOk && validationOk;
            healthStatus.put("overallHealth", overallHealth);
            healthStatus.put("timestamp", System.currentTimeMillis());
            
            log.info("健康檢查完成: Redis={}, 加密={}, 驗證={}, 整體={}", 
                redisOk, encryptionOk, validationOk, overallHealth);
            
        } catch (Exception e) {
            log.error("健康檢查失敗: {}", e.getMessage(), e);
            healthStatus.put("error", e.getMessage());
            healthStatus.put("overallHealth", false);
        }
        
        return healthStatus;
    }
    
    @Override
    public int refreshAllTTL() {
        int refreshedCount = 0;
        
        try {
            // 刷新所有用戶手機號碼鍵的TTL
            Set<String> userPhoneKeys = redisTemplate.keys(PhoneRedisKeys.getUserPhonePattern());
            if (userPhoneKeys != null) {
                for (String key : userPhoneKeys) {
                    if (Boolean.TRUE.equals(redisTemplate.expire(key, PhoneRedisKeys.DEFAULT_TTL, TimeUnit.SECONDS))) {
                        refreshedCount++;
                    }
                }
            }
            
            // 刷新所有手機號碼用戶鍵的TTL
            Set<String> phoneUserKeys = redisTemplate.keys(PhoneRedisKeys.getPhoneUserPattern());
            if (phoneUserKeys != null) {
                for (String key : phoneUserKeys) {
                    if (Boolean.TRUE.equals(redisTemplate.expire(key, PhoneRedisKeys.DEFAULT_TTL, TimeUnit.SECONDS))) {
                        refreshedCount++;
                    }
                }
            }
            
            log.info("刷新TTL完成，共刷新 {} 個鍵", refreshedCount);
            
        } catch (Exception e) {
            log.error("刷新TTL失敗: {}", e.getMessage(), e);
        }
        
        return refreshedCount;
    }
} 