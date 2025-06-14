//package com.shakemate.model;
//
//import java.sql.SQLException;
//
//public interface MatchDAO {
//
//    // 檢查是否已對某位使用者按過 like 或 dislike
//    boolean hasUserActed(int userId, int targetId) throws SQLException;
//
//    // 對某位使用者送出 like 行為
//    void insertLike(int userId, int targetId) throws SQLException;
//
//    // 對某位使用者送出 dislike 行為
//    void insertDislike(int userId, int targetId) throws SQLException;
//
//    // 檢查對方是否也 like 過我
//    boolean hasLikedBack(int userId, int targetId) throws SQLException;
//
//    // 雙方成功配對，插入配對成功紀錄（action_type = 2），回傳 match_id
//    int insertMatchRecord(int userId, int targetId) throws SQLException;
//
//    // 根據 matchId 建立一間聊天室，並回傳 room_id
//    int createChatRoom(int userId, int targetId, int matchId) throws SQLException;
//}
