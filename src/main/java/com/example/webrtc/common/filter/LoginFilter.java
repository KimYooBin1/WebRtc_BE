package com.example.webrtc.common.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.webrtc.common.dto.PrincipalDetails;
import com.example.webrtc.common.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		//클라이언트 요청에서 username, password 추출
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		log.info("로그인 정보 : {} {}", username, password);

		//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
		//token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws
		IOException {
		PrincipalDetails customUserDetails = (PrincipalDetails) authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String token = jwtUtil.createJwt(username, 60*60*1000L);

		response.addHeader("Set-Cookie", createCookie("Authorization", token).toString());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// 응답 바디에 JSON 쓰기
		response.getWriter().write(username);
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		org.springframework.security.core.AuthenticationException failed) throws IOException, ServletException {
		// TODO : 실패했을때 로직 추가하기
		response.setStatus(401);
	}

	private ResponseCookie createCookie(String key, String value) {
		log.info("createCookie");
		return ResponseCookie.from(key, value)
			.maxAge(60*60*6000)
			.httpOnly(true)
			.secure(true)
			.sameSite("None")
			.path("/")
			.build();
	}

}
