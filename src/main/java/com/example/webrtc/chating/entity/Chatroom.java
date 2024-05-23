package com.example.webrtc.chating.entity;

import static lombok.AccessLevel.*;

import com.example.webrtc.common.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Chatroom {
	@Id
	@GeneratedValue
	private Long id;
	private String roomName;
	private Long limitUserCnt;
	private Long userCnt;


	public Chatroom(String roomName) {
		this.roomName = roomName;
		this.userCnt = 0L;
	}
	public void plus(){
		// TODO : 인원이 꽉차면?
		this.userCnt += 1;
	}

	public void des() {
		// TODO : 마이너스가 되면?
		this.userCnt -= 1;
	}
}
