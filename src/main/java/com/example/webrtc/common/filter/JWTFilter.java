package com.example.webrtc.common.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.webrtc.common.dto.PrincipalDetails;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,
		IOException {
		//cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
		String token = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies) {

				System.out.println(cookie.getName());
				if (cookie.getName().equals("Authorization")) {

					token = cookie.getValue();
				}
			}
		}
		//Authorization 헤더 검증
		if (token == null) {

			System.out.println("token null");
			filterChain.doFilter(request, response);

			//조건이 해당되면 메소드 종료 (필수)
			return;
		}

		System.out.println("authorization now");
		//Bearer 부분 제거 후 순수 토큰만 획득

		//토큰 소멸 시간 검증
		if (jwtUtil.isExpired(token)) {
			// TODO : exception 발생
			System.out.println("token expired");
			filterChain.doFilter(request, response);
			//조건이 해당되면 메소드 종료 (필수)
			return;
		}

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(token);
		log.info("username == {}", username);
		Authentication authToken = null;
		// oauth2 로그인인지 일반 로그인인지 판별
		if(username.startsWith("naver")){
			//userDTO를 생성하여 값 set
			log.info("naver login check");
			// 해당 username을 가진 임의의 객체 생성
			User user = new User(username,"", "", "");
			PrincipalDetails principalDetails = new PrincipalDetails(user);
			//스프링 시큐리티 인증 토큰 생성
			authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
		}
		else{
			//userEntity를 생성하여 값 set
			User user = new User(username, "");
			// user.setName(username);
			// user.setPassword("temppassword");

			//UserDetails에 회원 정보 객체 담기
			PrincipalDetails customUserDetails = new PrincipalDetails(user);

			//스프링 시큐리티 인증 토큰 생성
			authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		}
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
