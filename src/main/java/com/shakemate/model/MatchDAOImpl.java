//package com.shakemate.model;
//
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.sql.*;
//
//@Repository
//public class MatchDAOImpl implements MatchDAO {
//
//    private final DataSource dataSource;
//
//    public MatchDAOImpl(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public boolean hasUserActed(int userId, int targetId) throws SQLException {
//        String sql = "SELECT 1 FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ?";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, userId);
//            pstmt.setInt(2, targetId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next();
//            }
//        }
//    }
//
//    @Override
//    public void insertLike(int userId, int targetId) throws SQLException {
//        String sql = "INSERT IGNORE INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 0, NOW())";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, userId);
//            pstmt.setInt(2, targetId);
//            pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public void insertDislike(int userId, int targetId) throws SQLException {
//        String sql = "INSERT IGNORE INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 1, NOW())";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, userId);
//            pstmt.setInt(2, targetId);
//            pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public boolean hasLikedBack(int userId, int targetId) throws SQLException {
//        String sql = "SELECT 1 FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ? AND ACTION_TYPE = 0";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, targetId); // 對方是否 like 我
//            pstmt.setInt(2, userId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next();
//            }
//        }
//    }
//
//    @Override
//    public int insertMatchRecord(int userId, int targetId) throws SQLException {
//        String checkSql = "SELECT MATCH_ID FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ? AND ACTION_TYPE = 2";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(checkSql)) {
//            pstmt.setInt(1, userId);
//            pstmt.setInt(2, targetId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("MATCH_ID");
//                }
//            }
//        }
//
//        String insertSql = "INSERT INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 2, NOW())";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
//            pstmt.setInt(1, userId);
//            pstmt.setInt(2, targetId);
//            pstmt.executeUpdate();
//            try (ResultSet rs = pstmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        }
//
//        return -1;
//    }
//
//    @Override
//    public int createChatRoom(int userId, int targetId, int matchId) throws SQLException {
//        String sql = "INSERT INTO chat_room (USER1_ID, USER2_ID, MATCH_ID, CREATED_TIME, ROOM_STATUS) VALUES (?, ?, ?, NOW(), 0)";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            int minId = Math.min(userId, targetId);
//            int maxId = Math.max(userId, targetId);
//
//            pstmt.setInt(1, minId);
//            pstmt.setInt(2, maxId);
//            pstmt.setInt(3, matchId);
//            pstmt.executeUpdate();
//
//            try (ResultSet rs = pstmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        }
//        return -1;
//    }
//}
