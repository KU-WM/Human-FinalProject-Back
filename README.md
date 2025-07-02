# Final Project
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![Springboot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Mysql](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)

## 📌 목차
- [📝 소개](#-소개)
- [🗄️ 데이터 베이스 구조](#-데이터-베이스-구조)
- [🧩 SpringBoot 구조](#-springBoot-구조)
- [📁 전체 프로젝트 구성 트리](#-전체-프로젝트-구성-트리)
- [🧰 사용 기술 스택](#-사용-기술-스택)
- [📄 License](#-license)

## 📝 소개
<img src="https://github.com/user-attachments/assets/85cc6a6d-3c04-41d2-b8ea-1ad42b39f49a" width="775px" />

Gemini API와 Stable-Audio 1.0 모델을 활용하여 **사용자가 간단한 텍스트 입력만으로 원하는 이미지와 간단한 효과음을 생성할 수 있는 사이트**를 풀스택으로 개발하고 서비스 하였습니다. 이 레포지토리는 해당 서비스의 BackEnd 코드 입니다.<br>
[https://lnpra.com](https://lnpra.com) 에 접속하시면 해당 서비스를 사용하실 수 있습니다.<br>

프로젝트의 유지보수를 위하여 개인 프로젝트를 아래 3개의 레포지토리로 분리하여 저장하였습니다.<br>

[FrontEnd]<br>
https://github.com/KU-WM/Human-FinalProject-Front<br>
[BackEnd] - 현재 페이지<br>
https://github.com/KU-WM/Human-FinalProject-Back.git<br>
[Api Server]<br>
https://github.com/KU-WM/Human-FinalProject-API.git<br>
<br>

## 🗄️ 데이터 베이스 구조
<img width="653" alt="스크린샷 2025-07-01 104459" src="https://github.com/user-attachments/assets/9738a38c-1fc8-42f6-824c-2b4cd3378143" />

|테이블명|기능|
|---|---|
|accesslog|모든 접속자의 접근 로그를 기록|
|tempImages|로그인 하지 않은 사용자가 생성한 이미지를 저장|
|tempAudios|로그인 하지 않은 사용자가 생성한 오디오를 저장|
|Users|유저 정보를 저장|
|Images|로그인 한 유저가 생성한 이미지를 저장|
|Audios|로그인 한 유저가 생성한 오디오를 저장|
|uuidToUser|유저의 아이디 정보와 구분용 uuid의 매칭정보 저장|

## 🧩 SpringBoot 구조

|MVC 패턴|기능|
|---|---|
|config|로그인, Cors등의 설정|
|controller|백엔드의 실질적인 기능들|
|dto|DB의 정보를 받아오기 위한 데이터 형식 정의|
|mapper|DB의 정보를 제어하는 기능|
|properties|환경변수를 불러와 실제 사용할 수 있도록 선언|
|repository|Logs와 같이 한번에 수백개의 Insert가 일어나는 경우 Batch 단위로 DB와 통신|
|service|실질적으로 실행되는 서비스의 동작|

- Spring Security
    - Spring Security를 통한 url 접근 권한을 설정합니다.
- 환경변수 설정
    - Api키와 같은 중요 요소를 환경 변수로 처리하여 보안 처리를 진행하였습니다.
- 로그인 보안처리
    - JWT Token을 발급하여(지속시간 3분) 최소한의 유지시간으로 해킹 피해를 방지하였습니다.
    - Refresh Token을 발급하여 (지속시간 7일) 자동 로그인 및 https only 쿠키 저장방식으로 보안성을 향상하였습니다.
- 외부 Api 처리방식
    - webClient를 사용하여 모든 요청을 비동기 방식으로 처리하였습니다.

## 🛠 개발 과정
- 2025.05.26 ~ 2025.06.26 (약 5주) 의 기간동안 진행하였습니다.
- 오전/오후의 스크럼 회의를 통해 진행사항을 점검하고, 애자일 방법론을 통하여 유연한 개발을 진행하였습니다.

## 📁 전체 프로젝트 구성 트리
```
📦Human-FinalProject-Back  
┣ 📂.gradle  
┣ 📂.idea  
┣ 📂build  
┣ 📂gradle  
┗ 📂src  
   ┣ 📂main  
   ┃ ┣ 📂java  
   ┃ ┃ ┗ 📂com  
   ┃ ┃    ┗ 📂backend  
   ┃ ┃       ┗ 📂back  
   ┃ ┃          ┣ 📜BackApplication.java  
   ┃ ┃          ┣ 📂config  - Spring Security 설정 / Cors 방지 설정
   ┃ ┃          ┃ ┣ 📜CustomUserDetailsService.java  
   ┃ ┃          ┃ ┣ 📜JwtAuthenticationFilter.java  
   ┃ ┃          ┃ ┣ 📜JwtUtil.java  
   ┃ ┃          ┃ ┣ 📜LoginSuccessHandler.java  
   ┃ ┃          ┃ ┣ 📜SecurityConfig.java  
   ┃ ┃          ┃ ┗ 📜WebConfig.java  
   ┃ ┃          ┣ 📂controller  - 각종 기능들 구현
   ┃ ┃          ┃ ┣ 📜AdminController.java  
   ┃ ┃          ┃ ┣ 📜AudioController.java  
   ┃ ┃          ┃ ┣ 📜FileController.java  
   ┃ ┃          ┃ ┣ 📜ImageController.java  
   ┃ ┃          ┃ ┣ 📜LogController.java  
   ┃ ┃          ┃ ┣ 📜PageController.java  
   ┃ ┃          ┃ ┣ 📜RefreshTokenController.java  
   ┃ ┃          ┃ ┣ 📜StatisticController.java  
   ┃ ┃          ┃ ┗ 📜UserController.java  
   ┃ ┃          ┣ 📂dto  - DB과의 통신을 위한 객체 정의
   ┃ ┃          ┃ ┣ 📜AccessLogDTO.java  
   ┃ ┃          ┃ ┣ 📜AudioDTO.java  
   ┃ ┃          ┃ ┣ 📜FilePath.java  
   ┃ ┃          ┃ ┣ 📜ImageDTO.java  
   ┃ ┃          ┃ ┣ 📜LogDTO.java  
   ┃ ┃          ┃ ┣ 📜PageDTO.java  
   ┃ ┃          ┃ ┣ 📜UserDTO.java  
   ┃ ┃          ┃ ┣ 📜UserGrade.java  
   ┃ ┃          ┃ ┗ 📜UuidToUserDTO.java  
   ┃ ┃          ┣ 📂mapper  - DB와의 통신 함수 정의
   ┃ ┃          ┃ ┣ 📜AdminMapper.java  
   ┃ ┃          ┃ ┣ 📜AudioMapper.java  
   ┃ ┃          ┃ ┣ 📜ImageMapper.java  
   ┃ ┃          ┃ ┣ 📜StatisticMapper.java  
   ┃ ┃          ┃ ┗ 📜UserMapper.java  
   ┃ ┃          ┣ 📂properties  - application.properties의 변수 불러오기
   ┃ ┃          ┃ ┣ 📜GeminiProperties.java  
   ┃ ┃          ┃ ┣ 📜GemmaProperties.java  
   ┃ ┃          ┃ ┗ 📜JwtProperties.java  
   ┃ ┃          ┣ 📂repository  - Logs와 같이 대량 입출력 batch로 처리
   ┃ ┃          ┃ ┗ 📜AccessLogsRepository.java  
   ┃ ┃          ┗ 📂service  - 사용자 요청시 실질적으로 처리하는 로직
   ┃ ┃             ┣ 📜DeleteFileService.java  
   ┃ ┃             ┣ 📜GenerateAudioService.java  
   ┃ ┃             ┣ 📜GenerateImageService.java  
   ┃ ┃             ┣ 📜LoggingService.java  
   ┃ ┃             ┗ 📜Temp2UserService.java  
   ┃ ┗ 📂resources  
   ┃    ┗ 📜application.properties  
   ┗ 📂test  
     ┗ 📂java  
       ┗ 📂com  
         ┗ 📂backend  
           ┗ 📂back  
             ┗ 📜BackApplicationTests.java  
```

## 🧰 사용 기술 스택

| 분류 | 기술 |
|------|------|
| Frontend | React, JavaScript, HTML5, CSS3, Bootstrap |
| Backend | SpringBoot, FastApi |
| Infra | Nginx, Cloudflare tunnel |
| AI 모델 | Gemini API (이미지 생성), Stable Audio 1.0 (효과음 생성) |

## 📄 License
본 프로젝트의 코드는 비상업적 용도로 자유롭게 사용하실 수 있습니다.
상업적 이용이나 수정, 재배포 시에는 사전 연락을 부탁드립니다.
