package com.shakemate.model;

import java.sql.SQLException;
import java.util.List;

import com.shakemate.vo.UserProfileVO;

public interface UserProfileDAO {
	public UserProfileVO findById(int userId) throws SQLException;
	public UserProfileVO getRandomUnmatchedUser(int currentUserId) throws SQLException;
	public List<UserProfileVO> prefer_matched(int currentUserId, List<String> interests, List<String> personality,
			Integer gender);
}
