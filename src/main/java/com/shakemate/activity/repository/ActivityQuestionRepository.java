package com.shakemate.activity.repository;

import com.shakemate.activity.entity.ActivityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityQuestionRepository  extends JpaRepository<ActivityQuestion, Integer> {

    @Query("""
    SELECT q FROM ActivityQuestion q
    WHERE q.activity.activityId = :activityId
    ORDER BY q.questionOrder ASC
""")
    List<ActivityQuestion> findAllQuestionsByActivity(@Param("activityId") Integer activityId);


}
