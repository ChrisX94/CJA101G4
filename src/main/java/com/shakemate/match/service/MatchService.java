package com.shakemate.match.service;

import com.shakemate.chatroom.repository.ChatRoomRepository;
import com.shakemate.chatroom.vo.ChatRoomVO;
import com.shakemate.match.repository.MatchRepository;
import com.shakemate.match.vo.UserMatchVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class MatchService {
	
	@Autowired
    private MatchRepository matchRepository;
	
	@Autowired
    private ChatRoomRepository chatRoomRepository;

    public boolean hasUserActed(int userId, int targetId) {
        return matchRepository.existsByActionUserIdAndTargetUserId(userId, targetId);
    }

    public void insertLike(int userId, int targetId) throws SQLException {
    	matchRepository.insertLikeIfNotExists(userId, targetId);
    }

    public void insertDislike(int userId, int targetId) throws SQLException {
    	matchRepository.insertDislikeIfNotExists(userId, targetId);
    }

    public boolean hasLikedBack(int userId, int targetId) throws SQLException {
    	return matchRepository.existsByActionUserIdAndTargetUserIdAndActionType(targetId, userId, 0);
        // 注意順序：targetId 是「對方」的 id，userId 是「我」的 id
    }
    
    // 若已有配對，回傳 matchId；否則插入並回傳新 matchId
    public int insertMatchRecord(int userId, int targetId) {
        // 1. 先查詢是否已配對（action_type = 2）
        UserMatchVO existing = matchRepository.findByActionUserIdAndTargetUserIdAndActionType(userId, targetId, 2);
        if (existing != null) {
            return existing.getMatchId(); // 已有資料，直接回傳 match_id
        }

        // 2️. 建立新的配對紀錄
        UserMatchVO newMatch = new UserMatchVO();
        newMatch.setActionUserId(userId);
        newMatch.setTargetUserId(targetId);
        newMatch.setActionType(2); // 2 = matched
        newMatch.setActionTime(LocalDateTime.now());

        UserMatchVO saved = matchRepository.save(newMatch); // 儲存後取得 match_id
        return saved.getMatchId();
    }


    public int createChatRoom(int userId, int targetId, int matchId) {
        int minId = Math.min(userId, targetId);
        int maxId = Math.max(userId, targetId);

        ChatRoomVO room = new ChatRoomVO();
        room.setUser1Id(minId);
        room.setUser2Id(maxId);
        room.setMatchId(matchId);


        ChatRoomVO saved = chatRoomRepository.save(room);
        return saved.getRoomId(); // 這邊就等於原本 getGeneratedKeys 的結果
    }
}
