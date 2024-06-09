package com.example.webrtc.common.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.webrtc.common.dto.PrincipalDetails;
import com.example.webrtc.common.entity.RefreshEntity;
import com.example.webrtc.common.repository.RefreshRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException, ServletException {

		//OAuth2User
		PrincipalDetails customUserDetails = (PrincipalDetails) authentication.getPrincipal();

		String username = customUserDetails.getUsername();
		log.info("jwt username = {}", username);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		//jwt token 만료시간은 10분
		String access = jwtUtil.createJwt("access", username, 60 * 10 * 1000L);
		//refresh token 만료시간은 24시간
		String refresh = jwtUtil.createJwt("refresh", username, 60 * 60 * 1000L * 24);
		//refresh token을 DB에 저장
		addRefreshEntity(username, refresh, 60 * 60 * 1000L * 24);

		response.addHeader("Set-Cookie", createCookie("Authorization", access).toString());
		response.addHeader("Set-Cookie", createCookie("refresh", refresh).toString());
		response.sendRedirect("http://localhost:3000/loginSuccess");
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
