package com.example.monechattest.tab1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.monechattest.MainActivity;
import com.example.monechattest.R;
import com.example.monechattest.database.AppDatabase;
import com.example.monechattest.database.ExpenseEntity;
import com.example.monechattest.database.SharedViewModel;
import com.example.monechattest.tab2.Fragment2;

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

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_detail);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllFieldsFilled()) {
                    saveExpense();
                } else {
                    Toast.makeText(AddExpenseDetailActivity.this, "모든 내역을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        ExpenseEntity expenseEntity = new ExpenseEntity(description, date, category, amount, memo);

        // 데이터베이스에 저장
        new Thread(() -> {
            long id = AppDatabase.getInstance(getApplicationContext()).getExpenseDao().insert(expenseEntity);
            expenseEntity.setIdx((int) id);
            Log.d("AddExpenseDetailActivity", "Expense saved to database with id: " + id);

            // ExpenseEntity를 ExpenseItem으로 변환
            ExpenseItem expenseItem = new ExpenseItem(expenseEntity.getIdx(), expenseEntity.getDate(), expenseEntity.getCategory(), expenseEntity.getDescription(), expenseEntity.getAmount(), expenseEntity.getNote(), false);

            runOnUiThread(() -> {
                // SharedViewModel을 사용하여 데이터 설정
                sharedViewModel.setNewExpense(expenseItem);

                // Fragment2로 데이터를 전달하는 Intent 생성
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("expenseItem", expenseItem);
                intent.putExtra("navigateToFragment2", true);
                setResult(RESULT_OK, intent);
                startActivity(intent);

                finish();
            });
        }).start();
    }
}