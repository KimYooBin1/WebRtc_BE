package com.example.webrtc.common.filter;

import static org.springframework.messaging.simp.stomp.StompCommand.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.webrtc.common.utils.JWTUtil;

import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StompInterceptor implements ChannelInterceptor {
	private final JWTUtil jwtUtil;
	String memberName = "";
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		log.info("jwt header = {}", headerAccessor);
		// log.info("jwt header Detail = {}", headerAccessor.getHeartbeat());
		StompCommand command = headerAccessor.getCommand();

		//subscribe 에서 JWT를 활용해 principal 설정
		if(command.equals(DISCONNECT)){
			return message;
		}
		// String authorizationHeader = headerAccessor.getNativeHeader("Authorization").get(0);
		String authorizationHeader = null;

		log.info("headerAccess = {}", authorizationHeader);
		if(authorizationHeader == null){
			log.info("token이 없다");
			// TODO : 여기서도 exception 처리?
			throw new MalformedJwtException("jwt");
		}

		String token = "";
		if(authorizationHeader.startsWith("Bearer ")){
			token = authorizationHeader.split(" ")[1];
			log.info("token = {}", token);
		}
		else{
			log.info("token 형식이 잘못됬습니다. : {}", authorizationHeader);
			// TODO : exception 처리
			throw new MalformedJwtException("jwt");
		}
		memberName = jwtUtil.getUsername(token);
		log.info("memberName = {}", memberName);
		this.setAuthentication(message, headerAccessor);
		return message;
	}

	private void setAuthentication(Message<?> message, StompHeaderAccessor headerAccessor) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberName,null, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		headerAccessor.setUser(authentication);
	}
}
