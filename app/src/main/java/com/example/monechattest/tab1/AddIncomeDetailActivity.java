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

public class AddIncomeDetailActivity extends AppCompatActivity {
    EditText descriptionText, amountText, dateText, memoText;
    ImageView categoryImage;
    Spinner spinner;
    Button backBtn, saveBtn;
    String[] categories = {"주수입", "부수입", "기타수입"};
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);

    // 카테고리와 이미지 리소스 ID 매핑
    private HashMap<String, Integer> categoryImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_detail);

        // EditText 초기화
        descriptionText = findViewById(R.id.descriptionText);
        amountText = findViewById(R.id.amountText);
        dateText = findViewById(R.id.dateText);
        memoText = findViewById(R.id.memoText);
        categoryImage = findViewById(R.id.categoryImage);

        initializeCategoryImageMap();

        // Spinner 초기화
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Spinner 선택 리스너 설정
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                updateCategoryImage(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않았을 때 기본 이미지 설정
                categoryImage.setImageResource(R.drawable.ic_launcher_foreground);
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
                if (areAllFieldsFilled()) {
                    saveIncome();
                } else {
                    Toast.makeText(AddIncomeDetailActivity.this, "모든 내역을 입력하세요", Toast.LENGTH_SHORT).show();
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
                saveBtn.setEnabled(areAllFieldsFilled());
            }
        };

        descriptionText.addTextChangedListener(textWatcher);
        amountText.addTextChangedListener(textWatcher);
        dateText.addTextChangedListener(textWatcher);

        saveBtn.setEnabled(areAllFieldsFilled());
    }

    // 모든 필드가 채워졌는지 확인하는 메서드 추가 (메모 필드는 제외)
    private boolean areAllFieldsFilled() {
        return !descriptionText.getText().toString().isEmpty() &&
                !amountText.getText().toString().isEmpty() &&
                !dateText.getText().toString().isEmpty();
    }

    private void initializeCategoryImageMap() {
        categoryImageMap.put("주수입", R.drawable.icon_income_main);
        categoryImageMap.put("부수입", R.drawable.icon_income_additional);
        categoryImageMap.put("기타수입", R.drawable.icon_income_else);
    }

    private void updateCategoryImage(String category) {
        Integer imageResId = categoryImageMap.get(category);
        if (imageResId != null) {
            categoryImage.setImageResource(imageResId);
        } else {
            categoryImage.setImageResource(R.drawable.ic_launcher_foreground);
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddIncomeDetailActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedMonth++;
                        dateText.setText(selectedYear + "/" + selectedMonth + "/" + selectedDay);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void saveIncome() {
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

        IncomeItem incomeItem = new IncomeItem(0, date, category, description, amount, memo);

        Intent intent = new Intent();
        intent.putExtra("incomeItem", incomeItem);
        setResult(RESULT_OK, intent);
        finish();
    }
}