package com.example.webrtc.streaming.service;

import static com.example.webrtc.chating.dto.ChatType.*;
import static com.example.webrtc.common.exception.ErrorCode.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.repository.ChatroomRepository;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StreamService {
	private final ChatroomRepository chatroomRepository;
	private final UserService userService;
	public List<Chatroom> findAllRoom(){
		return chatroomRepository.findAllByType(Streaming);
	}
	public Optional<Chatroom> findRoomById(Long id) {
		return chatroomRepository.findByIdAndType(id, Streaming);
	}

	@Transactional
	public Chatroom createRoom(CreateRoom request) {
		Chatroom chatroom = new Chatroom(request.getRoomName(), 2L, Streaming);
		return chatroomRepository.save(chatroom);
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
