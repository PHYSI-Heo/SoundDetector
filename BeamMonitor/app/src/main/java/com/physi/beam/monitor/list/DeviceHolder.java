package com.physi.beam.monitor.list;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;

public class DeviceHolder extends RecyclerView.ViewHolder {

    TextView tvItem;

    public DeviceHolder(@NonNull View itemView) {
        super(itemView);

        tvItem = itemView.findViewById(R.id.tv_device_item);
    }
}
