package com.example.webrtc.common.exception;

import java.security.Principal;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.webrtc.common.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalMessageExceptionHandler {
	private final GlobalExceptionHandler globalExceptionHandler;
	private final SimpMessagingTemplate template;
	private final MethodProvider methodProvider = new MethodProvider();

	@MessageExceptionHandler(CustomException.class)
	public void handleCustomException(CustomException e, Principal principal){
		log.info("Stomp 예외 발생");
		log.info("principal = {}", principal.getName());
		// TODO : 방번호를 어떻게 받아올지, sub 주소는 어떻게 해야될지
		template.convertAndSendToUser(principal.getName(), "/topic/chatroom/552",
			Objects.requireNonNull(methodProvider.globalException(e).getBody()));
	}
	private class MethodProvider implements GlobalExceptionHandlerInterface{
		@Override
		public ResponseEntity<ErrorResponse> globalException(CustomException e) {
			return globalExceptionHandler.globalException(e);
		}
	}
}
