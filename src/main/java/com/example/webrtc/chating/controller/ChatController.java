package com.example.webrtc.chating.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	@MessageMapping("/chatroom/{roomId}/join")
	@SendTo("/topic/chatroom/{roomId}")
	public String chatRoomJoin(){

		return "{user} 님이 입장하였습니다";
	}
}
