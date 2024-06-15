package com.example.webrtc.streaming.service;

import static com.example.webrtc.chating.dto.ChatType.*;
import static com.example.webrtc.common.exception.ErrorCode.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.repository.ChatroomRepository;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.service.UserService;
import com.example.webrtc.streaming.entity.Room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StreamService {
	private final ChatroomRepository chatroomRepository;
	private final UserService userService;
	private Set<Room> rooms = new TreeSet<>(Comparator.comparing(Room::getId));
	public Room findByRoomId(Long id) {
		return rooms.stream().filter(room -> room.getId().equals(id)).findFirst().orElseThrow(
				() -> new CustomException(ROOM_NOT_FOUND_ERROR)
		);
	}
	public Map<String, WebSocketSession> getClient(Room room){
		return Optional.ofNullable(room)
			.map(r -> Collections.unmodifiableMap(r.getClients()))
			.orElse(Collections.emptyMap());
	}
	public List<Chatroom> findAllRoom(){
		return chatroomRepository.findAllByType(Streaming);
	}
	public Optional<Chatroom> findRoomById(Long id) {
		return chatroomRepository.findByIdAndType(id, Streaming);
	}

	public WebSocketSession addClient(Room room, WebSocketSession session) {
		return room.getClients().put(session.getId(), session);
	}

	@Transactional
	public Chatroom createRoom(CreateRoom request) {
		Chatroom chatroom = new Chatroom(request.getRoomName(), 2L, Streaming);
		Chatroom save = chatroomRepository.save(chatroom);
		rooms.add(new Room(save.getId()));
		return save;
	}
	@Transactional
	public void joinRoom(Long RoomId, Principal principal) {
		if(principal == null) {
			// TODO : FE client 에 error message 전달해줘야됨
			throw new CustomException(PRINCIPAL_NOT_FOUND_ERROR);
		}
		Chatroom room = chatroomRepository.findById(RoomId).orElseThrow();
		log.info("User {} joined room {}", principal.getName(), room.getId());
		room.plus();
		room.connectUser(userService.findUserByUserName(principal.getName()));
	}
	@Transactional
	public void leaveRoom(Long RoomId, Principal principal) {
		if(principal == null) {
			// TODO : FE client 에 error message 전달해줘야됨
			throw new CustomException(PRINCIPAL_NOT_FOUND_ERROR);
		}
		Chatroom room = chatroomRepository.findById(RoomId).orElseThrow();
		log.info("User {} left room {}", principal.getName(), room.getId());
		room.des();
		room.disconnectUser(userService.findUserByUserName(principal.getName()));
	}
}
