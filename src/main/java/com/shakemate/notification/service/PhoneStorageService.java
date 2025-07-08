package com.shakemate.notification.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 手機號碼存儲服務接口
 * 
 * 負責管理用戶手機號碼在Redis中的存儲，提供：
 * 1. 用戶ID與手機號碼的雙向映射
 * 2. 加密存儲與安全查詢
 * 3. 批量操作與同步功能
 * 4. 數據一致性保證
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
public interface PhoneStorageService {

    // ==================== 基本CRUD操作 ====================
    
    /**
     * 存儲用戶手機號碼
     * 
     * @param userId 用戶ID
     * @param phoneNumber 手機號碼（明文）
     * @return 存儲成功時返回true
     * @throws IllegalArgumentException 當參數無效時拋出
     * @throws RuntimeException 當存儲失敗時拋出
     */
    boolean storeUserPhone(Integer userId, String phoneNumber);
    
    /**
     * 根據用戶ID查詢手機號碼
     * 
     * @param userId 用戶ID
     * @return 手機號碼（明文），不存在時返回空Optional
     */
    Optional<String> getPhoneByUserId(Integer userId);
    
    /**
     * 根據手機號碼查詢用戶ID
     * 
     * @param phoneNumber 手機號碼（明文）
     * @return 用戶ID，不存在時返回空Optional
     */
    Optional<Integer> getUserIdByPhone(String phoneNumber);
    
    /**
     * 更新用戶手機號碼
     * 
     * @param userId 用戶ID
     * @param newPhoneNumber 新手機號碼（明文）
     * @return 更新成功時返回true
     */
    boolean updateUserPhone(Integer userId, String newPhoneNumber);
    
    /**
     * 刪除用戶手機號碼
     * 
     * @param userId 用戶ID
     * @return 刪除成功時返回true
     */
    boolean deleteUserPhone(Integer userId);
    
    // ==================== 批量操作 ====================
    
    /**
     * 批量存儲用戶手機號碼
     * 
     * @param userPhoneMap 用戶ID與手機號碼的映射
     * @return 成功存儲的數量
     */
    int batchStoreUserPhones(Map<Integer, String> userPhoneMap);
    
    /**
     * 批量查詢用戶手機號碼
     * 
     * @param userIds 用戶ID列表
     * @return 用戶ID與手機號碼的映射（僅包含存在的記錄）
     */
    Map<Integer, String> batchGetPhonesByUserIds(List<Integer> userIds);
    
    /**
     * 批量查詢手機號碼對應的用戶ID
     * 
     * @param phoneNumbers 手機號碼列表
     * @return 手機號碼與用戶ID的映射（僅包含存在的記錄）
     */
    Map<String, Integer> batchGetUserIdsByPhones(List<String> phoneNumbers);
    
    /**
     * 批量刪除用戶手機號碼
     * 
     * @param userIds 用戶ID列表
     * @return 成功刪除的數量
     */
    int batchDeleteUserPhones(List<Integer> userIds);
    
    // ==================== 數據檢查與驗證 ====================
    
    /**
     * 檢查手機號碼是否已被使用
     * 
     * @param phoneNumber 手機號碼（明文）
     * @return 已被使用時返回true
     */
    boolean isPhoneExists(String phoneNumber);
    
    /**
     * 檢查用戶是否已綁定手機號碼
     * 
     * @param userId 用戶ID
     * @return 已綁定時返回true
     */
    boolean isUserPhoneBound(Integer userId);
    
    /**
     * 驗證手機號碼格式是否正確
     * 
     * @param phoneNumber 手機號碼
     * @return 格式正確時返回true
     */
    boolean validatePhoneFormat(String phoneNumber);
    
    // ==================== 同步與維護 ====================
    
    /**
     * 同步數據一致性檢查
     * 檢查用戶ID->手機號碼 和 手機號碼->用戶ID 的雙向映射是否一致
     * 
     * @return 同步結果資訊
     */
    Map<String, Object> syncDataConsistency();
    
    /**
     * 清理過期或無效的手機號碼記錄
     * 
     * @return 清理的記錄數量
     */
    int cleanupExpiredRecords();
    
    /**
     * 獲取存儲統計資訊
     * 
     * @return 統計資訊映射
     */
    Map<String, Object> getStorageStatistics();
    
    // ==================== 工具方法 ====================
    
    /**
     * 獲取所有已存儲的用戶ID
     * 
     * @return 用戶ID集合
     */
    Set<Integer> getAllStoredUserIds();
    
    /**
     * 獲取所有已存儲的手機號碼（遮罩顯示）
     * 
     * @return 遮罩後的手機號碼集合
     */
    Set<String> getAllStoredPhonesMasked();
    
    /**
     * 測試Redis連接與加密功能
     * 
     * @return 測試結果
     */
    Map<String, Object> performHealthCheck();
    
    /**
     * 強制刷新所有相關的Redis鍵的TTL
     * 
     * @return 刷新的鍵數量
     */
    int refreshAllTTL();
} 