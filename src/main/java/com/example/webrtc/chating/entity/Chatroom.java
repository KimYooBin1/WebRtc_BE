package com.example.webrtc.chating.entity;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import com.example.webrtc.common.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Chatroom {
	@Id
	@GeneratedValue
	private Long id;
	private String roomName;
	private Long limitUserCnt;
	private Long userCnt;
	private String password;
	@JsonIgnore
	@OneToMany(mappedBy = "chatroom")
	private List<User> userList = new ArrayList<>();

	public void plus(){
		// TODO : 인원이 꽉차면?
		this.userCnt += 1;
	}

	public void des() {
		// TODO : 마이너스가 되면?
		this.userCnt -= 1;
	}
	// 비밀번호가 걸린 방 생성
	public Chatroom(String roomName, Long limitUserCnt, String password) {
		this.roomName = roomName;
		this.limitUserCnt = limitUserCnt;
		this.password = password;
		this.userCnt = 1L;
	}
	// 비밀번호가 없는 방 생성
	public Chatroom(String roomName, Long limitUserCnt) {
		this.roomName = roomName;
		this.limitUserCnt = limitUserCnt;
		this.userCnt = 1L;
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
