//package com.shakemate.model;
//
//import org.springframework.stereotype.Repository;
//
//import com.shakemate.vo.ChatMessageVO;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//@Repository
//public class ChatMessageDAOImpl implements ChatMessageDAO {
//
//    private final DataSource dataSource;
//
//    public ChatMessageDAOImpl(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public List<ChatMessageVO> findByRoomId(int roomId) {
//        List<ChatMessageVO> list = new ArrayList<>();
//        String sql = "SELECT * FROM chat_message WHERE room_id = ? ORDER BY sent_time ASC";
//
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//
//            pstmt.setInt(1, roomId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    int messageId = rs.getInt("message_id");
//                    int senderId = rs.getInt("sender_id");
//                    String content = rs.getString("message_content");
//                    if ("null".equals(content)) {
//                        content = null;
//                    }
//
//                    String sentTime = rs.getString("sent_time");
//                    boolean isRead = rs.getBoolean("is_read");
//
//                    byte[] imgBytes = rs.getBytes("message_img");
//                    String imgBase64 = null;
//                    if (imgBytes != null && imgBytes.length > 0) {
//                        imgBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imgBytes);
//                    }
//
//                    ChatMessageVO msg = new ChatMessageVO(messageId, roomId, senderId, content, imgBase64, sentTime, isRead);
//                    list.add(msg);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    @Override
//    public void insert(ChatMessageVO message) {
//	        String sql = "INSERT INTO chat_message (room_id, sender_id, message_content, message_img, sent_time, is_read) VALUES (?, ?, ?, ?, NOW(), ?)";
//
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//
//            pstmt.setInt(1, message.getRoomId());
//            pstmt.setInt(2, message.getSenderId());
//            pstmt.setString(3, message.getContent());
//
//            if (message.getImgBytes() != null && message.getImgBytes().length > 0) {
//                pstmt.setBytes(4, message.getImgBytes());
//            } else {
//                pstmt.setNull(4, Types.BLOB);
//            }
//
//            pstmt.setInt(5, 0); // is_read 預設為 0（未讀）
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void markMessagesAsRead(int userId, int roomId) {
//        String sql = "UPDATE chat_message SET is_read = 1 WHERE room_id = ? AND sender_id != ? AND is_read = 0";
//
//        int maxRetry = 3;
//        int retryDelay = 150;
//
//        for (int i = 0; i < maxRetry; i++) {
//            try (Connection con = dataSource.getConnection();
//                 PreparedStatement pstmt = con.prepareStatement(sql)) {
//
//                pstmt.setInt(1, roomId);
//                pstmt.setInt(2, userId);
//                int updatedRows = pstmt.executeUpdate();
//
//                if (updatedRows > 0) {
////                    System.out.println("✅ 成功將 " + updatedRows + " 筆訊息標記為已讀");
//                    break;
//                } else {
////                    System.out.println("⚠️ 第 " + (i + 1) + " 次嘗試未標記任何訊息為已讀");
//                    if (i < maxRetry - 1) {
//                        Thread.sleep(retryDelay);
//                    }
//                }
//
//            } catch (SQLException e) {
//                System.err.println("❌ 資料庫錯誤：" + e.getMessage());
//                break;
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
//}
