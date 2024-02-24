package com.example.newschatbot;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsInputHistoryAdapter extends RecyclerView.Adapter<NewsInputHistoryAdapter.ViewHolder> {
private List<NewsInputHistory> historyList;

public NewsInputHistoryAdapter(List<NewsInputHistory> historyList) {
        this.historyList = historyList;
        }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_history, parent, false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsInputHistory historyItem = historyList.get(position);

        holder.valueTextView.setText(historyItem.getValue());
        }

@Override
public int getItemCount() {
        return historyList.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView keyTextView;
    public TextView valueTextView;

    public ViewHolder(View itemView) {
        super(itemView);

        valueTextView = itemView.findViewById(R.id.valueTextView);
    }
}
}
