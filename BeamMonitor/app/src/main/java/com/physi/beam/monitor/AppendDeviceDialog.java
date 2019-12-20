package com.physi.beam.monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.physi.beam.monitor.utils.DBHelper;

public class AppendDeviceDialog extends AppCompatActivity implements View.OnClickListener {

    private EditText etSerialNumber, etLocation;
    private Button btnRegister;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_append_device);
        setFinishOnTouchOutside(false);

        init();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register){
            registerDevice();
        }
    }

    private void registerDevice(){
        String number = etSerialNumber.getText().toString();
        String location = etLocation.getText().toString();

        if(number.length() == 0 || location.length() == 0)
            return;

        ContentValues params = new ContentValues();
        params.put(DBHelper.COL_DEVICE, number);
        params.put(DBHelper.COL_LOCATION, location);

        boolean result = dbHelper.insertData(DBHelper.DEVICE_TABLE, params);
        Toast.makeText(getApplicationContext(), "Register Result : " + result, Toast.LENGTH_SHORT).show();
        if(result){
            setResult(RESULT_OK, getIntent());
            finish();
        }
    }

    private void init() {
        etSerialNumber = findViewById(R.id.et_serial_number);
        etLocation = findViewById(R.id.et_location);

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);

        dbHelper = new DBHelper(getApplicationContext());
    }
}
