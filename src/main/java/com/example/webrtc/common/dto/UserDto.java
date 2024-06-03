package com.example.webrtc.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
	private String name;
	private String username;
	private String role;

	public UserDto(String name, String username) {
		this.name = name;
		this.username = username;
		this.role = "ROLE_USER";
	}
}
