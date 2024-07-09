package com.example.monechattest.tab2;

public class ChatMessage {
    private String message;
    private boolean isSentByCurrentUser;
    private String userName;
    private String profileImage;
    private String timestamp;

    // 기존 생성자
    public ChatMessage(String message, boolean isSentByCurrentUser) {
        this.message = message;
        this.isSentByCurrentUser = isSentByCurrentUser;
    }

    // 새 생성자
    public ChatMessage(String message, boolean isSentByCurrentUser, String userName) {
        this.message = message;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.userName = userName;
    }

    // 새 생성자 (프로필 이미지와 시간 포함)
    public ChatMessage(String message, boolean isSentByCurrentUser, String userName, String profileImage, String timestamp) {
        this.message = message;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.userName = userName;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
    }

    // Getter와 Setter 추가
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSentByCurrentUser() {
        return isSentByCurrentUser;
    }

    public void setSentByCurrentUser(boolean sentByCurrentUser) {
        isSentByCurrentUser = sentByCurrentUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
