package com.example.webrtc.common.service;

import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webrtc.common.dto.LoginDto;
import com.example.webrtc.common.dto.PrincipalDetails;
import com.example.webrtc.common.dto.SignDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	public User login(LoginDto request) throws Exception{
		// TODO : 후에 spring security 적용
		User user = userRepository.findByUsername((request.getUsername())).orElseThrow(
			() -> new RuntimeException("존재하지 않는 아이디 입니다")
		);
		if(!Objects.equals(user.getPassword(), request.getPassword())){
			throw new RuntimeException("password가 틀립니다");
		}
		return user;
	}
	@Transactional
	public User sign(SignDto request){
		if(userRepository.existsByName(request.getName())){
			// TODO : 중복 체크 api 를 따로 만들지
			throw new RuntimeException("이미 존재하는 아이디 입니다");
		}

		return userRepository.save(
			new User(request.getUsername(), request.getName(), passwordEncoder.encode(request.getPassword()),
				request.getEmail(),
				request.getPhoneNumber()));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("UserDetails username = {}", username);
		//DB에서 조회
		User user = userRepository.findByUsername((username)).orElse(null);
		if (user != null) {
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
			return new PrincipalDetails(user);
		}
		return null;
	}
}
