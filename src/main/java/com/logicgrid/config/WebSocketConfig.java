package com.logicgrid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.logicgrid.websocket.GameSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // This opens the ws://localhost:8080/LogicGrid/game endpoint
        registry.addHandler(gameSocketHandler(), "/game")
                .setAllowedOrigins("*"); // Allow connections from anywhere during testing
    }

    @Bean
    public GameSocketHandler gameSocketHandler() {
        return new GameSocketHandler();
    }
}