package com.example.webrtc.common.controller;

import static com.example.webrtc.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.webrtc.common.dto.LoginDto;
import com.example.webrtc.common.dto.PrincipalDetails;
import com.example.webrtc.common.dto.SignDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	/**
	 * spring security를 사용해 더이상 사용되지 않는 메소드
	 */
	@PostMapping("/user/login")
	public ResponseEntity<User> login(@RequestBody LoginDto request) throws Exception{
		return ResponseEntity.ok(userService.login(request));
	}

	@PostMapping("/user/sign")
	public ResponseEntity<User> sign(@RequestBody SignDto request){
		return ResponseEntity.ok(userService.sign(request));
	}

	@GetMapping("/test")
	public ResponseEntity<List<User>> test(){
		throw new CustomException(INVALID_TOKEN_ERROR);
	}

	@GetMapping("/user")
	public ResponseEntity<User> user(@AuthenticationPrincipal PrincipalDetails user) {
		log.info("principalUserName = {}", user);
		if(user == null){
			throw new CustomException(USERNAME_NOT_FOUND_ERROR);
		}
		User result = userService.findUserByName(user.getUsername());
		log.info("result = {}", result);
		return ResponseEntity.ok(result);
	}
}
