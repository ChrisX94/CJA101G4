package com.shakemate.model;

import org.springframework.stereotype.Repository;

import com.shakemate.vo.ChatRoomVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatRoomDAOImpl implements ChatRoomDAO{

    private final DataSource dataSource;

    public ChatRoomDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<ChatRoomVO> findByUserId(int userId) {
        List<ChatRoomVO> list = new ArrayList<>();
        String sql = """
                SELECT
                    cr.room_id,
                    cr.user1_id,
                    cr.user2_id,
                    cr.match_id,
                    u.user_id AS peer_id,
                    u.username AS peer_name,
                    u.img1 AS peer_avatar,

                    CASE 
                        WHEN LENGTH(cm.message_img) > 0 THEN 'image:preview'
                        WHEN cm.message_content IS NOT NULL THEN cm.message_content
                        ELSE NULL
                    END AS last_message,

                    cm.sent_time AS last_sent_time,
                    cr.created_time,
                    
                    EXISTS (
                      SELECT 1 FROM chat_message m
                      WHERE m.room_id = cr.room_id
                        AND m.sender_id != ?
                        AND m.is_read = 0
                    ) AS has_unread

                FROM chat_room cr
                JOIN users u
                  ON u.user_id = CASE
                                  WHEN cr.user1_id = ? THEN cr.user2_id
                                  ELSE cr.user1_id
                                END
                LEFT JOIN (
                    SELECT c1.*
                    FROM chat_message c1
                    JOIN (
                        SELECT room_id, MAX(sent_time) AS max_time
                        FROM chat_message
                        GROUP BY room_id
                    ) c2 ON c1.room_id = c2.room_id AND c1.sent_time = c2.max_time
                ) cm ON cm.room_id = cr.room_id
                WHERE cr.user1_id = ? OR cr.user2_id = ?
                ORDER BY COALESCE(cm.sent_time, cr.created_time) DESC
                """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChatRoomVO chatRoom = new ChatRoomVO(
                            rs.getInt("room_id"),
                            rs.getInt("user1_id"),
                            rs.getInt("user2_id"),
                            rs.getInt("match_id"),
                            rs.getInt("peer_id"),
                            rs.getString("peer_name"),
                            rs.getString("peer_avatar"),
                            rs.getString("last_message"),
                            rs.getTimestamp("last_sent_time"),
                            rs.getTimestamp("created_time"),
                            rs.getBoolean("has_unread")
                    );
                    list.add(chatRoom);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 日後可改為 logger.error()
        }

        return list;
    }
}
