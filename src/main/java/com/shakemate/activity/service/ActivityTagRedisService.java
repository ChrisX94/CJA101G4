package com.shakemate.activity.service;

import java.util.List;
import java.util.Set;

public interface ActivityTagRedisService {

    void saveActivityTags(Integer activityId, List<String> tags);
    Set<String> getTagsByActivityId(Integer activityId);
    void deleteTagsByActivityId(Integer activityId);
    void rebuildReverseIndexFromActivityTags();
    Set<String> getActivityIdsByTag(String tag);
    Set<String> searchActivityIdsByTagKeyword(String keyword);
}
