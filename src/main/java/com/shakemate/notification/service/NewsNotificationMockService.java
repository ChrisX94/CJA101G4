package com.shakemate.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 新聞模擬通知服務
 * 模擬新聞模塊的各種通知場景，僅用於集成測試與示例，不影響真實業務邏輯
 * @author ShakeMate團隊
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsNotificationMockService {

    private final NotificationDispatchService dispatchService;

    /** 新聞發布廣播通知 */
    public void broadcastNewsPublished(List<Integer> userIds, Map<String, Object> newsInfo) {
        for (Integer userId : userIds) {
            dispatchService.sendNotification(userId, "新聞發布通知", "最新新聞發布：《" + newsInfo.get("title") + "》。", "IN_APP");
            log.info("[模擬] 已通知用戶{}: 新聞發布 - {}", userId, newsInfo);
        }
    }

    /** 熱門話題推薦通知 */
    public void notifyTrendingTopic(Integer userId, String topic) {
        dispatchService.sendNotification(userId, "熱門話題推薦", "您可能感興趣的熱門話題：「" + topic + "」已上榜，快來看看！", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 熱門話題推薦 - {}", userId, topic);
    }

    /** 分類新聞訂閱通知 */
    public void notifyCategorySubscription(Integer userId, String category, String newsTitle) {
        dispatchService.sendNotification(userId, "分類新聞訂閱", "您訂閱的「" + category + "」有新新聞：《" + newsTitle + "》。", "IN_APP");
        log.info("[模擬] 已通知用戶{}: 分類新聞訂閱 - {} - {}", userId, category, newsTitle);
    }

    /** 用戶興趣標籤獲取（模擬） */
    public List<String> getUserInterestTags(Integer userId) {
        // 模擬返回一組興趣標籤
        return List.of("科技", "運動", "生活");
    }

    /** 新聞推薦演算法集成（模擬） */
    public void recommendNewsToUser(Integer userId, List<Map<String, Object>> recommendedNews) {
        for (Map<String, Object> news : recommendedNews) {
            dispatchService.sendNotification(userId, "新聞推薦", "根據您的興趣，推薦新聞：《" + news.get("title") + "》。", "IN_APP");
            log.info("[模擬] 已推薦新聞給用戶{}: {}", userId, news);
        }
    }

    /** 新聞發布自動通知（模擬） */
    public void scheduledNewsAutoNotify() {
        log.info("[模擬] 執行新聞發布自動通知定時任務");
        // 假設有一則新新聞發布，廣播給所有用戶
        broadcastNewsPublished(List.of(2001, 2002, 2003), Map.of("title", "ShakeMate平台全新改版上線"));
    }
} 