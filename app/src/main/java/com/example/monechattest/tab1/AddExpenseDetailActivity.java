package com.example.monechattest.tab1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monechattest.R;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddExpenseDetailActivity extends AppCompatActivity {
    EditText descriptionText, amountText, dateText, memoText;
    Spinner spinner;
    Button backBtn, saveBtn;
    ImageView categoryImageView;
    String[] categories = {"식사", "카페/간식", "생활/마트", "온라인쇼핑", "백화점", "금융/보험", "의료/건강", "주거/통신", "학습/교육", "교통/차량", "문화/예술/취미", "여행/숙박", "경조사/회비", "기타"};
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA); // gpt 코드

    // 이미지 리소스 매핑
    HashMap<String, Integer> categoryImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_detail);

        // EditText 초기화
        descriptionText = findViewById(R.id.descriptionText);
        amountText = findViewById(R.id.amountText);
        dateText = findViewById(R.id.dateText);
        memoText = findViewById(R.id.memoText);

        // Spinner 초기화
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 카테고리 이미지 뷰 초기화
        categoryImageView = findViewById(R.id.categoryImageView);

        // 이미지 매핑 설정
        initializeCategoryImageMap();

        // Spinner 선택 항목 변경 리스너 설정
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 카테고리 가져오기
                String selectedCategory = categories[position];
                // 이미지 변경
                updateCategoryImage(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않은 경우
            }
        });

        // 금액 포맷 설정 메서드 호출
        setupAmountFormatting();

        // 날짜 선택기 설정 메서드 호출
        setupDatePicker();

        // '뒤로 가기' 버튼 설정
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // '저장' 버튼 설정
        saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 기존 코드
                Intent intent = new Intent();
                // 저장 구현(putExtra) 요망
                setResult(RESULT_OK, intent);
                finish();*/

                // 저장 처리 메서드 호출
                saveExpense(); // gpt코드
            }
        });
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

    // 선택된 카테고리에 따라 이미지를 업데이트하는 메서드
    private void updateCategoryImage(String selectedCategory) {
        Integer imageResId = categoryImageMap.get(selectedCategory);
        if (imageResId != null) {
            categoryImageView.setImageResource(imageResId);
        }
    }

    // 금액 입력 시 포맷을 지정하는 메서드
    private void setupAmountFormatting() { // 문제 없을듯
        amountText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    amountText.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance().format(parsed);
                        current = formatted;
                        amountText.setText(formatted);
                        amountText.setSelection(formatted.length());
                    } else {
                        current = "";
                    }
                    amountText.addTextChangedListener(this);
                }
            }
        });
    }

    // 날짜 선택기 설정 메서드
    private void setupDatePicker() {
        dateText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseDetailActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedMonth++;
                        dateText.setText(selectedYear + "/" + selectedMonth + "/" + selectedDay);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    // 지출 정보를 저장하는 메서드
    private void saveExpense() {
        String description = descriptionText.getText().toString();
        String amount = amountText.getText().toString().replace(",","");
        String dateStr = dateText.getText().toString();
        String memo = memoText.getText().toString();
        String category = spinner.getSelectedItem().toString();

        Date date = null; // 데이터 컨버터
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // ExpenseItem 객체 생성
        ExpenseItem expenseItem = new ExpenseItem(0, date, category, description, amount, memo, false);

        // Intent에 데이터 추가하여 반환
        Intent intent = new Intent();
        intent.putExtra("expenseItem", expenseItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}