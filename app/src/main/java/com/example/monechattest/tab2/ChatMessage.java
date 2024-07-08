package com.example.monechattest.tab2;

//채팅 메시지의 데이터를 보유할 모델 클래스 정의
public class ChatMessage {
    private String content;
    private boolean isSentByMe;

    public ChatMessage(String content, boolean isSentByMe) {
        this.content = content;
        this.isSentByMe = isSentByMe;
    }

    public String getContent() {
        return content;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }
}
