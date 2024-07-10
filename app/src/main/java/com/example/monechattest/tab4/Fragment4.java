package com.example.monechattest.tab4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.example.monechattest.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.util.Log;

import com.example.monechattest.BuildConfig;
import android.content.SharedPreferences;

public class Fragment4 extends Fragment {
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private ImageView userProfileImageView;
    private Button logoutButton;
    private Button deleteAccountButton;
    private Button tutorialButton;

    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String JWT_TOKEN_KEY = "jwt_token";
    private static final String SERVER_URL = BuildConfig.SERVER_ADDRESS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_4, container, false);

        userNameTextView = rootView.findViewById(R.id.user_name);
        userEmailTextView = rootView.findViewById(R.id.user_email);
        userProfileImageView = rootView.findViewById(R.id.user_profile);
        logoutButton = rootView.findViewById(R.id.logout_button); // 로그아웃 버튼
        deleteAccountButton = rootView.findViewById(R.id.delete_account_button); // 회원탈퇴 버튼
        tutorialButton = rootView.findViewById(R.id.tutorial_button); // 튜토리얼 버튼

        // 서버로 POST 요청 보내기
        sendPostRequest();

        // 로그아웃 버튼 클릭 리스너
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("회원님 영원히 함께해요 ^^");
            }
        });

        // 회원탈퇴 버튼 클릭 리스너
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("탈퇴는 고객센터로 문의해 주세요.");
            }
        });

        // 튜토리얼 버튼 클릭 리스너
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorialDialog();
            }
        });

        return rootView;
    }

    private void sendPostRequest() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(JWT_TOKEN_KEY, null);

        String url = SERVER_URL + "/auth/UserInfo"; // 서버 URL을 입력하세요

        JSONObject postData = new JSONObject();
//        try {
//            postData.put("key", "value"); // 요청 본문에 필요한 데이터를 추가하세요
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        RequestBody body = RequestBody.create(postData.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.i("HTTP in Fragment4", "Sending token: " + token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                String userName = jsonResponse.getString("UserName");
                                String userProfile = jsonResponse.getString("UserProfile");
                                String userMail = jsonResponse.getString("UserMail");

                                userNameTextView.setText(userName);
                                userEmailTextView.setText(userMail);
                                if (!Objects.equals(userProfile, "") && !userProfile.isEmpty()) {
                                    Glide.with(getContext())
                                            .load(userProfile)
                                            .circleCrop() // 이미지를 원형으로 크롭
                                            .into(userProfileImageView);
                                } else {
                                    userProfileImageView.setImageResource(R.drawable.img_choucream); // 기본 프로필 이미지 설정
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    // 응답이 성공적이지 않은 경우
                    response.close();
                }
            }
        });
    }

    private void showInputDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        // 확인 버튼을 설정합니다.
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // 취소 버튼을 설정합니다.
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showTutorialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_tutorial, null);
        builder.setView(dialogLayout);

        ViewPager tutorialViewPager = dialogLayout.findViewById(R.id.tutorial_view_pager);

        // 여기에 로컬 이미지 리소스 ID들을 추가하세요.
        List<Integer> imageResIds = Arrays.asList(
                R.drawable.kakao_login_page,
                R.drawable.spend_page,
                R.drawable.income_page,
                R.drawable.add_spend_page,
                R.drawable.chatting_init_page,
                R.drawable.chatting_entrance_page,
                R.drawable.chat_example1,
                R.drawable.chat_example2,
                R.drawable.chart_page
        );

        TutorialPagerAdapter adapter = new TutorialPagerAdapter(getContext(), imageResIds);
        tutorialViewPager.setAdapter(adapter);

        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}