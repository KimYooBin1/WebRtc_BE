package com.example.webrtc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsMvcConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
			.exposedHeaders("Set-Cookie")
			.allowedOrigins("http://localhost:3000");
	}
}
