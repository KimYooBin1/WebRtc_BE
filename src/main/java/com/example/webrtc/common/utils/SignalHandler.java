package com.example.webrtc.common.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.webrtc.streaming.dto.WebSocketMessage;
import com.example.webrtc.streaming.entity.Room;
import com.example.webrtc.streaming.service.StreamService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalHandler extends TextWebSocketHandler {
	// 원래 목적의 취지는 해당 방에 sessionId를 저장해두어서 해당 방에 유저가 있는지 확인하는 용도를 겸한다
	// 그래서 해당 방에 유저가 있을떄만 true를 반환하도록 설계되어있다
	// 근데 여기서는 그냥 map에 한명이라도 있으면 같은 방이 아니더라도 실행되는 문제가 있다
	// TODO : 수정해야 된다는 뜻 :)
	private Map<String, Room> sessionIdToRoomMap = new HashMap<>();
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
		// 해당 방에 인원이 존재하는지 분석해서 있으면 true, 없으면 false 를 return 해서 다음에 처리할 일을 결정한다
		// TODO : 지금 코드에서는 해당 방의 인원이 있는지에 따라 T/F를 반환하는게 아닌, 다른 session이 있는지에 따라 T/F를 반환한다
		sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(!sessionIdToRoomMap.isEmpty()), null, null));
	}

	@Override
	// 들어오는 message type에 따라 처리
	protected void handleTextMessage(WebSocketSession session, TextMessage text) throws Exception {
		// 받은 메시지를 WebSocketMessage로 변환
		WebSocketMessage message = objectMapper.readValue(text.getPayload(), WebSocketMessage.class);
		String userName = message.getFrom();
		String data = message.getData();
		Room room;
		long id;
		log.info("Received message: {}", message);
		switch (message.getType()) {
			case MSG_TYPE_OFFER:
			case MSG_TYPE_ANSWER:
			case MSG_TYPE_ICE:
				Object candidate = message.getCandidate();
				Object sdp = message.getSdp();
				log.info("Signal: {}",
					candidate != null ? candidate.toString().substring(0, 64) : sdp.toString().substring(0, 64));
				// 현재 유저가 속해있는 방
				room = sessionIdToRoomMap.get(session.getId());
				if(room != null) {
					// 해당 방에 있는 유저들 중 현재 유저를 제외한 유저들에게 메시지 전송
					Map<String, WebSocketSession> clients = streamService.getClient(room);
					clients.forEach((sessionId, client) -> {
						if(!sessionId.equals(session.getId())) {
							sendMessage(client, new WebSocketMessage(
								userName,
								message.getType(),
								data,
								candidate,
								sdp
							));
						}
					});
				}
				break;
			case MSG_TYPE_JOIN:
				id = Long.parseLong(message.getData());
				// repository에서 id로 방을 찾아서 해당 방에 유저를 추가(DB 처리)
				streamService.joinRoom(id, session.getPrincipal());
				// TODO : service단으로 한번에 묶어도 될거 같은데
				// in-memory에서 해당 방을 찾아서 가져옴(in- memory 처리)
				Room findRoom = streamService.findByRoomId(id);
				// 해당 방에 sessionId를 매핑
				sessionIdToRoomMap.put(session.getId(), findRoom);
				// in-memory에 session을 저장
				streamService.addClient(findRoom, session);
				break;
			case MSG_TYPE_LEAVE:
				id = Long.parseLong(message.getData());
				// repository에서 id로 방을 찾아서 해당 방에서 유저를 제거(DB 처리)
				streamService.leaveRoom(id, session.getPrincipal());
				// TODO : service로 처리를 해줘도 될거 같다
				//Map 으로 부터 현재 나간 방을 검색
				room = sessionIdToRoomMap.get(session.getId());
				// 해당 방의 client 에서 해당 session을 제거
				room.getClients().remove(session.getId());
				int size = room.getClients().size();
				log.info("Room {} has {} clients", room.getId(), size);
				// 방의 인원이 0명이면 방을 삭제
				if(size == 0) {
					log.info("Room {} is empty. Removing room.", room.getId());
					streamService.removeRoom(room);
				}
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
