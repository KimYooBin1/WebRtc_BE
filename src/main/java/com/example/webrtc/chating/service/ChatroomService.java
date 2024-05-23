package com.example.webrtc.chating.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.repository.ChatroomRepository;

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
		return chatroomRepository.findByRoomName(name).orElseThrow();
	}

	public Chatroom createChatRoom(String name) {
		return chatroomRepository.save(new Chatroom(name));
	}

	public void plusUserCnt(Long id) {
		chatroomRepository.findById(id).orElseThrow(

		).plus();
	}
	public void desUserCnt(Long id){
		chatroomRepository.findById(id).orElseThrow(

		).des();
	}
}
