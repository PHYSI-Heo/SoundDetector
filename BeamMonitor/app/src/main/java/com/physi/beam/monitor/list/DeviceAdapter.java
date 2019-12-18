package com.physi.beam.monitor.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.beam.monitor.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {

    public interface OnSelectedDeviceListener{
        void onPosition(int position);
    }

    private OnSelectedDeviceListener onSelectedDeviceListener;

    public void setOnSelectedDeviceListener(OnSelectedDeviceListener listener){
        onSelectedDeviceListener = listener;
    }

    private List<String> devices = new LinkedList<>();
    private int selectedPosition = -1;

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_device_number, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, final int position) {
        holder.tvItem.setText(devices.get(position));
        holder.tvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedDeviceListener != null) {
                    onSelectedDeviceListener.onPosition(selectedPosition = position);
                    notifyDataSetChanged();
                }
            }
        });

        if(selectedPosition == position){
            holder.tvItem.setBackgroundResource(R.color.colorSelected);
        }else{

            holder.tvItem.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public void setItems(List<String> devices){
        this.devices = devices;
        notifyDataSetChanged();
    }
}
