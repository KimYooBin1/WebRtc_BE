package com.example.webrtc.common.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.webrtc.common.dto.LoginDto;
import com.example.webrtc.common.dto.SignDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.exception.ErrorCode;
import com.example.webrtc.common.repository.UserRepository;
import com.example.webrtc.common.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserRepository userRepository;

	@PostMapping("/user/login")
	public ResponseEntity<User> login(@RequestBody LoginDto request) throws Exception{
		// TODO : 후에 spring security 적용
		return ResponseEntity.ok(userService.login(request));
	}

	@PostMapping("/user/sign")
	public ResponseEntity<User> sign(@RequestBody SignDto request){
		return ResponseEntity.ok(userService.sign(request));
	}

	@GetMapping("/test")
	public ResponseEntity<List<User>> test(){
		List<User> all = userRepository.findAll();
		if(!all.isEmpty()){
			throw new CustomException(ErrorCode.INVALID_TOKEN_ERROR);
		}
		return ResponseEntity.ok(all);
	}
}
