package com.shakemate.model;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shakemate.vo.ChatMessageVO;

import jakarta.transaction.Transactional;

@Repository
public interface ChatMessageDAO2 extends JpaRepository<ChatMessageVO, Integer> {
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
    int markMessagesAsRead(@Param("userId") int userId, @Param("roomId") int roomId);
}
