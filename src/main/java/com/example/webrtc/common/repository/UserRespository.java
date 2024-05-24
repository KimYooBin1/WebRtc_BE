package com.example.webrtc.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webrtc.common.entity.User;

public interface UserRespository extends JpaRepository<User, Long> {
	public Optional<User> findByName(String name);
}
