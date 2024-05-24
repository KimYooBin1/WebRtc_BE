package com.example.webrtc.chating.entity;

import lombok.Data;

@Data
public class CreateRoom {
	private String roomName;
	private Long limitUserCnt;
	private String password;
}
