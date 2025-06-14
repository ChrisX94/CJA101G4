package com.shakemate.controller;

import com.shakemate.service.MatchService;
import com.shakemate.vo.UserProfileVO;
import com.shakemate.model.UserProfileDAOImpl;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    
    @Autowired
    private MatchService matchService;

    @Autowired
    private UserProfileDAOImpl userProfileDAO;

    
    @GetMapping("/currentUserId")
    public Map<String, Object> getCurrentUserId(HttpSession session) {
        Integer id = (Integer) session.getAttribute("account");
        Map<String, Object> result = new HashMap<>();
        result.put("currentUserId", id);
        return result;
    }

    @GetMapping("/getNext")
    public UserProfileVO getNextUser(HttpSession session) throws Exception {
        Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
        UserProfileVO profile = userProfileDAO.getRandomUnmatchedUser(currentUserId);
        if (profile == null) {
            throw new NoSuchElementException("沒有更多會員");
        }
        return profile;
    }

    @PostMapping(value = "/getFiltered", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<UserProfileVO> getFiltered(@RequestBody Map<String, Object> data, HttpSession session) throws Exception {
        Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());

        Integer gender = null;
        try {
            String genderStr = (String) data.get("gender");
            if (genderStr != null && !genderStr.isBlank()) {
                gender = Integer.parseInt(genderStr);
            }
        } catch (Exception ignored) {}

        List<String> interests = (List<String>) data.get("interests");
        List<String> personality = (List<String>) data.get("personality");

        return userProfileDAO.prefer_matched(currentUserId, interests, personality, gender);
    }

    @PostMapping("/like")
    public Map<String, Object> likeUser(@RequestParam int targetId, HttpSession session) throws Exception {
        Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
        Map<String, Object> result = new HashMap<>();
        
        // 檢查是否已經有對這個人按過的紀錄
        if (matchService.hasUserActed(currentUserId, targetId)) {
            result.put("alreadyActed", true);
        } else {
        	// 寫入 Like 紀錄
            matchService.insertLike(currentUserId, targetId);
            
            // 檢查對方是否也 Like 我（互相喜歡）
            if (matchService.hasLikedBack(currentUserId, targetId)) {
            	// 寫入配對紀錄並建立聊天室
                int matchId = matchService.insertMatchRecord(currentUserId, targetId);
                int roomId = matchService.createChatRoom(currentUserId, targetId, matchId);
                result.put("matched", true);
                result.put("roomId", roomId);
            } else {
                result.put("matched", false);
            }
        }
        return result;
    }

    @PostMapping("/dislike")
    public Map<String, Object> dislikeUser(@RequestParam int targetId, HttpSession session) throws Exception {
        Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
        Map<String, Object> result = new HashMap<>();

        if (!matchService.hasUserActed(currentUserId, targetId)) {
            matchService.insertDislike(currentUserId, targetId);
        }
        result.put("disliked", true);
        return result;
    }
}
