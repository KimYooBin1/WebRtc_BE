package com.example.webrtc.streaming.service;

import static com.example.webrtc.chating.dto.ChatType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.entity.CreateRoom;
import com.example.webrtc.chating.repository.ChatroomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreamService {
	private final ChatroomRepository chatroomRepository;
	public List<Chatroom> findAllRoom(){
		return chatroomRepository.findAllByType(Streaming);
	}
	public Optional<Chatroom> findRoomById(Long id) {
		return chatroomRepository.findByIdAndType(id, Streaming);
	}

	@Transactional
	public Chatroom createChatRoom(CreateRoom request) {
		Chatroom chatroom = new Chatroom(request.getRoomName(), 2L, Streaming);
		return chatroomRepository.save(chatroom);
	}
}
