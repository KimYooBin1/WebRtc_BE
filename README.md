# spring webrtc chatting, streaming project

## 1. chatting(진행중)
spring websocket과 stomp를 사용해서 chatting service 구현
### 구현한 기능
| 기능 설명 | 스크린샷 |
| -------- | -------- |
| 현재 채팅방 목록 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/6c7735c7-0722-4788-8c07-22bac6ad4c69) |
| 현재 방에 있는 유저 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/756907e8-cb38-4079-8657-a2e311f5caa7) |
| 유저가 들어올 때 채팅방에 해당 정보 표시 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/c6682e2d-0c55-4419-af1e-ba5040d07dc4) |
| 채팅 보내기 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/6970f7fd-6fbf-45ca-ab77-7e6fca9a59ac) |
| 채팅방 정보 설정 (채팅방 이름, 제한 인원, 비밀번호) | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/553f93b1-17fc-45e9-97eb-8a1ecacc88e7) |
| 채팅방 중복 이름 체크 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/055ff339-95d1-473a-9a82-9bf5ba7966c5) |
| 로그인(기본) | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/73d56755-d1f4-41e7-9cfc-f9529864e41e) |
| 인원이 방에서 나갔을 떄 표시 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/ed699ff6-0f9a-4d50-bb1e-418277c45cd4) |
| 비어있는 방 삭제 | spring batch를 사용해 30분 주기로 참여 인원이 0명인 방 삭제 |
| 방 비밀번호 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/1bc197d3-6995-46dc-8a62-28e38ee8fb8f) ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/433336c4-7ccb-4c99-8867-4ea4b62cc780) |
| 인원 다 찼을때 | ![image](https://github.com/KimYooBin1/WebRtc_BE/assets/55120730/47f668fb-afaa-4f6b-a12b-50583c4ed8cf) |
| 로그인(oauth2) | oauth2 + JWT + cookie를 활용해 구현 |

### 구현할 기능
| 기능 설명 | 스크린샷 |
| -------- | -------- |
| 채팅방 사진, 파일 전송 | aws s3 활용해 구현 |


## 2. streaming
webrtc를 사용해 streaming service 구현.

### 구현할 기능
| 기능 설명 | 스크린샷 |
| ------- | ----- |
| 1 대 1 화상통화 | |
| 얼굴 인식 -> 가상 캐릭터로 변환 | |
| 실제 배포 | |
| ui깔끔하게 | |

## 3. 사용 기술
| 역할            | 종류
| -------------- | ----------------
| framwork       |  <img alt="RED" src ="https://img.shields.io/badge/SPRING Boot-6DB33F.svg?&style=for-the-badge&logo=SpringBoot&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Spring Security-6DB33F.svg?&style=for-the-badge&logo=springsecurity&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/React-2361DAFB?style=for-the-badge&logo=React&logoColor=white" /> |
| database       | <img alt="RED" src ="https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white"/> |
| deploy         | <img alt="RED" src ="https://img.shields.io/badge/webrtc-333333?style=for-the-badge&logo=webrtc" /> <img alt="RED" src ="https://img.shields.io/badge/Nginx-009639.svg?&style=for-the-badge&logo=nginx&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=docker&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Amazon EC2-FF9900.svg?&style=for-the-badge&logo=AmazonEC2&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Amazon Rds-527FFF.svg?&style=for-the-badge&logo=AmazonRds&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Amazon S3-569A31.svg?&style=for-the-badge&logo=AmazonS3&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Amazon Route 53-8C4FFF.svg?&style=for-the-badge&logo=Amazon Route 53&logoColor=white"/> <img alt="RED" src ="https://img.shields.io/badge/Certbot-FF1E0D.svg?&style=for-the-badge&logo=Certbot&logoColor=white"/> |                   
| version control|  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">   |
