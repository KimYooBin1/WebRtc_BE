package com.example.webrtc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// "/app" 을 prefix로 하는 주소를 통해서 구독한다
		registry.enableSimpleBroker("/app");
		/** "/topic" 을 prefix로 하는 주소를 통해서 메시지를 보낸다
		 * 주로 @MessageMapping 과 @SendTo 를 사용해 구독하고 있는 peer 에게 data를 전송한다
		 */
		registry.setApplicationDestinationPrefixes("/topic");
	}
}
