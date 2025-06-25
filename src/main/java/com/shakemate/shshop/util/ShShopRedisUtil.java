package com.shakemate.shshop.util;

import com.shakemate.shshop.dto.ProdAuditResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 工具類
 * 支援操作 String、Object、Map（Hash）、List、Set
 */
@Component
public class ShShopRedisUtil {

    // 操作純文字（String）
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 操作物件（使用 JSON 序列化）
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ================== String ==================

    /**
     * 儲存字串
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 儲存字串並設定過期時間（秒）
     */
    public void set(String key, String value, long timeoutSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 讀取字串
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // ================== Object ==================

    /**
     * 儲存物件（自動轉為 JSON）
     */
    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 儲存物件並設定過期時間（秒）
     */
    public void setObject(String key, Object value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 讀取物件，自動轉型
     */
    public <T> T getObject(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : clazz.cast(obj);
    }

    // ================== Map（Hash） ==================

    /**
     * 儲存整個 Map
     */
    public void setMap(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 取得整個 Map
     */
    public Map<Object, Object> getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 新增或更新 Map 中的單一欄位
     */
    public void putMapItem(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 取得 Map 中的單一欄位
     */
    public Object getMapItem(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 刪除 Map 中的單一欄位
     */
    public void deleteMapItem(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    // ================== List ==================

    /**
     * 從右側加入 List（像佇列的 enqueue）
     */
    public void addToListRight(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 取得整個 List
     */
    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    // ================== Set ==================

    /**
     * 新增 Set（不重複的元素）
     */
    public void addToSet(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 取得 Set 中的所有元素
     */
    public Set<Object> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // ================== Key 操作 ==================

    /**
     * 判斷 Key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 刪除 Key
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 將物件加入 key:resultHistory
     */
    public void addHistoryTime(String key) {
        String historyKey = key + ":history";
        String now = java.time.LocalDateTime.now().toString();
        redisTemplate.opsForList().rightPush(historyKey, now);
    }
    /**
     * 將物件加入 key:resultHistory
     */
    public void addResultToHistory(String key, Object value) {
        String historyKey = key + ":resultHistory";
        redisTemplate.opsForList().rightPush(historyKey, value);
    }

    /**
     * 取得 key:history 中的所有時間紀錄
     */
    public List<Object> getHistoryTime(String key) {
        return redisTemplate.opsForList().range(key + ":history", 0, -1);
    }

    /**
     * 取得 key:resultHistory 中的所有結果紀錄
     */
    public List<Object> getResultHistory(String key) {
        return redisTemplate.opsForList().range(key + ":resultHistory", 0, -1);
    }



    public void saveAuditResult(String key, List<ProdAuditResult> resultList) {
        String timestamp = java.time.LocalDateTime.now().toString();

        // 1. 最新結果
        redisTemplate.opsForValue().set(key + ":latest", resultList);

        // 2. 歷史記錄：以時間為key
        redisTemplate.opsForHash().put(key + ":record", timestamp, resultList);

        // 3. 時間清單
        redisTemplate.opsForList().rightPush(key + ":timeList", timestamp);
    }
}
