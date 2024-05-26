package com.example.webrtc.chating.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webrtc.chating.entity.Chatroom;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	// Optional<Chatroom> findByRoomName(String name);
	Chatroom findByRoomName(String name);
}
