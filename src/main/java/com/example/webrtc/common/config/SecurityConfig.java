package com.example.webrtc.common.config;

import static org.springframework.http.HttpMethod.*;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.webrtc.common.filter.CustomLogoutFilter;
import com.example.webrtc.common.filter.JWTFilter;
import com.example.webrtc.common.filter.LoginFilter;
import com.example.webrtc.common.repository.RefreshRepository;
import com.example.webrtc.common.service.CustomOAuth2UserService;
import com.example.webrtc.common.utils.CustomSuccessHandler;
import com.example.webrtc.common.utils.JWTUtil;
import com.example.webrtc.common.utils.JwtAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final JWTUtil jwtUtil;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final RefreshRepository refreshRepository;
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
		http
			.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
					configuration.setAllowedMethods(Collections.singletonList("*"));
					configuration.setAllowCredentials(true);
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setMaxAge(3600L);

					configuration.setExposedHeaders(Collections.singletonList("Authorization"));

					return configuration;
				}
			})));
		//csrf disable
		http
			.csrf(AbstractHttpConfigurer::disable);

		//From 로그인 방식 disable
		http
			.formLogin(AbstractHttpConfigurer::disable);

		//http basic 인증 방식 disable
		http
			.httpBasic(AbstractHttpConfigurer::disable);
		//security 내부의 exception 처리를 위해 custom jwtAuthenticationEntryPoint 등록
		http
			.exceptionHandling((exception) -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint));
				// .accessDeniedHandler(jwtAccessDeniedHandler));

		//oath2
		http
			.oauth2Login((auth) -> auth
				.successHandler(customSuccessHandler)
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(customOAuth2UserService)));
		//경로별 인가 작업
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/websocket/**", "/webrtc/**").permitAll()
				.requestMatchers("/user/sign", "/reissue", "/login", "/logout").permitAll()
				.requestMatchers(GET, "/chatroom").permitAll()
				.requestMatchers(GET, "/stream/**").permitAll()
				.anyRequest().authenticated());

		//JWTFilter 등록
		http
			.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository),
				UsernamePasswordAuthenticationFilter.class);
		//로그아웃 설정
		http
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
		//세션 설정
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
