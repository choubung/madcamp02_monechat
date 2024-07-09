package com.example.monechattest.tab1;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monechattest.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    Context context;
    ArrayList<ExpenseItem> expenseItems;
    OnItemClickListener clickListener;
    OnItemLongClickListener longClickListener;  // 길게 누르기 리스너 추가

    // 이미지 리소스 매핑
    private HashMap<String, Integer> categoryImageMap = new HashMap<>();

    public ExpenseAdapter(Context context, ArrayList<ExpenseItem> expenseItems) {
        this.context = context;
        this.expenseItems = expenseItems;
        sortItemsByDate();  // 초기 데이터 정렬
        initializeCategoryImageMap(); // 이미지 매핑 초기화
    }

    public ExpenseItem getItem(int position) {
        return expenseItems.get(position);
    }

    // 아이템 추가 메서드
    public void addItem(ExpenseItem item) {
        expenseItems.add(item);
        notifyItemInserted(expenseItems.size() - 1);
    }

    // 아이템 삭제 메서드
    public void removeItem(int position) {
        expenseItems.remove(position);
        notifyItemRemoved(position);
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, View view, int position);
    }

    // 길게 누르기 리스너 인터페이스 추가
    public interface OnItemLongClickListener {  // 길게 누르기 리스너 인터페이스 추가
        void onItemLongClick(ViewHolder holder, View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(itemView, clickListener, longClickListener);  // 생성자에 리스너 전달
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseItem item = expenseItems.get(position);

        long dateMillis = item.getDateMillis();
        Date date = new Date(dateMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String formattedDate = sdf.format(date);

        holder.description.setText(item.getDescription().toString());
        holder.date.setText(formattedDate);
        holder.category.setText(item.getCategory().toString());

        // 금액 포맷팅
        try {
            double amountValue = Double.parseDouble(item.getAmount().toString());
            String formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(amountValue);
            holder.amount.setText(formattedAmount);
        } catch (NumberFormatException e) {
            // 금액 파싱 오류가 발생할 경우 원래 문자열을 그대로 설정
            holder.amount.setText(item.getAmount().toString());
        }

        // 카테고리에 따라 이미지 설정
        Integer imageResId = categoryImageMap.get(item.getCategory());
        if (imageResId != null) {
            holder.categoryImage.setImageResource(imageResId);
        } else {
            holder.categoryImage.setImageResource(R.drawable.ic_launcher_foreground); // 기본 이미지
        }
    }

    @Override
    public int getItemCount() {
        return expenseItems.size();
    }

    // 아이템 리스트를 설정하는 메서드
    public void setItems(ArrayList<ExpenseItem> items) {
        this.expenseItems = items;
        notifyDataSetChanged();
    }

    // 아이템 클릭 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // 아이템 길게 누르기 리스너 설정 메서드
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {  // 길게 누르기 리스너 설정 메서드
        this.longClickListener = listener;
    }

    // 날짜 기준으로 아이템 정렬
    private void sortItemsByDate() {
        Collections.sort(expenseItems, new Comparator<ExpenseItem>() {
            @Override
            public int compare(ExpenseItem item1, ExpenseItem item2) {
                return Long.compare(item2.getDateMillis(), item1.getDateMillis());
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

    // 뷰 홀더 클래스
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView date, category, description, amount;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {  // 생성자에서 리스너 전달받음
            super(itemView);

            categoryImage = itemView.findViewById(R.id.categoryImage);

            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);
            amount = itemView.findViewById(R.id.amount);
//            isSmartExpense = itemView.findViewById(R.id.isSmartExpense);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (clickListener != null && position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {  // 길게 누르기 리스너 설정
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(ViewHolder.this, view, position);
                        return true;  // 이벤트 소비
                    }
                    return false;
                }
            });
        }
    }
}