package com.physi.beam.monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.physi.beam.monitor.list.DeviceAdapter;
import com.physi.beam.monitor.utils.AppPreferences;
import com.physi.beam.monitor.utils.DBHelper;
import com.physicomtech.kit.physislibrary.PHYSIsMQTTActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends PHYSIsMQTTActivity implements View.OnClickListener, DeviceAdapter.OnSelectedDeviceListener {

    private static final String TAG = MainActivity.class.getName();

    private static final String SUB_TOPIC = "SoundState";

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private EditText etDeviceNum;
    private Button btnSaveDevice;
    private RecyclerView rvDevices;
    private ImageView ivSound;
    private TextView tvDetectLog, tvDetectDevice;

    private AppPreferences appPreferences;
    private DeviceAdapter deviceAdapter;
    private ObjectAnimator animator;
    private DBHelper dbHelper;

    private List<String> devices;
    private String selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectMQTT();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectMQTT();
    }

    @Override
    protected void onMQTTConnectedStatus(boolean result) {
        super.onMQTTConnectedStatus(result);
        Toast.makeText(MainActivity.this, "MQTT Connected Result : " + result, Toast.LENGTH_SHORT).show();
        if(result){
            for(String device : devices)
                startSubscribe(device, SUB_TOPIC);
        }
    }

    @Override
    protected void onMQTTDisconnected() {
        super.onMQTTDisconnected();
        Toast.makeText(MainActivity.this, "MQTT Disconnected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSubscribeListener(String serialNum, String topic, String data) {
        super.onSubscribeListener(serialNum, topic, data);
        Log.e(TAG, serialNum + "/" + topic + " : " + data);
        if(SUB_TOPIC.equals(topic)){
            if(data.equals("1")){
                tvDetectDevice.setText("Device Name : " + serialNum);
                ivSound.setImageResource(R.drawable.ic_sound_on);
                animator.start();
                if(devices.contains(serialNum)) {
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.COL_DEVICE, serialNum);
                    values.put(DBHelper.COL_LOG, dateFormat.format(new Date()));
                    dbHelper.insertRow(values);
                    if(serialNum.equals(selectedDevice))
                        showDetectLog(serialNum);
                }
            }else{
                ivSound.setImageResource(R.drawable.ic_sound_off);
                animator.end();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_append_device){
            setDeviceList();
        }
    }

    @Override
    public void onPosition(int position) {
        showDetectLog(devices.get(position));
    }

    private void showDetectLog(String device){
        tvDetectLog.setText(dbHelper.selectRows(device));
    }

    private void setDeviceList(){
        String deviceNum = etDeviceNum.getText().toString();
        if(deviceNum.length() != 12 || devices.contains(deviceNum)) {
            Toast.makeText(MainActivity.this, "Device Number Error!!", Toast.LENGTH_SHORT).show();
            return;
        }
        startSubscribe(deviceNum, SUB_TOPIC);
        devices.add(deviceNum);
        deviceAdapter.setItems(devices);
        appPreferences.setDevices(devices);
    }

    @SuppressLint("WrongConstant")
    private void init() {
        appPreferences = new AppPreferences(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());

        etDeviceNum = findViewById(R.id.et_device_num);
        tvDetectLog = findViewById(R.id.tv_detect_log);
        tvDetectLog.setMovementMethod(new ScrollingMovementMethod());
        tvDetectDevice = findViewById(R.id.tv_detect_device);
        tvDetectDevice.setText("Device Name : No Device");

        btnSaveDevice = findViewById(R.id.btn_append_device);
        btnSaveDevice.setOnClickListener(this);

        rvDevices = findViewById(R.id.rv_devices);
        // Set Recycler Division Line
        DividerItemDecoration decoration
                = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.rc_item_division_line));
        rvDevices.addItemDecoration(decoration);
        // Set Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setItemPrefetchEnabled(true);
        rvDevices.setLayoutManager(linearLayoutManager);
        // Set Adapter
        rvDevices.setAdapter(deviceAdapter = new DeviceAdapter());
        deviceAdapter.setOnSelectedDeviceListener(this);

        // Effect Animation
        ivSound = findViewById(R.id.iv_sound);
        animator =  ObjectAnimator.ofInt(ivSound, "backgroundColor", Color.WHITE, Color.RED, Color.WHITE);
        animator.setDuration(250);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(Animation.INFINITE);
        animator.setRepeatMode(Animation.RESTART);

        devices = appPreferences.getDevices();
        if(devices != null)
            deviceAdapter.setItems(devices);
        else
            devices = new LinkedList<>();
    }
}
