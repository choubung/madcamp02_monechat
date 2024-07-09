package com.example.monechattest.tab2;

public class ChatMessage {
    private String message;
    private boolean isSentByUser;
    private String userName;
    private String profileImage;
    private String timestamp;

    public ChatMessage(String message, boolean isSentByUser, String userName) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.userName = userName;
    }

    public ChatMessage(String message, boolean isSentByUser, String userName, String profileImage, String timestamp) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.userName = userName;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
