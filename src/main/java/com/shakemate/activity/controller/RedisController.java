package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.service.impl.ActivityTagRedisServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final ActivityTagRedisServiceImpl redisService;
    private final StringRedisTemplate redisTemplate;

    // 新增標籤
    @PostMapping("/save-tag/{activityId}")
    public ApiResponse<String> saveTags(@PathVariable Integer activityId, @RequestBody List<String> tags) {
        redisService.saveActivityTags(activityId, tags);
        return ApiResponse.success("已儲存標籤！");
    }

    // 查詢標籤
    @GetMapping("/tags")
    public Set<String> getTags(@RequestParam Integer activityId) {
        return redisService.getTagsByActivityId(activityId);
    }

    // 刪除標籤
    @DeleteMapping("/tags/{activityId}")
    public String deleteTags(@PathVariable Integer activityId) {
        redisService.deleteTagsByActivityId(activityId);
        return "已刪除標籤";
    }

    @GetMapping("/rebuild-index")
    public String rebuildIndex() {
        redisService.rebuildReverseIndexFromActivityTags();
        return "已重建反向索引";
    }

    // 查詢關鍵字對應的活動ID清單
    @GetMapping("/search-activities")
    public Set<String> searchActivitiesByKeyword(@RequestParam String keyword) {
        return redisService.searchActivityIdsByTagKeyword(keyword);
    }

}
