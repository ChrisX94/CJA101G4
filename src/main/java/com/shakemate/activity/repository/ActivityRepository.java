package com.shakemate.activity.repository;

import com.shakemate.activity.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    // 你可以在這裡加自訂查詢方法，但不需要自己實作 JpaRepository 方法

    // 動態牆
    @Query("""
    SELECT a FROM Activity a
    WHERE :userAge BETWEEN a.minAge AND a.maxAge
      AND (a.genderFilter = 0 OR a.genderFilter = :userGender)
    """)
    Page<Activity> findByUserAgeGender(
            @Param("userAge") int userAge,
            @Param("userGender") int userGender,
            Pageable pageable);

    // 這是新增的，支援 spots 排序
    @Query("""
    SELECT a FROM Activity a
    WHERE :userAge BETWEEN a.minAge AND a.maxAge
      AND (a.genderFilter = 0 OR a.genderFilter = :userGender)
    ORDER BY (a.maxPeople - a.signupCount) ASC, a.createdTime ASC
    """)
    Page<Activity> findByUserAgeGenderOrderBySpotsLeft(
            @Param("userAge") int userAge,
            @Param("userGender") int userGender,
            Pageable pageable);


    // Card
    // 查某使用者為團主的活動（加上分頁）
    @Query("SELECT a FROM Activity a WHERE a.user.userId = :userId")
    Page<Activity> findByUserId(@Param("userId") Integer userId, Pageable pageable);


    // 查某使用者為團員的活動（加上分頁）
    @Query("""
    SELECT a FROM ActivityParticipant ap
    JOIN ap.activity a
    WHERE ap.id.participantId = :userId
    """)
    Page<Activity> findAllMemberActivities(@Param("userId") Integer userId, Pageable pageable);


    // 查某使用者追蹤的活動（加上分頁）
    @Query("""
        SELECT a FROM ActivityTracking at
        JOIN at.activity a
        WHERE at.id.userId = :userId
          AND at.trackingState = 0
         
    """)
    Page<Activity> findTrackedOngoingActivities(@Param("userId") Integer userId, Pageable pageable);





}