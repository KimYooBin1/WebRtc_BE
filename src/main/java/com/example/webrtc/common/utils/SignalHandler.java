package com.example.webrtc.common.utils;

import static com.example.webrtc.common.exception.ErrorCode.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.streaming.dto.WebSocketMessage;
import com.example.webrtc.streaming.service.StreamService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {
	private Map<String, Chatroom> sessionIdToRoomMap = new HashMap<>();
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
		log.info("websocket is closed: {}", status);
		log.debug("websocket connection closed: {}", session);
		// 종료되면 map에서 session과 mapping된 room을 제거
		sessionIdToRoomMap.remove(session.getId());
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Connection established: {}", session);
		Chatroom room = sessionIdToRoomMap.get(session.getId());
		// 해당 방에 인원이 존재하는지 분석해서 있으면 true, 없으면 false 를 return 해서 다음에 처리할 일을 결정한다
		sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(room != null), null, null));
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
				sessionIdToRoomMap.put(session.getId(), streamService.findRoomById(data).orElseThrow(
						() -> new CustomException(STREAM_ROOM_NOT_FOUND_ERROR)
				));
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
