package kyodream.controller.config;

import kyodream.server.ContextSocketHandler;
import kyodream.server.FrameworkSocketHandler;
import kyodream.server.LogSocketHandler;
import kyodream.server.StackSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketServerConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ContextSocketHandler(), "/ws/context")
                .setAllowedOrigins("*");
        registry.addHandler(new FrameworkSocketHandler(), "/ws/framework")
                .setAllowedOrigins("*");
        registry.addHandler(new StackSocketHandler(), "/ws/stack")
                .setAllowedOrigins("*");
        registry.addHandler(new LogSocketHandler(), "/ws/log")
                .setAllowedOrigins("*");
    }
}
