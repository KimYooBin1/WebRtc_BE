package com.example.webrtc.common.utils;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.webrtc.streaming.dto.WebSocketMessage;
import com.example.webrtc.streaming.service.StreamService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {
	private final StreamService streamService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String MSG_TYPE_TEXT = "text";
	private static final String MSG_TYPE_OFFER = "offer";
	private static final String MSG_TYPE_ANSWER = "answer";
	private static final String MSG_TYPE_ICE = "ice";
	private static final String MSG_TYPE_JOIN = "join";
	private static final String MSG_TYPE_LEAVE = "leave";

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("websocket connection closed: {}", session);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Connection established: {}", session);
		// sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_TEXT, Boolean.toString(true), null, null));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage text) throws Exception {
		WebSocketMessage message = objectMapper.readValue(text.getPayload(), WebSocketMessage.class);
		String userName = message.getFrom();
		Long data = Long.parseLong(message.getData());
		log.info("Received message: {}", message);
		switch (message.getType()) {
			case MSG_TYPE_OFFER:
			case MSG_TYPE_ANSWER:
			case MSG_TYPE_ICE:
				break;
			case MSG_TYPE_JOIN:
				streamService.joinRoom(data, session.getPrincipal());
				break;
			case MSG_TYPE_LEAVE:
				streamService.leaveRoom(data, session.getPrincipal());
				break;
			default:
				log.debug("Unknown message type: {}", message.getType());
		}
	}

	private void sendMessage(WebSocketSession session, WebSocketMessage message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			session.sendMessage(new TextMessage(json));
		} catch (IOException e) {
			log.debug("An error occured: {}", e.getMessage());
		}
	}
}
