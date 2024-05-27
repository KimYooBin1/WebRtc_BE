package com.example.webrtc.common.entity;

import static lombok.AccessLevel.*;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.common.dto.SignDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
public class User {
	@Id @GeneratedValue
	private Long id;
	private String name;
	// 인증을 위해서
	private String phoneNum;
	private String email;
	// private String ID;
	private String password;
	@ManyToOne
	@JoinColumn(name = "chatroom_id")
	private Chatroom chatroom;

	public User(SignDto request) {
		this.name = request.getName();
		this.phoneNum = request.getPhoneNumber();
		this.email = request.getEmail();
		this.password = request.getPassword();
	}
}
