package com.example.monechattest.tab1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monechattest.R;

import java.text.NumberFormat;
import java.util.Calendar;

public class AddExpenseDetailActivity extends AppCompatActivity {
    EditText descriptionText, amountText, dateText, memoText;
    Spinner spinner;
    Button backBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_detail);

        descriptionText = findViewById(R.id.descriptionText);
        amountText = findViewById(R.id.amountText);
        dateText = findViewById(R.id.dateText);
        memoText = findViewById(R.id.memoText);

        spinner = findViewById(R.id.spinner);

        setupAmountFormatting();
        setupDatePicker();

        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // 저장 구현(putExtra) 요망
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

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
}