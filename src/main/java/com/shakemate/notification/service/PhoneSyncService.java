package com.shakemate.notification.service;

import java.util.List;
import java.util.Map;

/**
 * 手機號碼同步服務接口
 * 
 * 負責管理手機號碼數據的同步與一致性保證：
 * 1. 從外部數據源同步手機號碼
 * 2. 用戶註冊/更新時的手機號碼處理
 * 3. 數據一致性檢查與修復
 * 4. 定期同步與清理任務
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
public interface PhoneSyncService {

    // ==================== 同步操作 ====================
    
    /**
     * 從外部數據源批量同步用戶手機號碼
     * 可從用戶註冊表單、第三方API等來源同步
     * 
     * @param userPhoneMap 用戶ID與手機號碼的映射
     * @return 同步結果統計
     */
    Map<String, Object> batchSyncUserPhones(Map<Integer, String> userPhoneMap);
    
    /**
     * 從CSV文件同步用戶手機號碼
     * 
     * @param csvFilePath CSV文件路徑
     * @return 同步結果統計
     */
    Map<String, Object> syncFromCsvFile(String csvFilePath);
    
    /**
     * 同步單個用戶的手機號碼
     * 
     * @param userId 用戶ID
     * @param phoneNumber 手機號碼
     * @param source 數據來源（如：USER_REGISTER, ADMIN_IMPORT, API_UPDATE等）
     * @return 同步是否成功
     */
    boolean syncSingleUserPhone(Integer userId, String phoneNumber, String source);
    
    /**
     * 從用戶表中檢查並同步遺漏的手機號碼
     * 檢查是否有用戶在其他表中有手機號碼但Redis中沒有
     * 
     * @return 同步結果統計
     */
    Map<String, Object> syncMissingPhones();
    
    // ==================== 一致性檢查與修復 ====================
    
    /**
     * 執行完整的數據一致性檢查
     * 檢查雙向映射、數據完整性、格式正確性等
     * 
     * @return 檢查結果報告
     */
    Map<String, Object> performConsistencyCheck();
    
    /**
     * 修復發現的數據不一致問題
     * 
     * @param autoFix 是否自動修復（true為自動修復，false為僅報告）
     * @return 修復結果統計
     */
    Map<String, Object> repairDataInconsistencies(boolean autoFix);
    
    /**
     * 重建索引
     * 重建所有的雙向映射索引
     * 
     * @return 重建結果統計
     */
    Map<String, Object> rebuildAllIndexes();
    
    /**
     * 驗證所有手機號碼格式
     * 檢查存儲的手機號碼是否符合台灣手機號碼格式
     * 
     * @return 驗證結果統計
     */
    Map<String, Object> validateAllPhoneFormats();
    
    // ==================== 定期維護任務 ====================
    
    /**
     * 執行日常維護任務
     * 包括數據一致性檢查、過期記錄清理、統計更新等
     * 
     * @return 維護任務執行結果
     */
    Map<String, Object> performDailyMaintenance();
    
    /**
     * 執行週期性備份
     * 將重要的手機號碼映射關係備份到指定存儲
     * 
     * @param backupLocation 備份位置標識
     * @return 備份結果統計
     */
    Map<String, Object> performPeriodicBackup(String backupLocation);
    
    /**
     * 從備份恢復數據
     * 
     * @param backupLocation 備份位置標識
     * @param restoreMode 恢復模式（FULL_RESTORE, INCREMENTAL_RESTORE等）
     * @return 恢復結果統計
     */
    Map<String, Object> restoreFromBackup(String backupLocation, String restoreMode);
    
    // ==================== 監控與統計 ====================
    
    /**
     * 獲取同步統計報告
     * 
     * @param reportType 報告類型（DAILY, WEEKLY, MONTHLY等）
     * @return 統計報告
     */
    Map<String, Object> getSyncStatistics(String reportType);
    
    /**
     * 獲取數據品質報告
     * 檢查數據的完整性、準確性、一致性等指標
     * 
     * @return 數據品質報告
     */
    Map<String, Object> getDataQualityReport();
    
    /**
     * 獲取異常記錄列表
     * 列出同步過程中發生的異常情況
     * 
     * @param maxRecords 最大記錄數
     * @return 異常記錄列表
     */
    List<Map<String, Object>> getAnomalyRecords(int maxRecords);
    
    // ==================== 用戶生命週期管理 ====================
    
    /**
     * 處理用戶註冊時的手機號碼綁定
     * 
     * @param userId 新註冊的用戶ID
     * @param phoneNumber 用戶提供的手機號碼
     * @return 綁定結果
     */
    Map<String, Object> handleUserRegistration(Integer userId, String phoneNumber);
    
    /**
     * 處理用戶更新手機號碼
     * 
     * @param userId 用戶ID
     * @param oldPhoneNumber 舊手機號碼
     * @param newPhoneNumber 新手機號碼
     * @return 更新結果
     */
    Map<String, Object> handlePhoneNumberUpdate(Integer userId, String oldPhoneNumber, String newPhoneNumber);
    
    /**
     * 處理用戶註銷時的數據清理
     * 
     * @param userId 要註銷的用戶ID
     * @return 清理結果
     */
    Map<String, Object> handleUserDeregistration(Integer userId);
    
    /**
     * 處理用戶手機號碼驗證
     * 驗證用戶提供的手機號碼是否與存儲的一致
     * 
     * @param userId 用戶ID
     * @param phoneNumber 待驗證的手機號碼
     * @return 驗證結果
     */
    Map<String, Object> verifyUserPhone(Integer userId, String phoneNumber);
    
    // ==================== 工具方法 ====================
    
    /**
     * 強制同步指定用戶列表的手機號碼
     * 
     * @param userIds 要同步的用戶ID列表
     * @param forceOverwrite 是否強制覆蓋已存在的記錄
     * @return 同步結果統計
     */
    Map<String, Object> forceSyncUsers(List<Integer> userIds, boolean forceOverwrite);
    
    /**
     * 測試同步服務的功能
     * 
     * @return 測試結果
     */
    Map<String, Object> performFunctionTest();
    
    /**
     * 獲取同步服務的健康狀態
     * 
     * @return 健康狀態報告
     */
    Map<String, Object> getServiceHealthStatus();
} 