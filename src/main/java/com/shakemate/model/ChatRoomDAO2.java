package com.shakemate.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakemate.vo.ChatRoomVO;

public interface ChatRoomDAO2 extends JpaRepository<ChatRoomVO, Integer>{

}
