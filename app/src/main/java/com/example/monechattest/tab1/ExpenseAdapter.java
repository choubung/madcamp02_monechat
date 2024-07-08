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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    Context context;
    ArrayList<ExpenseItem> expenseItems;
    OnItemClickListener clickListener;
    OnItemLongClickListener longClickListener;  // 길게 누르기 리스너 추가

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

    public ExpenseAdapter(Context context, ArrayList<ExpenseItem> expenseItems) {
        this.context = context;
        this.expenseItems = expenseItems;
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        String formattedDate = sdf.format(date);

        holder.description.setText(item.getDescription().toString());
        holder.date.setText(formattedDate);
        holder.category.setText(item.getCategory().toString());
        holder.amount.setText(item.getAmount().toString());
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

    // 뷰 홀더 클래스
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView date, category, description, amount;
        Button isSmartExpense; // TODO: 현명 소비 화면에 어떤 방식으로 띄울 건지 구체적으로 정해야함 (drawable)

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {  // 생성자에서 리스너 전달받음
            super(itemView);

            categoryImage = itemView.findViewById(R.id.categoryImage);

            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);

            amount = itemView.findViewById(R.id.amount);
            isSmartExpense = itemView.findViewById(R.id.isSmartExpense);

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