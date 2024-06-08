package com.example.webrtc.common.utils;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	// TODO : 인가 도중 발생하는 exception에 대해서는 여기서 처리를 한다는데 왜 작동을 안하는지...?
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.error("JwtAccessDeniedHandler 진입 = {}", accessDeniedException.getMessage());
		response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
	}
}
