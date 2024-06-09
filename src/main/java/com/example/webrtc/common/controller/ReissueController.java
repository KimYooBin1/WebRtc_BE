package com.example.webrtc.common.controller;

import static com.example.webrtc.common.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

import java.util.Date;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webrtc.common.entity.RefreshEntity;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.repository.RefreshRepository;
import com.example.webrtc.common.utils.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
// TODO : 지금 service와 controller 가 혼재되어 있는데, service와 controller를 분리하고, service에서 비즈니스 로직을 처리하도록 수정
public class ReissueController {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	/**
	 * refresh token을 이용하여 access token 재발급
	 */
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

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

		if (refresh == null) {
			//response status code
			throw new CustomException(REFRESH_TOKEN_NOT_FOUND_ERROR);
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			throw new CustomException(REFRESH_TOKEN_EXPIRED_ERROR);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {
			throw new CustomException(TOKEN_CATEGORY_NOT_MATCHED_ERROR);
		}

		//DB에 저장된 refresh token인지 확인
		if (!refreshRepository.existsByRefresh(refresh)) {
			throw new CustomException(REFRESH_TOKEN_DB_NOT_FOUND_ERROR);
		}
		String username = jwtUtil.getUsername(refresh);

		//make new JWT
		String access = jwtUtil.createJwt("access", username, 600000L);
		// refresh rotate(refresh token이 탈취될 경우를 대비하여 한번 사용하면 새로 발급)
		String newRefresh = jwtUtil.createJwt("refresh", username, 86400000L);

		//refresh token DB에 저장, 기존의 refresh token 삭제 후 새 refresh 토큰 저장
		refreshRepository.deleteByRefresh(refresh);
		addRefreshEntity(username, newRefresh, 86400000L);

		//response
		// TODO : Cookie로 값을 전송하도록 변경하던지 아니면 header로 값을 전송하도록 변경
		response.addHeader("Set-Cookie", createCookie("Authorization", access).toString());
		response.addHeader("Set-Cookie", createCookie("refresh", newRefresh).toString());

		return new ResponseEntity<>(OK);
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

	// TODO : setter 다 변경하기
	private void addRefreshEntity(String username, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		RefreshEntity refreshEntity = new RefreshEntity();
		refreshEntity.setUsername(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshRepository.save(refreshEntity);
	}
}
