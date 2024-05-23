package com.example.webrtc.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.webrtc.chating.entity.Chatroom;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class User {
	@Id @GeneratedValue
	private Long id;
	private String name;
	// private String ID;
	private String password;

}
