package com.shakemate.chatroom.controller;

import com.shakemate.chatroom.model.ChatRoomDAO;
import com.shakemate.chatroom.repository.ChatMessageRepository;
import com.shakemate.chatroom.service.ChatMessageService;
import com.shakemate.chatroom.vo.ChatMessageVO;
import com.shakemate.chatroom.vo.ChatRoomVO;
import com.shakemate.match.model.UserProfileDAO;
import com.shakemate.match.vo.UserProfileVO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {
	
	@Autowired
	private ChatRoomDAO chatRoomDAO;
	
	@Autowired
	private ChatMessageService chatMessageService;
	
	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private UserProfileDAO userProfileDAO;
    
	
    @GetMapping("/currentUserId")
    public Map<String, Object> getCurrentUserId(HttpSession session) {
        Integer id = (Integer) session.getAttribute("account");
        Map<String, Object> result = new HashMap<>();
        result.put("currentUserId", id);
        return result;
    }
    
    // ✅ 取得聊天室清單
    @GetMapping("/list")
    public ResponseEntity<?> getChatRooms(HttpSession session) {
        Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
        List<ChatRoomVO> list = chatRoomDAO.findByUserId(currentUserId);
        return ResponseEntity.ok(list);
    }

    // ✅ 取得聊天室訊息
    @GetMapping("/messages")
    public ResponseEntity<?> getMessages(@RequestParam int roomId) {
//        List<ChatMessageVO> messages = chatMessageDAO.findByRoomId(roomId);
    	List<ChatMessageVO> messages = chatMessageService.getMessagesWithImageBase64(roomId);
        return ResponseEntity.ok(messages);
    }

    // ✅ 傳送訊息（純文字或圖片）
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam int roomId,
            @RequestParam int senderId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile img
    ) throws IOException {

        byte[] imgBytes = null;
        if (img != null && !img.isEmpty()) {
            imgBytes = img.getBytes();
        }

        ChatMessageVO msg = new ChatMessageVO();
        msg.setRoomId(roomId);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setImgBytes(imgBytes);

        chatMessageService.insertMessage(msg);
        return ResponseEntity.ok("sent");
    }

    // ✅ 已讀訊息
    @PostMapping("/markAsRead")
    public ResponseEntity<?> markAsRead(@RequestParam int roomId, HttpSession session) {
        try {
            Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
            chatMessageService.markMessagesAsReadWhenClickRoom(currentUserId, roomId);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("error");
        }
    }
    
    // ✅ 雙方都在聊天室內的已讀訊息
    @PostMapping("/markAsReadInRoom")
    public ResponseEntity<?> markAsReadInRoom(@RequestParam int roomId, @RequestParam int currentUserId, HttpSession session) {
        try {
            chatMessageService.markMessagesAsReadWhenInRoom(currentUserId, roomId);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("error");
        }
    }

    // ✅ 取得對方使用者資料
    @GetMapping("/userProfile")
    public ResponseEntity<?> getUserProfile(@RequestParam int peerId) {
        try {
            UserProfileVO vo = userProfileDAO.findById(peerId);
            if (vo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(vo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("error");
        }
    }


}
