package com.shakemate.notification.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private Integer extractUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String path = uri.getPath(); // e.g., /notificationSocket/123
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            logger.error("無法從 URI 解析 User ID: {}", path);
            return null;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            logger.info("使用者 {} 已連上通知 WebSocket", userId);
        } else {
            try {
                session.close(CloseStatus.BAD_DATA.withReason("無效的使用者 ID"));
            } catch (IOException e) {
                logger.error("關閉無效連線時發生錯誤", e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            logger.info("使用者 {} 已離線，原因: {}", userId, status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("WebSocket 傳輸錯誤", exception);
    }

    /**
     * 發送訊息給指定使用者
     * @param userId 使用者 ID
     * @param message 要發送的訊息
     */
    public static void sendMessageToUser(Integer userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                logger.info("成功發送通知給使用者 {}", userId);
            } catch (IOException e) {
                logger.error("發送通知給使用者 {} 時發生錯誤", userId, e);
            }
        } else {
            logger.warn("嘗試發送通知給離線使用者 {} 或 Session 已關閉", userId);
        }
    }
} 