package com.example.webrtc.streaming.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import lombok.Getter;

@Getter
public class Room {
	// DB와 in-memory에서의 id를 동일하게 하기 위해 Long 타입으로 선언
	private Long id;
	//session ID를 key로 하여 WebSocketSession을 저장
	private Map<String, WebSocketSession> clients = new HashMap<>();
	public Room(Long id) {
		this.id = id;
	}
}
