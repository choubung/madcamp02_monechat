package com.example.monechattest.tab4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;
import com.example.monechattest.ChatMessageListener;
import com.example.monechattest.R;

import com.example.monechattest.R;

public class Fragment4 extends Fragment implements ChatMessageListener {
    private TextView messageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_4, container, false);

        return rootView;
    }

    @Override
    public void onNewChatMessage(String message) {
        // 시스템 알림을 통해 메시지를 수신하므로 여기는 비워둡니다.
    }
}