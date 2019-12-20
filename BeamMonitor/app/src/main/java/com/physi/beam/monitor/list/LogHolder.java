package com.physi.beam.monitor.list;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;

public class LogHolder extends RecyclerView.ViewHolder {

    TextView tvLog;

    public LogHolder(@NonNull View itemView) {
        super(itemView);

        tvLog = itemView.findViewById(R.id.tv_log_item);
    }
}
