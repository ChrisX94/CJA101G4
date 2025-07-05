package com.shakemate.activity.repository;

import com.shakemate.activity.entity.ActivityAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityAnswerRepository extends JpaRepository<ActivityAnswer, Integer> {

    @Query("""
    SELECT a FROM ActivityAnswer a 
    WHERE a.activity.activityId = :activityId 
      AND a.user.userId = :userId 
    ORDER BY a.activityQuestion.questionId ASC
""")
    List<ActivityAnswer> findAllAnswersByActivityAndUser(
            @Param("activityId") Integer activityId,
            @Param("userId") Integer userId
    );

}
