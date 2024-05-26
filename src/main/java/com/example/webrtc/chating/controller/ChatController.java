package com.example.webrtc.chating.controller;

import static java.time.LocalDateTime.*;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.webrtc.chating.dto.ChatDto;
import com.example.webrtc.chating.dto.UserListDto;
import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.repository.ChatroomRepository;
import com.example.webrtc.chating.service.ChatroomService;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.repository.UserRespository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
	private final ChatroomRepository chatroomRepository;
	private final UserRespository userRespository;
	private final ChatroomService chatroomService;
	/**
	 * MessageMapping 에 정의되어 있는 url로 message 를 보내게 되면
	 * SendTo url 을 구독하고 있는 peer 에게 데이터 처리후 전달
	 */
	@MessageMapping("/chatroom/{roomId}/join")
	@SendTo("/topic/chatroom/{roomId}")
	@Transactional	//한 tarnsactional 안에서 처리해주기 위해 설정
	public ChatDto chatRoomJoin(@Payload ChatDto chatDto){
		log.info("{}",chatDto);
		Chatroom chatroom = chatroomRepository.findById(chatDto.getRoomId()).orElseThrow(
			// TODO : 해당 id의 chatroom이 없을 경우
		);
		String sender = chatDto.getSender();
		chatroom.plus();
		chatroom.connectUser(userRespository.findByName(sender).orElseThrow(
			// TODO : 이름이 없을때는 어떻게 처리할 것인가
		));
		log.info("chatroomlist : {}", chatroom.getUserList().get(0).getName());
		chatDto.setMessage(sender+"님이 들어왔습니다");
		chatDto.setTime(now());
		log.info("{}님이  {}번 방에 접속", sender, chatDto.getRoomId());
		return chatDto;
	}

	/**
	 * 한 user 가 메시지 전달
	 */
	@MessageMapping("/chatroom/{roomId}/send")
	@SendTo("/topic/chatroom/{roomId}")
	@Transactional	//한 tarnsactional 안에서 처리해주기 위해 설정
	public ChatDto chatRoomMessageSend(@Payload ChatDto chatDto) {
		chatroomRepository.findById(chatDto.getRoomId()).orElseThrow(
			// TODO : 해당 id의 chatroom이 없을 경우
		);
		chatDto.setTime(now());
		// TODO : message가 비어있을때 BE 에서 처리할지 FE 에서 처리할지
		log.info("{} : {}", chatDto.getSender(), chatDto.getMessage());
		return chatDto;
	}

	// TODO : 채팅방을 나갔을떄

	@GetMapping("/chatroom/{roomId}/users")
	public ResponseEntity<List<UserListDto>> chatRoomUserList(@PathVariable Long roomId){
		log.info("{}방 유저 확인", roomId);
		List<User> chatRoomUsers = chatroomService.findChatRoomUsers(roomId);
		// log.info("{}",chatRoomUsers);
		List<UserListDto> list = chatRoomUsers.stream().map((e) -> {
			return new UserListDto(e.getName());
		}).toList();
		return ResponseEntity.ok(list);
	}
}
