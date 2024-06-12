package com.example.webrtc.streaming.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.streaming.service.StreamService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StreamController {
	private final StreamService streamService;

	@GetMapping("/stream")
	public ResponseEntity<List<Chatroom>> stream(){
		return ResponseEntity.ok(streamService.findAllRoom());
	}

	@PostMapping("/stream")
	public ResponseEntity<Chatroom> streamCreate(@RequestBody CreateRoom request){
		return ResponseEntity.ok(streamService.createRoom(request));
	}
}
