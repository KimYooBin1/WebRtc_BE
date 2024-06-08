package com.example.webrtc.common.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	//handlerExceptionResolver의 빈종류가 두 종류 있기때문에 @Qualifier를 사용하여 명시적으로 빈을 선택
	private final HandlerExceptionResolver resolver;
	public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws
		IOException {
		log.error("JwtAuthenticationEntryPoint 진입 = {}", request.getAttribute("exception").getClass());
		// JWTFilter에서 발생한 예외를 처리하기 위해 resolveException 메소드를 사용
		resolver.resolveException(request, response, null, (Exception)request.getAttribute("exception"));
	}
}
