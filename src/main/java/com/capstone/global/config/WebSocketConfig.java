package com.capstone.global.config;

//import com.capstone.domain.chat.interceptor.WebSocketSecurityInterceptor;
import com.capstone.domain.document.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;
    //private final WebSocketSecurityInterceptor interceptors;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/socket/doc/wss", "/socket/notification/wss")
                // .setAllowedOriginPatterns("https://docktalk.co.kr", "http://localhost:3000").withSockJS();
                .setAllowedOriginPatterns("http://180.210.81.232:8080", "https://docktalk.co.kr", "http://localhost:3000").withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //sub으로 시작되는 요청을 구독한 모든 사용자들에게 메시지를 broadcast한다.
        registry.enableSimpleBroker("/sub", "/topic", "/queue");

        // pub로 시작되는 메시지는 message-handling methods로 라우팅된다.
        registry.setApplicationDestinationPrefixes("/pub", "/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // connect / disconnect 인터셉터
        registration.interceptors(authInterceptor);
    }
}