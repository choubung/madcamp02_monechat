# Madcamp Week 2. MoneChat(먼챗)

너 또 돈썼니...?
<br/>
<br/>

## 프로젝트 소개

MoneChat은 사람들의 소비를 다시 한번 생각하게 해주는 거지방을 편하게 세팅해주는 앱입니다.

친구들을 초대해 함께 가계부를 쓰고 소비 내역을 공유하며 자신의 소비가 적절한지를 컨펌을 받으며 재고해보세요!

당신의 소비는 확실히 줄어들 것입니다...
<br/>
<br/>

## 개발환경

FrontEnd:
  - Tool: JAVA
  - IDE: Android Studio

BackEnd:
  - Tool: Node.js
  - Server: AWS EC2
  - DB: MongoDB Atlas

Design: Figma
<br/>
<br/>

## 팀원 소개

- 김원중(KAIST 전산학부 20): [github 프로필](https://github.com/wjhjkim)
- 이수연(숙명여대 컴퓨터과학전공 21): [github 프로필](https://github.com/choubung)
<br/>
<br/>

## 프로젝트 구조

### 0. 카카오 로그인 페이지

- 앱을 처음 시작할 때 로고가 짧게 떠오릅니다.
- 카카오로 로그인하기 버튼을 통해 앱에 로그인할 수 있습니다.
<img src= https://github.com/choubung/madcamp02/assets/96229091/2a4e1c2f-2316-43ee-8661-d0643a7ab6ed width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/eb14d6cf-9251-4d5e-9a1c-27137d709c5a width="200" height="400"/>
<br/>

### 1. 가계부 페이지

- 가계부 페이지에서 자신의 지출과 수입을 월별로 구분해서 RecyclerView로 제공합니다.
<img src= https://github.com/choubung/madcamp02/assets/96229091/878e4316-942b-4fe5-b66d-77267863cdc6 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/283784f0-5f84-4d92-a6c0-30a9865ea85f width="200" height="400"/>
<br/>
- 각 내역을 클릭 시 세부 내역을 확인할 수 있습니다.
<br/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/342737aa-48e2-49bb-a4c4-2e767bac0377 width="200" height="400"/>
<br/>
- 지출 및 수입 내역을 새로 등록하면 자동으로 정렬되어 화면에 보여집니다.
<br/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/e63fc8a1-769d-4d5c-97eb-d350e3583943 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/24699948-2c4c-4276-9b38-854bbb1bface width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/ed8624f0-428d-4cf6-a79e-0319c0d99555 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/9aefa65d-9009-45ac-bb72-307821d764eb width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/99b7de43-9343-450c-86a0-8dc46c993931 width="200" height="400"/>
<br/>
- 지출 내역을 등록하면 채팅 페이지의 채팅방으로 자동으로 메세지가 보내집니다.
- 가계부 내역을 local에 저장하기 위해 room 라이브러리를 사용했습니다.
<br/>

### 2. 채팅 페이지

- 처음 채팅 페이지로 들어가면 채팅방 입장 코드를 입력해 채팅방에 들어갈 수 있습니다.
<img src= https://github.com/choubung/madcamp02/assets/96229091/a95e46f4-c87e-48d7-97c9-be4a1d8a0a80 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/8e7698e5-3e13-4be8-bf8d-c0ceb215ec70 width="200" height="400"/>
<br/>
- 채팅방은 Socket.io를 사용해 실시간 채팅으로 구현했습니다: 이 메세지 수신은 앱이 꺼져도 지속됩니다.
<br/>
- 친구들과 함께 가계부 내역을 공유해 보세요!
<br/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/8bc6fcd1-f841-4715-b440-303919eb6d03 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/fc80cde6-f657-472a-ac9d-931c311821a7 width="200" height="400"/>
<br/>

### 3. 가계부 분석 페이지

- 가계부 페이지에서 입력한 소비 내역을 가져와 카테고리별로 구분해 원형 그래프로 보여줍니다.
- 가계부 분석 그래프는 PieChart 라이브러리를 사용해 구현했습니다.
<img src= https://github.com/choubung/madcamp02/assets/96229091/982e76ce-250d-4dfe-8bed-1ce1fdc5fa4f width="200" height="400"/>
<br/>

### 4. 마이 페이지
- 마이 페이지에서 회원 정보를 확인하고 튜토리얼을 확인할 수 있습니다.
<img src= https://github.com/choubung/madcamp02/assets/96229091/b9d58342-0466-47fb-b117-ec8892075dd5 width="200" height="400"/>
<img src= https://github.com/choubung/madcamp02/assets/96229091/9fb474be-a788-4917-b565-2b3d056f0253 width="200" height="400"/>
<br/>

## 프로젝트 서버
- 카카오 로그인 SDK를 이용해 카카오 계정으로 로그인 기능 구현
  - 시간 순으로 배열
    - 프론트엔드
      1. 핵심 기능
          1. 0) jwt 토큰을 가지고 있는지 판별(새로 로그인 해야하는지 확인을 위해)
              1. jwt 토큰 가지고 있을 시 카카오 로그인 SDK 실행 없이 서버로 jwt 토큰을 보내게 됨
          2. 1) 카카오 로그인 API 호출, 로그인 정보를 가지고 카카오 로그인 서버에 Access Token 요청
          3. 2) Access Token을 받아와 백엔드 서버에 http 통신을 요청하며 authorization 헤더로 전달
          4. 6) 로그인 성공!
    - 백엔드
      1. 핵심 기능
          1. 3) 프론트엔드에서 Access Token을 전달받아 카카오 로그인 서버에 유저 정보 요청
          2. 4) 카카오 로그인 서버에서 유저 정보를 받아와 DB에 저장
          3. 5) 유저 로그인 정보를 jwt 토큰으로 암호화해 클라이언트에 json 형식으로 전달
              1. jwt 토큰을 DB에 추가
          4. 7)  jwt 토큰이 오면 jwt 토큰이 DB에있는지 확인
              1. 성공: 로그인
              2. 실패: 카카오 Access token이라고 생각하고 3번으로
- Socket.io를 이용해 채팅방 실시간 통신 구현
  - 유저가 채팅방 코드를 입력 시 입력된 코드의 채팅방으로 접속
  - 시스템 메세지로 접속 안내 채팅 전송
  - 메세지를 자유롭게 주고받을 수 있다.
    - 다른 사람의 채팅에 그 사람의 카카오 닉네임, 프로필 사진과 전송 시간을 포함해서 화면에 표시
      - 유저가 메세지를 전송 시 서버로 메세지가 전송됨
      - DB에 메세지 저장, 그 사람의 초대 코드를 통해 그 사람이 접속해 있는 채팅방으로 채팅 내용 전달
    - 채팅방 내의 사람들이 모두 나갔을 때 유저 수를 판별해 유저 수가 0명일 시 채팅방의 메세지들을 DB에서 삭제
    - 이때 채팅 fragment가 꺼져도 메세지를 전달받아 저장해 놓고 채팅 fragment가 켜졌을 때 로딩하는 로직 구현을 통해 앱이 백그라운드에서 돌아가고 있을 때 채팅 메세지 전달받기 가능

## APK 파일 링크
