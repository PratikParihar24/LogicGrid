package com.logicgrid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.logicgrid.websocket.GameSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameSocketHandler(), "/game")
                .addInterceptors(new HttpSessionHandshakeInterceptor()) // <-- THE IDENTITY BRIDGE
                .setAllowedOrigins("*"); 
    }

    @Bean
    public GameSocketHandler gameSocketHandler() {
        return new GameSocketHandler();
    }
}