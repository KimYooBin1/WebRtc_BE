package com.example.webrtc.common.entity;

import static lombok.AccessLevel.*;

import com.example.webrtc.chating.entity.Chatroom;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class User {
	@Id @GeneratedValue
	private Long id;
	private String username;
	private String name;
	// 인증을 위해서
	private String mobile;
	private String email;
	// private String ID;
	private String password;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "chatroom_id")
	private Chatroom chatroom;

	public User(String name, String password){
		this.name = name;
		this.password = password;
	}
	public User(String username, String name, String password, String email, String mobile) {
		this.username = username;
		this.name = name;
		this.mobile = mobile;
		this.email = email;
		this.password = password;
	}

	public User(String username, String name, String email, String mobile) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
	}
}
