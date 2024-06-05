package com.example.webrtc.common.exception;

import static java.time.LocalDateTime.*;
import static org.springframework.http.HttpStatus.*;

import java.security.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler implements GlobalExceptionHandlerInterface{
	@Override
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> globalException(CustomException e) {
		log.info("exception 발생");
		// HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		ErrorResponse response = ErrorResponse.builder()
			.timeStamp(now())
			.status(e.getErrorCode().getStatus())
			.error(e.getErrorCode().getCode())
			.message(e.getMessage())
			// .path(request.getRequestURI())
			.build();
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
	}
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		ErrorResponse response = ErrorResponse.builder()
			.timeStamp(now())
			.status(ErrorCode.EXPIRED_TOKEN_ERROR.getStatus())
			.error(ErrorCode.EXPIRED_TOKEN_ERROR.getCode())
			.message(ErrorCode.EXPIRED_TOKEN_ERROR.getMessage())
			.path(request.getRequestURI())
			.build();
		return ResponseEntity.status(UNAUTHORIZED).body(response);
	}
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		ErrorResponse response = ErrorResponse.builder()
			.timeStamp(now())
			.status(ErrorCode.INVALID_TOKEN_ERROR.getStatus())
			.error(ErrorCode.INVALID_TOKEN_ERROR.getCode())
			.message(ErrorCode.INVALID_TOKEN_ERROR.getMessage())
			.path(request.getRequestURI())
			.build();
		return ResponseEntity.status(UNAUTHORIZED).body(response);
	}
	@ExceptionHandler(MissingRequestCookieException.class)
	public ResponseEntity<ErrorResponse> handleMalformedJwtException(MissingRequestCookieException e) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		ErrorResponse response = ErrorResponse.builder()
			.timeStamp(now())
			.status(ErrorCode.INVALID_TOKEN_ERROR.getStatus())
			.error(ErrorCode.INVALID_TOKEN_ERROR.getCode())
			.message(ErrorCode.INVALID_TOKEN_ERROR.getMessage())
			.path(request.getRequestURI())
			.build();
		return ResponseEntity.status(UNAUTHORIZED).body(response);
	}
}
