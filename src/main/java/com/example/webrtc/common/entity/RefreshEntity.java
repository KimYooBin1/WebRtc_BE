package com.example.webrtc.common.entity;

import static jakarta.persistence.GenerationType.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
// TODO : redis를 사용하면 삭제가 편해진다
public class RefreshEntity {
	@Id @GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String username;
	private String refresh;
	// 토큰이 만료되는 시간
	private String expiration;
}
