package com.example.webrtc.common.exception;

import java.security.Principal;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
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
	public String handleCustomException(CustomException e, SimpMessageHeaderAccessor accessor){
		log.info("accessor = {}", accessor.getSessionAttributes());
		String user = String.valueOf(accessor.getSessionAttributes().get("userId"));
		// String user = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("Stomp 예외 발생");
		log.info("user = {}", user);
		// TODO : @AuthenticationPrincipal, principal 이 작동 안함
		// template.convertAndSendToUser("user", "/topic/chatroom/"+"123",
		// 	Objects.requireNonNull(methodProvider.globalException(e).getBody()));
		return "error";
	}
	private class MethodProvider implements GlobalExceptionHandlerInterface{
		@Override
		public ResponseEntity<ErrorResponse> globalException(CustomException e) {
			return globalExceptionHandler.globalException(e);
		}
	}
}
