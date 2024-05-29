package com.example.webrtc.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.webrtc.common.filter.StompInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {
	private final StompInterceptor stompInterceptor;
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket")
			.setAllowedOriginPatterns("*")	//CORS 문제 해결
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// "/app" 을 prefix로 하는 주소를 통해서 구독한다
		registry.enableSimpleBroker("/topic");
		/** "/topic" 을 prefix로 하는 주소를 통해서 메시지를 보낸다
		 * 주로 @MessageMapping 과 @SendTo 를 사용해 구독하고 있는 peer 에게 data를 전송한다
		 */
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompInterceptor);
	}
}
