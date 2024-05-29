package com.example.webrtc.common.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private LocalDateTime timeStamp;
	private int status;
	private String error;
	private String message;
	// TODO : 해당 정보를 내가 컨트롤 가능한가?
	private String path;
}
