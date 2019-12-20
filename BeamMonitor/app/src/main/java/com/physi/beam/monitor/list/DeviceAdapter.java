package com.physi.beam.monitor.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;
import com.physi.beam.monitor.data.DeviceData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {

    public interface OnSelectedListener{
        void onDevice(int action, DeviceData data);
    }

    private OnSelectedListener onSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener){
        onSelectedListener = listener;
    }

    private List<DeviceData> devices = new LinkedList<>();

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_device_info, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, final int position) {
        final DeviceData data = devices.get(position);
        holder.tvNumber.setText("Serial Number :  " + data.getNumber());
        holder.tvLocation.setText("Location :  " + data.getLocation());

        holder.llDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedListener != null)
                    onSelectedListener.onDevice(1, data);
            }
        });

        holder.llDevice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onSelectedListener != null)
                    onSelectedListener.onDevice(2, data);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public void setItems(List<DeviceData> devices){
        this.devices = devices;
        notifyDataSetChanged();
    }
}
