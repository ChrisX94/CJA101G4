package com.shakemate.user.dao;

import com.shakemate.user.model.UserMatchVO;
import com.shakemate.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMatchRepository extends JpaRepository<UserMatchVO, Integer> {

    @Query(value = """
                SELECT 
                    CASE 
                        WHEN action_user_id = :userId THEN target_user_id
                        WHEN target_user_id = :userId THEN action_user_id
                    END AS friendId
                FROM user_matches
                WHERE action_type = 2
                  AND (action_user_id = :userId OR target_user_id = :userId)
            """, nativeQuery = true)
    List<Integer> findFriendIdsByUserId(@Param("userId") Integer userId);

}

