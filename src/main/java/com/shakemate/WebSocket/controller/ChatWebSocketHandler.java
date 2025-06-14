package com.shakemate.WebSocket.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // ✅ 保存連線中的使用者 Session
    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // 🔍 從 URI 擷取 userId（例如 /chatSocket/123）
    private Integer extractUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String path = uri.getPath(); // 例如 /chatSocket/123
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("✅ 使用者 " + userId + " 已連上 WebSocket");
        } else {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Integer senderId = extractUserId(session);
        String msg = message.getPayload();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(msg, Map.class);
        } catch (Exception e) {
            System.err.println("⚠️ JSON 解析錯誤：" + msg);
            return;
        }

        int roomId = (int) data.get("roomId");
        int fromId = (int) data.get("senderId");
        int receiveId = (int) data.get("receiveId");
        String type = (String) data.get("type");

        WebSocketSession targetSession = userSessions.get(receiveId);
        if (targetSession != null && targetSession.isOpen()) {
            // ✅ 回傳 JSON 給對方
            targetSession.sendMessage(new TextMessage(msg));
        }
    }    
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("🔴 使用者 " + userId + " 離線");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("❌ WebSocket 錯誤：" + exception.getMessage());
    }
}
