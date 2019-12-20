package com.physi.beam.monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.physi.beam.monitor.data.DeviceData;
import com.physi.beam.monitor.list.DeviceAdapter;
import com.physi.beam.monitor.list.LogAdapter;
import com.physi.beam.monitor.utils.DBHelper;

import java.util.Date;

public class DetailDeviceDialog extends AppCompatActivity implements View.OnClickListener {

    private EditText etNumber, etLocation;
    private Button btnUpdate;
    private RecyclerView rvLogs;

    private DBHelper dbHelper;
    private DeviceData device;
    private LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail_device);
        setFinishOnTouchOutside(false);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logAdapter.setItem(dbHelper.getLogList(device.getNumber()));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_update){
            updateDeviceInfo();
        }
    }

    private void updateDeviceInfo(){
        String num = etNumber.getText().toString();
        String location = etLocation.getText().toString();

        if(num.length() == 0 || location.length() == 0){
            return;
        }

        ContentValues params = new ContentValues();
        params.put(DBHelper.COL_DEVICE, num);
        params.put(DBHelper.COL_LOCATION, location);
        boolean result = dbHelper.updateData(DBHelper.DEVICE_TABLE, params, DBHelper.COL_NO, device.getNo());
        Toast.makeText(getApplicationContext(), "Update Result : " + result, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        dbHelper = new DBHelper(getApplicationContext());
        device = getIntent().getParcelableExtra("DEVICE");

        etLocation = findViewById(R.id.et_location);
        etLocation.setText(device.getNumber());
        etNumber = findViewById(R.id.et_serial_number);
        etNumber.setText(device.getLocation());

        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);

        rvLogs = findViewById(R.id.rv_noise_log);
        // Set Recycler Division Line
        DividerItemDecoration decoration
                = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.rc_item_division_line2));
        rvLogs.addItemDecoration(decoration);
        // Set Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailDeviceDialog.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setItemPrefetchEnabled(true);
        rvLogs.setLayoutManager(linearLayoutManager);
        // Set Adapter
        rvLogs.setAdapter(logAdapter = new LogAdapter());
    }

}
