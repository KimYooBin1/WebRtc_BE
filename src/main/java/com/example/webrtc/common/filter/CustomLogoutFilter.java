package com.example.webrtc.common.filter;

import static com.example.webrtc.common.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.example.webrtc.common.repository.RefreshRepository;
import com.example.webrtc.common.utils.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
	// TODO : CustomException 사용 못하는지
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {
		//path and method verify

		// '/logout' 이외의 요청은 통과
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/logout$")) {

			filterChain.doFilter(request, response);
			return;
		}
		// '/logout' 요청 중 POST가 아닌 요청은 통과
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					refresh = cookie.getValue();
				}
			}
		}

		//refresh null check
		if (refresh == null) {
			// throw new CustomException(ALREADY_LOGOUT_ERROR);
			response.setStatus(SC_BAD_REQUEST);
			return;
		}

		//expired check, 만료가 되었다면 로그아웃된 상태
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			// throw new CustomException(ALREADY_LOGOUT_ERROR);
			//response status code
			response.setStatus(SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);
		if (!category.equals("refresh")) {
			// throw new CustomException(TOKEN_CATEGORY_NOT_MATCHED_ERROR);
			//response status code
			response.setStatus(SC_BAD_REQUEST);
			return;
		}

		//DB에 저장되어 있는지 확인, 없으면 이미 로그아웃된 상태
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if (!isExist) {
			// throw new CustomException(ALREADY_LOGOUT_ERROR);
			//response status code
			response.setStatus(SC_BAD_REQUEST);
			return;
		}

		//로그아웃 진행
		//Refresh 토큰 DB에서 제거
		refreshRepository.deleteByRefresh(refresh);

		//Refresh 토큰 Cookie 값 0
		Cookie cookie = new Cookie("refresh", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");

		response.addCookie(cookie);
		response.setStatus(SC_OK);
	}
}
