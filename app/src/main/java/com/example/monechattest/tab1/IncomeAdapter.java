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
import java.util.Locale;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {
    Context context;
    ArrayList<IncomeItem> incomeItems;
    IncomeAdapter.OnItemClickListener clickListener;
    IncomeAdapter.OnItemLongClickListener longClickListener;  // 길게 누르기 리스너 추가

    public IncomeItem getItem(int position) {
        return incomeItems.get(position);
    }

    // 아이템 추가 메서드
    public void addItem(IncomeItem item) {
        incomeItems.add(item);
        notifyItemInserted(incomeItems.size() - 1);
    }

    // 아이템 삭제 메서드
    public void removeItem(int position) {
        incomeItems.remove(position);
        notifyItemRemoved(position);
    }

    // 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(IncomeAdapter.ViewHolder holder, View view, int position);
    }

    // 길게 누르기 리스너 인터페이스 추가
    public interface OnItemLongClickListener {  // 길게 누르기 리스너 인터페이스 추가
        void onItemLongClick(IncomeAdapter.ViewHolder holder, View view, int position);
    }

    public IncomeAdapter(Context context, ArrayList<IncomeItem> incomeItems) {
        this.context = context;
        this.incomeItems = incomeItems;
        sortItemsByDate();  // 초기 데이터 정렬
    }

    @NonNull
    @Override
    public IncomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.income_item, parent, false);
        return new IncomeAdapter.ViewHolder(itemView, clickListener, longClickListener);  // 생성자에 리스너 전달
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.ViewHolder holder, int position) {
        IncomeItem item = incomeItems.get(position);

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
    }

    @Override
    public int getItemCount() {
        return incomeItems.size();
    }

    // 아이템 리스트를 설정하는 메서드
    public void setItems(ArrayList<IncomeItem> items) {
        this.incomeItems = items;
        notifyDataSetChanged();
    }

    // 아이템 클릭 리스너 설정 메서드
    public void setOnItemClickListener(IncomeAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // 아이템 길게 누르기 리스너 설정 메서드
    public void setOnItemLongClickListener(IncomeAdapter.OnItemLongClickListener listener) {  // 길게 누르기 리스너 설정 메서드
        this.longClickListener = listener;
    }

    // 날짜 기준으로 아이템 정렬
    private void sortItemsByDate() {
        Collections.sort(incomeItems, new Comparator<IncomeItem>() {
            @Override
            public int compare(IncomeItem item1, IncomeItem item2) {
                return Long.compare(item2.getDateMillis(), item1.getDateMillis());
            }
        });
    }

    // 뷰 홀더 클래스
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView date, category, description, amount;
        Button isSmartIncome; // TODO: 현명 소비 화면에 어떤 방식으로 띄울 건지 구체적으로 정해야함 (drawable)

        public ViewHolder(@NonNull View itemView, final IncomeAdapter.OnItemClickListener clickListener, final IncomeAdapter.OnItemLongClickListener longClickListener) {  // 생성자에서 리스너 전달받음
            super(itemView);

            categoryImage = itemView.findViewById(R.id.categoryImage);

            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category);

            amount = itemView.findViewById(R.id.amount);
//            isSmartIncome = itemView.findViewById(R.id.isSmartIncome);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (clickListener != null && position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(IncomeAdapter.ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {  // 길게 누르기 리스너 설정
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(IncomeAdapter.ViewHolder.this, view, position);
                        return true;  // 이벤트 소비
                    }
                    return false;
                }
            });
        }
    }
}
