package com.shakemate.activity.service.impl;

import com.shakemate.activity.service.ActivityTagRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActivityTagRedisServiceImpl implements ActivityTagRedisService {

    private final StringRedisTemplate redisTemplate;

    // 儲存活動的所有標籤，並建立反向索引
    @Transactional
    public void saveActivityTags(Integer activityId, List<String> tags) {
        String activityKey = "activity:tags:" + activityId;

        // 儲存正向索引（活動 ➜ 標籤）
        redisTemplate.opsForSet().add(activityKey, tags.toArray(new String[0]));

        // 建立反向索引（標籤 ➜ 活動）
        for (String tag : tags) {
            String reverseKey = "tag:activities:" + tag;
            redisTemplate.opsForSet().add(reverseKey, String.valueOf(activityId));
        }
    }

    // 讀取某活動的所有標籤
    public Set<String> getTagsByActivityId(Integer activityId) {
        String key = "activity:tags:" + activityId;
        return redisTemplate.opsForSet().members(key);
    }

    // 刪除整個活動的標籤（包含反向索引）
    public void deleteTagsByActivityId(Integer activityId) {
        String activityKey = "activity:tags:" + activityId;

        // 先讀出要刪除的標籤
        Set<String> tags = redisTemplate.opsForSet().members(activityKey);
        if (tags != null) {
            for (String tag : tags) {
                String reverseKey = "tag:activities:" + tag;
                redisTemplate.opsForSet().remove(reverseKey, String.valueOf(activityId));
            }
        }

        // 刪除正向索引
        redisTemplate.delete(activityKey);
    }

    // 可選：重新建立所有反向索引（一次性補資料）
    public void rebuildReverseIndexFromActivityTags() {
        Set<String> keys = redisTemplate.keys("activity:tags:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String activityId = key.replace("activity:tags:", "");
            Set<String> tags = redisTemplate.opsForSet().members(key);
            if (tags == null) continue;

            for (String tag : tags) {
                String reverseKey = "tag:activities:" + tag;
                redisTemplate.opsForSet().add(reverseKey, activityId);
            }
        }
    }

    // 讀取某標籤對應的所有活動 ID（反向查詢）
    public Set<String> getActivityIdsByTag(String tag) {
        String reverseKey = "tag:activities:" + tag;
        return redisTemplate.opsForSet().members(reverseKey);
    }

    // 模糊搜尋標籤對應的活動 ID
    public Set<String> searchActivityIdsByTagKeyword(String keyword) {
        String pattern = "tag:activities:*" + keyword + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<>();
        for (String key : keys) {
            Set<String> activityIds = redisTemplate.opsForSet().members(key);
            if (activityIds != null) {
                result.addAll(activityIds);
            }
        }

        return result;
    }


}
