package com.shakemate.activity.repository;

import com.shakemate.activity.entity.ActivityTracking;
import com.shakemate.activity.entity.id.ActivityTrackingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityTrackingRepository extends JpaRepository<ActivityTracking, ActivityTrackingId> {

    // 查某個會員目前追蹤的所有活動
    List<ActivityTracking> findByIdUserIdAndTrackingState(Integer userId, Byte trackingState);

    // 查某活動被哪些人追蹤
    List<ActivityTracking> findByIdActivityIdAndTrackingState(Integer activityId, Byte trackingState);

    // 查某個會員是否正在追蹤某個活動
    boolean existsByIdUserIdAndIdActivityIdAndTrackingState(Integer userId, Integer activityId, Byte trackingState);

}
