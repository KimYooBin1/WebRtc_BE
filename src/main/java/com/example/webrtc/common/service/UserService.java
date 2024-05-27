package com.example.webrtc.common.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webrtc.common.dto.LoginDto;
import com.example.webrtc.common.dto.SignDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRespository;
	public User login(LoginDto request) throws Exception{
		// TODO : 후에 spring security 적용
		User user = userRespository.findByName(request.getName()).orElseThrow(
			() -> new RuntimeException("해당 name을 가진 유저는 없습니다")
		);
		if(!Objects.equals(user.getPassword(), request.getPassword())){
			throw new RuntimeException("password가 틀립니다");
		}
		return user;
	}
	@Transactional
	public User sign(SignDto request){
		if(userRespository.findByName(request.getName()).isPresent()){
			// TODO : 중복 체크 api 를 따로 만들지
			throw new RuntimeException("이미 존재하는 아이디 입니다");
		}
		User user = new User(request);
		userRespository.save(user);
		return user;
	}

}
