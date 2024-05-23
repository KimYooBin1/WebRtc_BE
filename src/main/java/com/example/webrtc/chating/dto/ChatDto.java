package com.example.webrtc.chating.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
	public enum Type{
		ENTER, TALK, LEAVE
	}
	private Type type;
	private String sender;
	private String message;
	private LocalDateTime time;
}
