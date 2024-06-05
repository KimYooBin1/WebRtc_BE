package com.example.webrtc.common.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.webrtc.common.dto.PrincipalDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(JWTUtil jwtUtil) {

		this.jwtUtil = jwtUtil;
	}

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

		String token = jwtUtil.createJwt(username, 60*60*6000L);

		response.addHeader("Set-Cookie", createCookie(token).toString());
		response.sendRedirect("http://localhost:3000/loginSuccess");
	}

	private ResponseCookie createCookie(String value) {
		return ResponseCookie.from("Authorization", value)
			.maxAge(60*60*6000)
			.httpOnly(true)
			.secure(true)
			.sameSite("None")
			.path("/")
			.build();
	}
}
