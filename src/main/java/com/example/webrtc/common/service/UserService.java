package com.example.webrtc.common.service;

import static com.example.webrtc.common.exception.ErrorCode.*;

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
import com.example.webrtc.common.exception.CustomException;
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

	/**
	 *	spring security를 사용해 더이상 사용되지 않는 로그인 api
	 */
	public User login(LoginDto request) throws Exception{
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
			throw new CustomException(ALREADY_EXIST_USER_ERROR);
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
		User user = userRepository.findByUsername((username)).orElseThrow(
			() -> {
				log.error("존재하지 않는 아이디 입니다");
				return new UsernameNotFoundException("존재하지 않는 아이디 입니다");
			}
		);
		if (user != null) {
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
			return new PrincipalDetails(user);
		}
		return null;
	}

	public User findUserById(Long id){
		return userRepository.findById(id).orElseThrow(
			() -> {
				log.error("존재하지 않는 아이디 입니다");
				return new CustomException(NOT_FOUND_USER_ERROR);
			}
		);
	}
	public User findUserByName(String name){
		return userRepository.findByUsername(name).orElseThrow(
			() -> {
				log.error("존재하지 않는 이름 입니다");
				return new CustomException(NOT_FOUND_USER_ERROR);
			}
		);
	}
}
