package com.example.webrtc.chating.controller;

import static com.example.webrtc.chating.dto.ChatDto.Type.*;
import static com.example.webrtc.common.exception.ErrorCode.*;
import static java.time.LocalDateTime.*;

import java.security.Principal;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.webrtc.chating.dto.ChatDto;
import com.example.webrtc.chating.dto.UserListDto;
import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.service.ChatroomService;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
	private final UserService userService;
	private final ChatroomService chatroomService;
	private final SimpMessageSendingOperations template;

	/**
	 * MessageMapping 에 정의되어 있는 url로 message 를 보내게 되면
	 * SendTo url 을 구독하고 있는 peer 에게 데이터 처리후 전달
	 */
	@MessageMapping("/chatroom/{roomId}/join")
	@SendTo("/topic/chatroom/{roomId}")
	@Transactional	//한 tarnsactional 안에서 처리해주기 위해 설정
	// TODO : @AuthenticationPrincipal로 유저정보를 받아올 수는 없는지
	public ChatDto chatRoomJoin(@Payload ChatDto chatDto, SimpMessageHeaderAccessor headerAccessor, Principal principal){
		String loginUser = principal.getName();
		log.info("roomId = {}", chatDto.getRoomId());
		Chatroom chatroom = chatroomService.findRoomById(chatDto.getRoomId());
		//principal 이 없으면 @MessageExceptionHandler로 처리가 안돼는데 굳이 잡아줄 이유가 있는지
		User user = userService.findUserByName(loginUser);

		if(chatroom.getLimitUserCnt() <= chatroom.getUserCnt()){
			log.info("인원 제한");
			throw new CustomException(CHAT_ROOM_LIMITED_USER_ERROR);
		}
		// sessionDisconnect event 를 사용하기 때문에 해당 session 에 user 정보와 chatroom 정보 입력
		headerAccessor.getSessionAttributes().put("roomId", chatroom.getId());
		headerAccessor.getSessionAttributes().put("userId", user.getId());

		chatroom.plus();
		chatroom.connectUser(user);


		chatDto.setMessage(user.getName() +"님이 들어왔습니다");
		chatDto.setTime(now());

		log.info("{}님이  {}번 방에 접속", loginUser, chatDto.getRoomId());
		return chatDto;
	}

	/**
	 * 한 user 가 메시지 전달
	 */
	@MessageMapping("/chatroom/{roomId}/send")
	@SendTo("/topic/chatroom/{roomId}")
	@Transactional	//한 tarnsactional 안에서 처리해주기 위해 설정
	public ChatDto chatRoomMessageSend(@Payload ChatDto chatDto) {
		chatroomService.findRoomById(chatDto.getRoomId());
		chatDto.setTime(now());
		log.info("{} : {}", chatDto.getSender(), chatDto.getMessage());
		return chatDto;
	}

	@GetMapping("/chatroom/{roomId}/users")
	public ResponseEntity<List<UserListDto>> chatRoomUserList(@PathVariable Long roomId){
		log.info("{}방 유저 확인", roomId);
		List<User> chatRoomUsers = chatroomService.findChatRoomUsers(roomId);
		List<UserListDto> list = chatRoomUsers.stream().map((e) -> new UserListDto(e.getName())).toList();
		return ResponseEntity.ok(list);
	}

	@EventListener
	@Transactional
	public void userDisconnect(SessionDisconnectEvent event) {
		log.info("event = {}",event.getMessage());
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		log.info("headerAccessor : {}", headerAccessor);
		Long roomId = (Long)headerAccessor.getSessionAttributes().get("roomId");
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");

		// 비정상적인 종료로 해당 roomId, userId 가 없을 경우
		if(roomId == null || userId == null){
			log.error("roomId or userId is null");
			return;
		}
		Chatroom chatroom = chatroomService.findRoomById(roomId);
		User user = userService.findUserById(userId);
		chatroom.disconnectUser(user);
		chatroom.des();
		ChatDto chat = ChatDto.builder()
			.type(LEAVE)
			.sender(user.getName())
			.message(user.getName() + "님이 퇴장했습니다")
			.build();
		template.convertAndSend("/topic/chatroom/"+roomId, chat);
	}

	@EventListener
	public void userConnect(SessionConnectedEvent event) {
		log.info("event.getMessage = {}",event.getMessage());
	}
}
