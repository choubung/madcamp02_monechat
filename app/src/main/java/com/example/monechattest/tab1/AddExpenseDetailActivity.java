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
import android.widget.Toast;
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
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);

    HashMap<String, Integer> categoryImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_detail);

        descriptionText = findViewById(R.id.descriptionText);
        amountText = findViewById(R.id.amountText);
        dateText = findViewById(R.id.dateText);
        memoText = findViewById(R.id.memoText);
        spinner = findViewById(R.id.spinner);
        backBtn = findViewById(R.id.back);
        saveBtn = findViewById(R.id.btnSave);
        categoryImageView = findViewById(R.id.categoryImageView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        initializeCategoryImageMap();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                updateCategoryImage(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupAmountFormatting();
        setupDatePicker();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 수정된 부분 시작
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllFieldsFilled()) { // 모든 필드가 채워졌는지 확인
                    saveExpense();
                } else {
                    Toast.makeText(AddExpenseDetailActivity.this, "모든 내역을 입력하세요", Toast.LENGTH_SHORT).show(); // 필드가 채워지지 않으면 토스트 메시지 표시
                }
            }
        });

        // 각 EditText에 TextWatcher 추가
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saveBtn.setEnabled(areAllFieldsFilled()); // 필드가 채워졌는지 확인하여 저장 버튼 활성화 여부 결정
            }
        };

        descriptionText.addTextChangedListener(textWatcher);
        amountText.addTextChangedListener(textWatcher);
        dateText.addTextChangedListener(textWatcher);

        saveBtn.setEnabled(areAllFieldsFilled()); // 초기 상태에서 저장 버튼 활성화 여부 결정
        // 수정된 부분 끝
    }

    // 모든 필드가 채워졌는지 확인하는 메서드 추가 (메모 필드는 제외)
    private boolean areAllFieldsFilled() {
        return !descriptionText.getText().toString().isEmpty() &&
                !amountText.getText().toString().isEmpty() &&
                !dateText.getText().toString().isEmpty();
    }

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

    private void updateCategoryImage(String selectedCategory) {
        Integer imageResId = categoryImageMap.get(selectedCategory);
        if (imageResId != null) {
            categoryImageView.setImageResource(imageResId);
        }
    }

    private void setupAmountFormatting() {
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

    private void saveExpense() {
        String description = descriptionText.getText().toString();
        String amount = amountText.getText().toString().replace(",", "");
        String dateStr = dateText.getText().toString();
        String memo = memoText.getText().toString();
        String category = spinner.getSelectedItem().toString();

        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ExpenseItem expenseItem = new ExpenseItem(0, date, category, description, amount, memo, false);

        Intent intent = new Intent();
        intent.putExtra("expenseItem", expenseItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}