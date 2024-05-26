package com.example.webrtc.chating.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.service.ChatroomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {
	private final ChatroomService chatroomService;

	// TODO : 나중에 pagination, search 등등 붙인다?
	@GetMapping("/chatroom")
	public ResponseEntity<List<Chatroom>> ChatRoomList(){
		return ResponseEntity.ok(chatroomService.findAllRoom());
	}

	@PostMapping("/chatroom")
	public ResponseEntity<Chatroom> ChatRoomCreate(@RequestBody CreateRoom request){
		return ResponseEntity.ok(chatroomService.createChatRoom(request));
	}

	@PostMapping("/chatroom/duplication")
	public ResponseEntity<Boolean> ChatRoomNameDupCheck(@RequestBody CreateRoom request){
		Chatroom room = chatroomService.findRoomByName(request.getRoomName());
		log.info("{}", request.getRoomName());
		if(room == null){
			// 중복되는 이름이 없음
			return ResponseEntity.ok(true);
		}
		else{
			//중복되는 이름이 있음(사용 불가)
			return ResponseEntity.ok(false);
		}
	}
}
