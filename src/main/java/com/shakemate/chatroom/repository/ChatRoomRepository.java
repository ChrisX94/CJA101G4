package com.shakemate.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakemate.chatroom.vo.ChatRoomVO;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoomVO, Integer>{
    @Query("""
	        SELECT cr.roomId FROM ChatRoomVO AS cr WHERE 
				        (cr.user1Id = :viewerId AND cr.user2Id = :seller) OR 
					    (cr.user1Id = :seller AND cr.user2Id = :viewerId)
			""")
    Integer findRoomIdForShop(Integer viewerId, Integer seller);
}
