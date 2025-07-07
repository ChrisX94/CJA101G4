package com.shakemate.activity.repository;

import com.shakemate.activity.entity.ActivityParticipant;
import com.shakemate.activity.entity.id.ActivityParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, ActivityParticipantId> {

    // 申請者與已加入者
    // 申請中（parStatus = 0）
    @Query("""
        SELECT ap FROM ActivityParticipant ap
        JOIN FETCH ap.participant p
        WHERE ap.id.activityId = :activityId AND ap.parStatus = 0
    """)
    Page<ActivityParticipant> findAllApplicantsByActivityId(
            @Param("activityId") Integer activityId,
            Pageable pageable
    );

    // 已加入（parStatus = 2）
    @Query("""
        SELECT ap FROM ActivityParticipant ap
        JOIN FETCH ap.participant p
        WHERE ap.id.activityId = :activityId AND ap.parStatus = 2
    """)
    Page<ActivityParticipant> findAllAcceptedByActivityId(
            @Param("activityId") Integer activityId,
            Pageable pageable
    );

    // 活動評語
    @Query("""
    SELECT ap FROM ActivityParticipant ap
    JOIN FETCH ap.participant p
    WHERE ap.id.activityId = :activityId
      AND ap.reviewContent IS NOT NULL
      AND ap.reviewContent <> ''
""")
    Page<ActivityParticipant> findAllReviewsByActivityId(
            @Param("activityId") Integer activityId,
            Pageable pageable
    );

    // 計算平均星星
    @Query("""
    SELECT AVG(ap.rating) FROM ActivityParticipant ap
    WHERE ap.id.activityId = :activityId
      AND ap.rating IS NOT NULL
    """)
    Double findAverageRatingByActivityId(@Param("activityId") Integer activityId);

    List<ActivityParticipant> findByParticipantUserIdAndActivityActivityIdIn(Integer userId, List<Integer> activityIds);


}
