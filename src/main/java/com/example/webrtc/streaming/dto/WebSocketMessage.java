package com.example.webrtc.streaming.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class WebSocketMessage {
	private String from;
	private String type;
	private String data;
	private Object candidate;
	private Object sdp;

	public WebSocketMessage(String from, String type, String data, Object candidate, Object sdp) {
		this.from = from;
		this.type = type;
		this.data = data;
		this.candidate = candidate;
		this.sdp = sdp;
	}
}
