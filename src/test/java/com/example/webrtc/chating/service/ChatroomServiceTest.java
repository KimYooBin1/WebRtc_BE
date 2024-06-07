package com.example.webrtc.chating.service;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.webrtc.common.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test")
class ChatroomServiceTest {
	@Autowired
	ChatroomService chatroomService;
	@Autowired
	UserService userService;
	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("채팅방 생성")
	void 채팅방_생성() throws Exception {
		mockMvc.perform(post("/chatroom")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"roomName\":\"test\",\"limitUserCnt\":10,\"password\":\"1234\"}"))
				.andExpect(status().isOk());
	}
}
