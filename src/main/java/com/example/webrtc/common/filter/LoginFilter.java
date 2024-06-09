package com.example.webrtc.common.filter;

import static com.example.webrtc.common.exception.ErrorCode.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.webrtc.common.entity.RefreshEntity;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.repository.RefreshRepository;
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
	private final RefreshRepository refreshRepository;
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
		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		//jwt token 만료시간은 10분
		String access = jwtUtil.createJwt("access", username, 60 * 10 * 1000L);
		//refresh token 만료시간은 24시간
		String refresh = jwtUtil.createJwt("refresh", username, 60 * 60 * 1000L * 24);
		//refresh token을 DB에 저장
		addRefreshEntity(username, refresh, 60 * 60 * 1000L * 24);


		response.addHeader("Set-Cookie", createCookie("Authorization", access).toString());
		response.addHeader("Set-Cookie", createCookie("refresh", refresh).toString());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		log.error("error = {}",failed.getMessage());
		throw new CustomException(CREDENTIALS_NOT_MATCHED_ERROR);
	}

	private ResponseCookie createCookie(String key, String value) {
		log.info("createCookie");
		return ResponseCookie.from(key, value)
			.maxAge(60*30)
			.httpOnly(false)
			.secure(true)
			.sameSite("None")
			.path("/")
			.build();
	}
	private void addRefreshEntity(String username, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		RefreshEntity refreshEntity = new RefreshEntity();
		refreshEntity.setUsername(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshRepository.save(refreshEntity);
	}
}
