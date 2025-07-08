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

    /** 新商品上架通知 */
    public void notifyProductNew(Integer productId, List<Integer> followerUserIds, Map<String, Object> productInfo) {
        for (Integer userId : followerUserIds) {
            dispatchService.sendNotification(userId, "新商品上架通知", "您關注的商品已上架: " + productInfo.get("name"), "IN_APP");
            log.info("[模擬] 已通知用戶{}: 新商品上架 - {}", userId, productInfo);
        }
    }

    /** 庫存不足警告通知 */
    public void notifyProductLowStock(Integer productId, Integer sellerId, Map<String, Object> productInfo) {
        dispatchService.sendNotification(sellerId, "庫存不足警告", "您的商品『" + productInfo.get("name") + "』庫存不足，請及時補貨。", "IN_APP");
        log.info("[模擬] 已通知賣家{}: 商品庫存不足 - {}", sellerId, productInfo);
    }

    /** 商品價格變動通知 */
    public void notifyProductPriceChange(Integer productId, List<Integer> followerUserIds, Map<String, Object> productInfo, Integer oldPrice, Integer newPrice) {
        for (Integer userId : followerUserIds) {
            dispatchService.sendNotification(userId, "商品價格異動", "您關注的商品『" + productInfo.get("name") + "』價格由" + oldPrice + "元調整為" + newPrice + "元。", "IN_APP");
            log.info("[模擬] 已通知用戶{}: 商品價格異動 - {}", userId, productInfo);
        }
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
        notifyProductLowStock(101, 1001, Map.of("name", "ShakeMate智能水壺"));
    }

    /** 價格變動檢測機制（模擬） */
    public void scheduledPriceChangeMonitor() {
        log.info("[模擬] 執行價格變動檢測定時任務");
        // 假設檢查到商品ID=102價格變動
        notifyProductPriceChange(102, getProductFollowers(102), Map.of("name", "ShakeMate運動背包"), 1200, 999);
    }

    /** 完善商店運營通知（模擬） */
    public void notifyShopOperation(Integer userId, String operation, String detail) {
        dispatchService.sendNotification(userId, "商店運營通知", operation + ": " + detail, "IN_APP");
        log.info("[模擬] 已通知用戶{}: 商店運營 - {} {}", userId, operation, detail);
    }
} 