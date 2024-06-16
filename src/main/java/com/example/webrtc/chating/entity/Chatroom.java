package com.example.webrtc.chating.entity;

import static jakarta.persistence.EnumType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import com.example.webrtc.chating.dto.ChatType;
import com.example.webrtc.common.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class Chatroom {
	@Id
	@GeneratedValue
	private Long id;
	private String roomName;
	private Long limitUserCnt;
	private Long userCnt;
	private String password;
	@Enumerated(STRING)
	private ChatType type;
	@JsonIgnore
	@OneToMany(mappedBy = "chatroom")
	private List<User> userList = new ArrayList<>();

	public void plus(){
		this.userCnt += 1;
	}

	public void des() {
		this.userCnt -= 1;
		if(this.userCnt < 0){
			this.userCnt = 0L;
		}
	}
	// 비밀번호가 걸린 방 생성
	public Chatroom(String roomName, Long limitUserCnt, String password, ChatType type) {
		this.roomName = roomName;
		this.limitUserCnt = limitUserCnt;
		this.password = password;
		this.userCnt = 0L;
		this.type = type;
	}
	// 비밀번호가 없는 방 생성
	public Chatroom(String roomName, Long limitUserCnt, ChatType type) {
		this.roomName = roomName;
		this.limitUserCnt = limitUserCnt;
		this.userCnt = 0L;
		this.type = type;
	}

	public void connectUser(User user) {
		this.userList.add(user);
		user.setChatroom(this);
	}
	public void disconnectUser(User user){
		this.userList.remove(user);
		user.setChatroom(null);
	}
}
