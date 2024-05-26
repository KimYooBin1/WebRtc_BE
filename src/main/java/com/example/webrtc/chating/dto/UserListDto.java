package com.example.webrtc.chating.dto;

import lombok.Data;

@Data
public class UserListDto {
	private String name;

	public UserListDto(String name) {
		this.name = name;
	}
}
