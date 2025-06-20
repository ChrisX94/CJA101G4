package com.shakemate.chatroom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shakemate.chatroom.vo.ChatMessageVO;

import jakarta.transaction.Transactional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageVO, Integer> {
	List<ChatMessageVO> findByRoomIdOrderBySentTimeAsc(int roomId);

	@Modifying
	@Transactional
	@Query("""
			    UPDATE ChatMessageVO m
			    SET m.isRead = true
			    WHERE m.roomId = :roomId
			      AND m.senderId <> :userId
			      AND m.isRead = false
			""")
	int markMessagesAsReadWhenClickRoom(@Param("userId") int userId, @Param("roomId") int roomId);

	@Modifying
	@Transactional
	@Query(value = """
			    UPDATE Chat_Message
			    SET is_Read = true
			    WHERE room_Id = :roomId
			      AND sender_Id = :userId
			      AND is_Read = false
			""", nativeQuery = true)
	int markMessagesAsReadWhenInRoom(@Param("userId") int userId, @Param("roomId") int roomId);
}
