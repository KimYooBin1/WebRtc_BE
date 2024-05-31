package com.example.webrtc.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webrtc.common.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByName(String name);
	User findByUsername(String username);
	Boolean existsByName(String name);
}
