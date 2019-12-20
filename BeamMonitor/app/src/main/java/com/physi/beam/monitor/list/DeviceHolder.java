package com.physi.beam.monitor.list;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;

public class DeviceHolder extends RecyclerView.ViewHolder {

    TextView tvNumber, tvLocation;
    LinearLayout llDevice;

    public DeviceHolder(@NonNull View itemView) {
        super(itemView);

        tvNumber = itemView.findViewById(R.id.tv_device_number);
        tvLocation = itemView.findViewById(R.id.tv_device_location);

        llDevice = itemView.findViewById(R.id.ll_device);
    }
}
