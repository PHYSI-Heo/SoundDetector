package com.physi.beam.monitor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.physi.beam.monitor.data.DeviceData;
import com.physi.beam.monitor.list.DeviceAdapter;
import com.physi.beam.monitor.utils.DBHelper;
import com.physi.beam.monitor.utils.NotifyDialog;
import com.physicomtech.kit.physislibrary.PHYSIsMQTTActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DeviceActivity extends PHYSIsMQTTActivity implements View.OnClickListener, DeviceAdapter.OnSelectedListener {

    private static final int REQ_APPEND_DEVICE_CODE = 101;
    private static final String SUB_TOPIC = "SoundState";

    private FloatingActionButton btnAppendDevice;
    private RecyclerView rvDeviceList;

    private DeviceAdapter deviceAdapter;
    private DBHelper dbHelper;

    private List<DeviceData> devices = new LinkedList<>();


    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        init();
        connectMQTT();
    }

    @Override
    protected void onStart() {
        super.onStart();
        devices = dbHelper.getDeviceList();
        deviceAdapter.setItems(devices);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onMQTTConnectedStatus(boolean result) {
        super.onMQTTConnectedStatus(result);
        Toast.makeText(getApplicationContext(), "MQTT Connect Result : " + result, Toast.LENGTH_SHORT).show();
        if(result){
            startSubscribes();
        }
    }

    @Override
    protected void onMQTTDisconnected() {
        super.onMQTTDisconnected();
        Toast.makeText(getApplicationContext(), "MQTT Disconnected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSubscribeListener(String serialNum, String topic, String data) {
        super.onSubscribeListener(serialNum, topic, data);
        if(SUB_TOPIC.equals(topic)){
            for(DeviceData device : devices){
                if(device.getNumber().equals(serialNum)){
                    // noise
                    checkNoiseMessage(device, data);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_APPEND_DEVICE_CODE && resultCode == RESULT_OK){
            devices = dbHelper.getDeviceList();
            deviceAdapter.setItems(devices);
            startSubscribes();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_append_device){
            startActivityForResult(new Intent(DeviceActivity.this, AppendDeviceDialog.class), REQ_APPEND_DEVICE_CODE);
        }
    }

    @Override
    public void onDevice(int action, final DeviceData data) {
        if(action == 1){
            // Show Dialog
            startActivity(new Intent(DeviceActivity.this, DetailDeviceDialog.class).putExtra("DEVICE", data));
        }else{
            // Delete
            String notifyMsg = data.getNumber() + "에 대한 정보를 삭제하시겠습니까?";
            new NotifyDialog().show(DeviceActivity.this, notifyMsg,
                    "Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean result = dbHelper.deleteData(DBHelper.DEVICE_TABLE, DBHelper.COL_NO, data.getNo());
                            Toast.makeText(DeviceActivity.this, "Delete Result : " + result, Toast.LENGTH_SHORT).show();
                            if(result){
                                stopSubscribe(data.getNumber(), SUB_TOPIC);
                                deviceAdapter.setItems(devices = dbHelper.getDeviceList());
                            }
                        }
                    },
                    "Cancel", null);
        }
    }

    private void checkNoiseMessage(DeviceData device, String msg){
        String decibel = msg.substring(1);
        if(msg.startsWith("1")){
            sendNotification(device.getLocation(), decibel + " dB 소음이 발생하였습니다.");
        }
        ContentValues params = new ContentValues();
        params.put(DBHelper.COL_DEVICE, device.getNumber());
        params.put(DBHelper.COL_LOG, dateFormat.format(new Date()) + " (" + decibel + ")");
        dbHelper.insertData(DBHelper.LOG_TABLE, params);
    }

    private void sendNotification(String location, String msg) {
        String channelID = "Noise";
        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this, channelID)
                        .setSmallIcon(R.drawable.ic_noise_effect)
                        .setContentTitle(location + " Noise!")
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager nManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            NotificationChannel channel = new NotificationChannel(channelID, timeFormat.format(new Date()), NotificationManager.IMPORTANCE_HIGH);
            assert nManager != null;
            nManager.createNotificationChannel(channel);
        }

        assert nManager != null;
        nManager.notify(0 /* ID of notification */, nBuilder.build());
    }

    private void startSubscribes(){
        for(DeviceData data : devices){
            startSubscribe(data.getNumber(), SUB_TOPIC);
        }
    }

    private void init() {
        dbHelper = new DBHelper(getApplicationContext());

        btnAppendDevice = findViewById(R.id.btn_append_device);
        btnAppendDevice.setOnClickListener(this);

        rvDeviceList = findViewById(R.id.rv_devices);
        // Set Recycler Division Line
        DividerItemDecoration decoration
                = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.rc_item_division_line));
        rvDeviceList.addItemDecoration(decoration);
        // Set Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DeviceActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setItemPrefetchEnabled(true);
        rvDeviceList.setLayoutManager(linearLayoutManager);
        // Set Adapter
        rvDeviceList.setAdapter(deviceAdapter = new DeviceAdapter());
        deviceAdapter.setOnSelectedListener(this);
    }

}
