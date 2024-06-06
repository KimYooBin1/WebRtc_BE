package com.example.webrtc.chating.service;

import static com.example.webrtc.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.repository.ChatroomRepository;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.exception.CustomException;
import com.example.webrtc.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatroomService {
	private final ChatroomRepository chatroomRepository;
	public List<Chatroom> findAllRoom(){
		return chatroomRepository.findAll();
	}

	public Chatroom findRoomById(Long id) {
		return chatroomRepository.findById(id).orElseThrow(
			() ->{
				log.error("해당 id의 chatroom이 없습니다");
				return new CustomException(CHAT_ROOM_NOT_FOUND_ERROR);
			}
		);
	}
	public Chatroom findRoomByName(String name) {
		return chatroomRepository.findByRoomName(name).orElse(null);
	}

	public List<User> findChatRoomUsers(Long roomId) {
		Chatroom chatroom = chatroomRepository.findById(roomId).orElse(null);
		return chatroom != null ? chatroom.getUserList() : null;
	}

	@Transactional
	public Chatroom createChatRoom(CreateRoom request) {
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

	@Transactional
	public void deleteChatRoom(Long id){
		chatroomRepository.deleteById(id);
	}
}
