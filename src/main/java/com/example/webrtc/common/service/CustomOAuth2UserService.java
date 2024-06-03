package com.example.webrtc.common.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.webrtc.common.dto.CustomOAuth2User;
import com.example.webrtc.common.dto.NaverResponse;
import com.example.webrtc.common.dto.OAuth2Response;
import com.example.webrtc.common.dto.UserDto;
import com.example.webrtc.common.entity.User;
import com.example.webrtc.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("oAuth2User = {}", oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		log.info("registrationId = {}", registrationId);
		OAuth2Response oAuth2Response = null;
		if (registrationId.equals("naver")) {
			oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
		}
		else {
			return null;
		}
		String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
		User existData = userRepository.findByUsername(username).orElse(null);
		log.info("oAuth2Response.getName() = {}", oAuth2Response.getName());
		if(existData == null){
			User user = new User(username, oAuth2Response.getName(), oAuth2Response.getEmail());
			userRepository.save(user);
			UserDto userDTO = new UserDto(oAuth2Response.getName(), username);

			return new CustomOAuth2User(userDTO);
		}
		else{
			existData.setEmail(oAuth2Response.getEmail());
			existData.setName(oAuth2Response.getName());

			userRepository.save(existData);
			UserDto userDTO = new UserDto(oAuth2Response.getName(), username);

			return new CustomOAuth2User(userDTO);
		}
	}
}
