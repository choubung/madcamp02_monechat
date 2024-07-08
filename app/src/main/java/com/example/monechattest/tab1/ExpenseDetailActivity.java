package com.example.monechattest.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.monechattest.R;

public class ExpenseDetailActivity extends AppCompatActivity {
    TextView descriptionTextView, dateTextView, categoryTextView, amountTextView, noteTextView;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

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
            int idx = intent.getIntExtra("idx", -1);
            long dateMillis = intent.getLongExtra("date", 0);
            String category = intent.getStringExtra("category");
            String description = intent.getStringExtra("description");
            String amount = intent.getStringExtra("amount");
            String note = intent.getStringExtra("note");
            boolean isSmartExpense = intent.getBooleanExtra("isSmartExpense", false);

            Date date = new Date(dateMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
            String formattedDate = sdf.format(date);

            descriptionTextView.setText(description);
            dateTextView.setText(formattedDate);
            categoryTextView.setText(category);
            amountTextView.setText(amount);
            noteTextView.setText(note);
        }
    }
}