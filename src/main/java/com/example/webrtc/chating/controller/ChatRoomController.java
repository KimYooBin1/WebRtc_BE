package com.example.webrtc.chating.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.repository.ChatroomRepository;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {
	private final ChatroomRepository chatroomRepository;

	// TODO : 나중에 pagination, search 등등 붙인다?
	@GetMapping("/")
	public List<Chatroom> ChatRoomList(){
		return chatroomRepository.findAll();
	}
}
