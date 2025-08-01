package com.shakemate.chatroom.service;

import com.shakemate.chatroom.repository.ChatMessageRepository;
import com.shakemate.chatroom.vo.ChatMessageVO;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {
	
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    // 處理圖片訊息，Bytes轉Base64，才能丟給前端
    public List<ChatMessageVO> getMessagesWithImageBase64(int roomId) {
        List<ChatMessageVO> messages = chatMessageRepository.findByRoomIdOrderBySentTimeAsc(roomId);
        
        for (ChatMessageVO vo : messages) {
            byte[] imgBytes = vo.getImgBytes();
            if (imgBytes != null) {
                String imgBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imgBytes);
                vo.setImgBase64(imgBase64);
            }
        }
        return messages;
    }
    
    // 將新訊息寫入資料庫
    public void insertMessage(ChatMessageVO message) {
        // 預設為未讀
        message.setRead(false);
        // 若圖片為空，直接設 null（避免空陣列）
        if (message.getImgBytes() == null || message.getImgBytes().length == 0) {
            message.setImgBytes(null);
        }
        chatMessageRepository.save(message);
    }
    
    public void markMessagesAsReadWhenClickRoom(int userId, int roomId) {
//        int maxRetry = 3;
//        int retryDelay = 150;
//        int updatedRows = chatMessageRepository.markMessagesAsReadWhenClickRoom(userId, roomId);
//        for (int i = 0; i < maxRetry; i++) {
//            try {
//                if (updatedRows > 0) {
////                    System.out.println("✅ 第 " + (i + 1) + " 次：已標記 " + updatedRows + " 筆訊息為已讀");
//                    break;
//                } else {
////                    System.out.println("⚠️ 第 " + (i + 1) + " 次：沒有標記任何訊息為已讀");
//                    if (i < maxRetry - 1) {
//                        Thread.sleep(retryDelay);
//                    }
//                }
//            } catch (Exception e) {
//                System.err.println("❌ 發生錯誤：" + e.getMessage());
//                break;
//            }
//        }
        chatMessageRepository.markMessagesAsReadWhenClickRoom(userId, roomId);
    }

    public void markMessagesAsReadWhenInRoom(int userId, int roomId) {
//        int maxRetry = 3;
//        int retryDelay = 1500;
//        int updatedRows = chatMessageRepository.markMessagesAsReadWhenInRoom(userId, roomId);
//        for (int i = 0; i < maxRetry; i++) {
//            try {
//                if (updatedRows > 0) {
////                    System.out.println("✅ 第 " + (i + 1) + " 次：已標記 " + updatedRows + " 筆訊息為已讀");
//                    break;
//                } else {
////                    System.out.println("⚠️ 第 " + (i + 1) + " 次：沒有標記任何訊息為已讀");
//                    if (i < maxRetry - 1) {
//                        Thread.sleep(retryDelay);
//                    }
//                }
//            } catch (Exception e) {
//                System.err.println("❌ 發生錯誤：" + e.getMessage());
//                break;
//            }
//        }
        chatMessageRepository.markMessagesAsReadWhenInRoom(userId, roomId);

    }
}
