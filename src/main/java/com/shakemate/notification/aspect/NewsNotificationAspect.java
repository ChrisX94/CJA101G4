package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.news.dto.NewsResponse;
import com.shakemate.news.model.News;
import com.shakemate.news.repository.NewsRepository;
import com.shakemate.user.dao.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新聞通知AOP切面
 * 
 * 透過AOP機制攔截新聞相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 新聞發布（NEWS_PUBLISHED）
 * - 新聞更新（NEWS_UPDATED）
 * - 熱門新聞推薦（NEWS_TRENDING）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NewsNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;
    private final NewsRepository newsRepository;

    /**
     * 定義切點：攔截新聞創建或更新操作
     */
    @Pointcut("execution(* com.shakemate.news.service.NewsService.saveOrUpdate(..))")
    public void newsSavePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截新聞刪除操作
     */
    @Pointcut("execution(* com.shakemate.news.service.NewsService.deleteById(..))")
    public void newsDeletePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截獲取最新新聞操作（用於推薦通知）
     */
    @Pointcut("execution(* com.shakemate.news.service.NewsService.getLatestNews(..))")
    public void latestNewsPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理新聞創建/更新後的通知
     * 
     * 當管理員發布或更新新聞後，廣播通知給用戶
     * 
     * @param result 新聞響應DTO
     * @param dto 新聞DTO
     * @param admin 管理員
     */
    @AfterReturning(pointcut = "newsSavePointcut() && args(dto, admin)", returning = "result")
    public void handleNewsSaved(Object result, Object dto, Object admin) {
        try {
            if (result instanceof NewsResponse) {
                NewsResponse newsResponse = (NewsResponse) result;
                
                log.info("AOP攔截到新聞保存，新聞ID: {}, 標題: {}", 
                        newsResponse.getNewsId(), newsResponse.getTitle());

                // 查詢新聞詳細數據
                News news = newsRepository.findById(newsResponse.getNewsId()).orElse(null);
                if (news == null) {
                    log.warn("無法找到新聞數據: newsId={}", newsResponse.getNewsId());
                    return;
                }

                // 判斷是新建還是更新
                boolean isNewNews = news.getPublishTime() != null && 
                    (System.currentTimeMillis() - news.getPublishTime().getTime()) < 60000; // 1分鐘內視為新建

                String templateCode = isNewNews ? "NEWS_PUBLISHED" : "NEWS_UPDATED";
                String actionType = isNewNews ? "發布" : "更新";

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("news_id", newsResponse.getNewsId());
                templateVariables.put("news_title", newsResponse.getTitle());
                templateVariables.put("news_content", truncateContent(newsResponse.getContent(), 100));
                templateVariables.put("category_name", newsResponse.getCategoryName());
                templateVariables.put("admin_name", "管理員"); // NewsResponse沒有adminName，使用預設值
                templateVariables.put("publish_time", newsResponse.getPublishTime().toString());
                templateVariables.put("action_type", actionType);

                // 根據新聞類別設定目標用戶條件
                Map<String, Object> targetCriteria = new HashMap<>();
                targetCriteria.put("news_category", newsResponse.getCategoryName());
                targetCriteria.put("notification_preference", "NEWS");

                // 廣播新聞通知給所有用戶
                notificationService.broadcastTemplateNotification(
                    templateCode,
                    templateVariables,
                    targetCriteria
                ).exceptionally(throwable -> {
                    log.error("發送新聞{}通知失敗，新聞ID: {}", actionType, newsResponse.getNewsId(), throwable);
                    return false;
                });

                log.info("新聞{}通知已廣播，新聞ID: {}", actionType, newsResponse.getNewsId());
            }
            
        } catch (Exception e) {
            log.error("處理新聞保存通知時發生錯誤", e);
        }
    }

    /**
     * 處理新聞刪除後的通知
     * 
     * 當管理員刪除新聞後，記錄操作日誌
     * 
     * @param newsId 新聞ID
     */
    @AfterReturning("newsDeletePointcut() && args(newsId)")
    public void handleNewsDeleted(Integer newsId) {
        try {
            log.info("AOP攔截到新聞刪除，新聞ID: {}", newsId);

            // 新聞刪除通常不需要通知普通用戶
            // 這裡主要用於記錄管理操作日誌
            // 可以選擇性地通知管理員或相關人員

            // 如果需要通知，可以發送給管理員群組
            // Map<String, Object> templateVariables = new HashMap<>();
            // templateVariables.put("news_id", newsId);
            // templateVariables.put("deleted_time", LocalDateTime.now().toString());
            
            log.info("新聞刪除操作已記錄，新聞ID: {}", newsId);
            
        } catch (Exception e) {
            log.error("處理新聞刪除通知時發生錯誤，新聞ID: {}", newsId, e);
        }
    }

    /**
     * 處理最新新聞推薦
     * 
     * 當系統獲取最新新聞時，可以觸發推薦通知
     * （此功能可選，避免過度通知）
     * 
     * @param result 最新新聞列表
     */
    @AfterReturning(pointcut = "latestNewsPointcut()", returning = "result")
    public void handleLatestNewsRetrieved(Object result) {
        try {
            if (result instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<NewsResponse> newsList = (List<NewsResponse>) result;
                
                if (!newsList.isEmpty()) {
                    log.info("AOP攔截到最新新聞查詢，返回{}條新聞", newsList.size());

                    // 這裡可以實現新聞推薦邏輯
                    // 例如：每日熱門新聞推送、個人化新聞推薦等
                    // 為避免過度通知，暫時只記錄日誌
                    
                    // 示例：如果有特別重要的新聞，可以推送
                    for (NewsResponse news : newsList) {
                        if (isImportantNews(news)) {
                            sendImportantNewsNotification(news);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("處理最新新聞推薦時發生錯誤", e);
        }
    }

    /**
     * 判斷是否為重要新聞
     * 
     * @param news 新聞響應
     * @return 是否為重要新聞
     */
    private boolean isImportantNews(NewsResponse news) {
        // 可以根據新聞類別、關鍵字等判斷重要性
        String title = news.getTitle().toLowerCase();
        String category = news.getCategoryName();
        
        // 示例：系統公告、重大更新等視為重要新聞
        return "系統公告".equals(category) || 
               title.contains("重要") || 
               title.contains("緊急") ||
               title.contains("維護") ||
               title.contains("更新");
    }

    /**
     * 發送重要新聞通知
     * 
     * @param news 重要新聞
     */
    private void sendImportantNewsNotification(NewsResponse news) {
        try {
            log.info("發送重要新聞通知，新聞ID: {}", news.getNewsId());

            // 準備重要新聞通知模板變數
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("news_id", news.getNewsId());
            templateVariables.put("news_title", news.getTitle());
            templateVariables.put("news_content", truncateContent(news.getContent(), 150));
            templateVariables.put("category_name", news.getCategoryName());
            templateVariables.put("publish_time", news.getPublishTime().toString());

            // 設定目標用戶條件（所有用戶）
            Map<String, Object> targetCriteria = new HashMap<>();
            targetCriteria.put("important_news", true);

            // 廣播重要新聞通知
            notificationService.broadcastTemplateNotification(
                "NEWS_IMPORTANT",
                templateVariables,
                targetCriteria
            ).exceptionally(throwable -> {
                log.error("發送重要新聞通知失敗，新聞ID: {}", news.getNewsId(), throwable);
                return false;
            });

            log.info("重要新聞通知已廣播，新聞ID: {}", news.getNewsId());
            
        } catch (Exception e) {
            log.error("發送重要新聞通知時發生錯誤，新聞ID: {}", news.getNewsId(), e);
        }
    }

    /**
     * 截斷內容的輔助方法
     * 
     * @param content 原始內容
     * @param maxLength 最大長度
     * @return 截斷後的內容
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
} 