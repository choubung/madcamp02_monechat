package com.example.monechattest.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

import com.example.monechattest.R;

public class ExpenseDetailActivity extends AppCompatActivity {
    TextView descriptionTextView, dateTextView, categoryTextView, amountTextView, noteTextView;
    ImageView categoryImageView; // 카테고리 이미지를 위한 ImageView 추가
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    Button btn;

    // 카테고리와 이미지 리소스 ID 매핑
    private HashMap<String, Integer> categoryImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // 매핑 초기화
        initializeCategoryImageMap();

        btn = findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        descriptionTextView = findViewById(R.id.descriptionText);
        dateTextView = findViewById(R.id.dateText);
        categoryTextView = findViewById(R.id.categoryText);
        amountTextView = findViewById(R.id.amountText);
        noteTextView = findViewById(R.id.memoText);

        Intent intent = getIntent();
        if (intent != null) {
            long dateMillis = intent.getLongExtra("date", 0);
            String category = intent.getStringExtra("category");
            String description = intent.getStringExtra("description");
            String amount = intent.getStringExtra("amount");
            String note = intent.getStringExtra("note");
            boolean isSmartExpense = intent.getBooleanExtra("isSmartExpense", false);

            // 데이터 설정
            Date date = new Date(dateMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
            String formattedDate = sdf.format(date);

            descriptionTextView.setText(description);
            dateTextView.setText(formattedDate);
            categoryTextView.setText(category);
            // 금액 포맷팅
            try {
                double amountValue = Double.parseDouble(amount);
                String formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(amountValue);
                amountTextView.setText(formattedAmount);
            } catch (NumberFormatException e) {
                // 금액 파싱 오류가 발생할 경우 원래 문자열을 그대로 설정
                amountTextView.setText(amount);
            }

            noteTextView.setText(note);
            categoryImageView = findViewById(R.id.categoryImage); // ImageView 초기화

            // 카테고리에 따라 이미지 설정
            Integer imageResId = categoryImageMap.get(category);
            if (imageResId != null) {
                categoryImageView.setImageResource(imageResId);
            } else {
                categoryImageView.setImageResource(R.drawable.ic_launcher_foreground); // 기본 이미지
            }
        }
    }

    // 카테고리와 이미지 리소스 ID를 매핑하는 메서드
    private void initializeCategoryImageMap() {
        categoryImageMap.put("식사", R.drawable.icon_expense_meal);
        categoryImageMap.put("카페/간식", R.drawable.icon_expense_cafe);
        categoryImageMap.put("생활/마트", R.drawable.icon_expense_market);
        categoryImageMap.put("온라인쇼핑", R.drawable.icon_expense_online);
        categoryImageMap.put("백화점", R.drawable.icon_expense_departmentstore);
        categoryImageMap.put("금융/보험", R.drawable.icon_expense_bank);
        categoryImageMap.put("의료/건강", R.drawable.icon_expense_medi);
        categoryImageMap.put("주거/통신", R.drawable.icon_expense_call);
        categoryImageMap.put("학습/교육", R.drawable.icon_expense_edu);
        categoryImageMap.put("교통/차량", R.drawable.icon_expense_traffic);
        categoryImageMap.put("문화/예술/취미", R.drawable.icon_expense_play);
        categoryImageMap.put("여행/숙박", R.drawable.icon_expense_traffic);
        categoryImageMap.put("경조사/회비", R.drawable.icon_expense_cong);
        categoryImageMap.put("기타", R.drawable.icon_expense_else);
    }
}