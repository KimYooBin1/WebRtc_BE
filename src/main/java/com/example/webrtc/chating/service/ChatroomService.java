package com.example.webrtc.chating.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.repository.ChatroomRepository;
import com.example.webrtc.common.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatroomService {
	private final ChatroomRepository chatroomRepository;
	public List<Chatroom> findAllRoom(){
		// TODO : 값이 비어있으면?
		return chatroomRepository.findAll();
	}

	public Chatroom findRoomById(Long id) {
		// TODO : 값이 비어있으면?
		return chatroomRepository.findById(id).orElseThrow();
	}

	public Chatroom findRoomByName(String name) {
		// TODO : 값이 비어있으면?
		return chatroomRepository.findByRoomName(name);
	}

	public List<User> findChatRoomUsers(Long roomId) {
		Chatroom chatroom = chatroomRepository.findById(roomId).orElseThrow(
			// TODO : user가 1명도 없다?
		);
		return chatroom.getUserList();
	}

	@Transactional
	public Chatroom createChatRoom(CreateRoom request) {
		// TODO : room 이름 중복 확인
		Chatroom chatroom;
		if(StringUtils.hasText(request.getPassword())){
			chatroom = new Chatroom(request.getRoomName(), request.getLimitUserCnt(), request.getPassword());
		}
		else{
			chatroom = new Chatroom(request.getRoomName(), request.getLimitUserCnt());
		}
		return chatroomRepository.save(chatroom);
	}
	@Transactional
	public void plusUserCnt(Long id) {
		chatroomRepository.findById(id).orElseThrow(

		).plus();
	}
	@Transactional
	public void desUserCnt(Long id){
		chatroomRepository.findById(id).orElseThrow(

		).des();
	}


}
