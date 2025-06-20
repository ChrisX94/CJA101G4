package com.shakemate.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakemate.chatroom.vo.ChatRoomVO;

public interface ChatRoomRepository extends JpaRepository<ChatRoomVO, Integer>{

}
