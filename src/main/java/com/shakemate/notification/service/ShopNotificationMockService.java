package com.shakemate.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商店模擬通知服務
 * 模擬商店模塊的各種通知場景，僅用於集成測試與示例，不影響真實業務邏輯
 * @author ShakeMate團隊
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopNotificationMockService {

    private final NotificationDispatchService dispatchService;

    /** 商品上架通知 */
    public void notifyProductListed(Integer userId, Map<String, Object> productInfo) {
        dispatchService.sendNotificationWithCategory(userId, "新商品上架通知", "您關注的商品已上架: " + productInfo.get("name"), "IN_APP", "促銷活動");
        log.info("[模擬] 已通知用戶{}: 商品上架 - {}", userId, productInfo);
    }

    /** 庫存不足警告 */
    public void notifyLowStock(Integer sellerId, Map<String, Object> productInfo) {
        dispatchService.sendNotificationWithCategory(sellerId, "庫存不足警告", "您的商品『" + productInfo.get("name") + "』庫存不足，請及時補貨。", "IN_APP", "系統通知");
        log.info("[模擬] 已通知賣家{}: 庫存不足 - {}", sellerId, productInfo);
    }

    /** 價格異動通知 */
    public void notifyPriceChange(Integer userId, Map<String, Object> productInfo, String oldPrice, String newPrice) {
        dispatchService.sendNotificationWithCategory(userId, "商品價格異動", "您關注的商品『" + productInfo.get("name") + "』價格由" + oldPrice + "元調整為" + newPrice + "元。", "IN_APP", "促銷活動");
        log.info("[模擬] 已通知用戶{}: 價格異動 - {} - {} -> {}", userId, productInfo, oldPrice, newPrice);
    }

    /** 商品評價提醒 */
    public void notifyProductReviewReminder(Integer userId, Map<String, Object> orderInfo) {
        log.info("[模擬] 商品評價提醒: 用戶{} - 訂單{}", userId, orderInfo);
        // 實際實現可以在這裡添加評價提醒邏輯
    }

    /** 商品推薦通知 */
    public void notifyProductRecommendation(Integer userId, List<Map<String, Object>> recommendedProducts) {
        for (Map<String, Object> product : recommendedProducts) {
            log.info("[模擬] 商品推薦: 用戶{} - 推薦商品{}", userId, product);
            // 實際實現可以在這裡添加推薦邏輯
        }
    }

    /** 商店運營通知 */
    public void notifyStoreOperation(Integer userId, String operation, String detail) {
        dispatchService.sendNotificationWithCategory(userId, "商店運營通知", operation + ": " + detail, "IN_APP", "系統通知");
        log.info("[模擬] 已通知用戶{}: 商店運營 - {} - {}", userId, operation, detail);
    }

    /** 關注者列表獲取（模擬） */
    public List<Integer> getProductFollowers(Integer productId) {
        // 模擬返回一組用戶ID
        return List.of(2001, 2002, 2003);
    }

    /** 庫存監控定時任務（模擬） */
    public void scheduledStockMonitor() {
        log.info("[模擬] 執行庫存監控定時任務");
        // 假設檢查到商品ID=101庫存不足
        notifyLowStock(1001, Map.of("name", "ShakeMate智能水壺"));
    }

    /** 價格變動檢測機制（模擬） */
    public void scheduledPriceChangeMonitor() {
        log.info("[模擬] 執行價格變動檢測定時任務");
        // 假設檢查到商品ID=102價格變動
        notifyPriceChange(2001, Map.of("name", "ShakeMate運動背包"), "1200", "999");
    }
} 