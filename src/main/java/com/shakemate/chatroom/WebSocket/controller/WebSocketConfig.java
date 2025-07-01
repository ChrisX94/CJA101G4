package com.shakemate.chatroom.WebSocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.shakemate.notification.ws.NotificationWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @Autowired
    private NotificationWebSocketHandler notificationWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/chatSocket/{userId}")
                .setAllowedOrigins("*"); // ✅ 可改為你的 domain（例如 http://localhost:8080）

        registry.addHandler(notificationWebSocketHandler, "/notificationSocket/{userId}")
                .setAllowedOrigins("*");
    }
    
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(5 * 1024 * 1024); // 🔺增加到 5MB
        container.setMaxBinaryMessageBufferSize(5 * 1024 * 1024);
        return container;
    }
}
