package com.shakemate.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shakemate.vo.UserMatchVO;

@Repository
public interface MatchDAO2 extends JpaRepository<UserMatchVO, Integer> {
	// 檢查 userId 是否已經對 targetId 執行過任何配對動作（不分 like / dislike / matched）
	boolean existsByActionUserIdAndTargetUserId(int actionUserId, int targetUserId);
	
	
	// 若尚未 like 過，新增一筆 like 配對紀錄（避免重複 insert）
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO user_matches (action_user_id, target_user_id, action_type, action_time) "
			+ "SELECT :userId, :targetId, 0, NOW() FROM DUAL " + "WHERE NOT EXISTS ("
			+ "    SELECT 1 FROM user_matches "
			+ "    WHERE action_user_id = :userId AND target_user_id = :targetId AND action_type = 0"
			+ ")", nativeQuery = true)
	void insertLikeIfNotExists(int userId, int targetId);
	
	// 若尚未 dislike 過，新增一筆 dislike 配對紀錄（避免重複 insert）
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO user_matches (action_user_id, target_user_id, action_type, action_time) "
			+ "SELECT :userId, :targetId, 1, NOW() FROM DUAL " + "WHERE NOT EXISTS ("
			+ "    SELECT 1 FROM user_matches "
			+ "    WHERE action_user_id = :userId AND target_user_id = :targetId AND action_type = 1"
			+ ")", nativeQuery = true)
	void insertDislikeIfNotExists(int userId, int targetId);
	
	// 檢查是否存在特定配對類型的紀錄（例如：是否有 matched 記錄）
	boolean existsByActionUserIdAndTargetUserIdAndActionType(int actionUserId, int targetUserId, int actionType);

	// 查詢是否已有配對紀錄
    UserMatchVO findByActionUserIdAndTargetUserIdAndActionType(int userId, int targetId, int actionType);
}
