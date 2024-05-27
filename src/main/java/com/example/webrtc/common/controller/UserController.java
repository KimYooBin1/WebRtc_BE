package com.example.webrtc.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.webrtc.common.dto.LoginDto;
import com.example.webrtc.common.dto.SignDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/user/login")
	public ResponseEntity<User> login(@RequestBody LoginDto request) throws Exception{
		// TODO : 후에 spring security 적용
		return ResponseEntity.ok(userService.login(request));
	}

	@PostMapping("/user/sign")
	public ResponseEntity<User> sign(@RequestBody SignDto request){
		return ResponseEntity.ok(userService.sign(request));
	}
}
