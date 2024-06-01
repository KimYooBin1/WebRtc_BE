package com.example.webrtc.common.exception;

import static java.time.LocalDateTime.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler implements GlobalExceptionHandlerInterface{
	@Override
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> globalException(CustomException e) {
		log.info("exception 발생");
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		ErrorResponse response = ErrorResponse.builder()
			.timeStamp(now())
			.status(e.getErrorCode().getStatus())
			.error(e.getErrorCode().getCode())
			.message(e.getMessage())
			.path(request.getRequestURI())
			.build();
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
	}
}
