package com.example.webrtc.common.exception;

import org.springframework.http.ResponseEntity;

public interface GlobalExceptionHandlerInterface {
	public ResponseEntity<ErrorResponse> globalException(CustomException e);
}
