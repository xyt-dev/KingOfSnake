package com.kos.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

// @ServerEndpoint注解是Jakarta WebSocket API的一部分，不是Spring的一部分，Spring并不会自动扫描和注册@ServerEndpoint注解。
// @ServerEndpointExporter的作用就是自动扫描和注册这些端点，这样就不需要手动注册每一个端点了。
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
