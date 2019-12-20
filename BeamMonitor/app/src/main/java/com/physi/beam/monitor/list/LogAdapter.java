package com.physi.beam.monitor.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;

import java.util.LinkedList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogHolder> {

    private List<String> logList = new LinkedList<>();

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_log_info, parent, false);
        return new LogHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        holder.tvLog.setText(logList.get(position));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public void setItem(List<String> logs){
        this.logList = logs;
        notifyDataSetChanged();
    }
}
