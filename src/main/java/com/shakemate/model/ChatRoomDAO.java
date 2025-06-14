package com.shakemate.model;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shakemate.vo.ChatRoomVO;

@Repository
public interface ChatRoomDAO {
	public List<ChatRoomVO> findByUserId(int userId);
}
