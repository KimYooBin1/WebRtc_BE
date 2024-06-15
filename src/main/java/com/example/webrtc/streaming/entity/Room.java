package com.example.webrtc.streaming.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import lombok.Getter;

@Getter
public class Room {
	private Long id;
	private Map<String, WebSocketSession> clients = new HashMap<>();
	public Room(Long id) {
		this.id = id;
	}
}
