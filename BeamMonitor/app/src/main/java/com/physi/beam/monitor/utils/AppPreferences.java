package com.physi.beam.monitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.physi.beam.monitor.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AppPreferences {

    private static final String PHYSIs_SERIAL_NUM = "Beam_Monitor";

    private SharedPreferences pref;

    public AppPreferences(Context context){
        pref = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
    }

    public void setDevices(List<String> devices){
        StringBuilder builder = new StringBuilder();
        for(String device : devices){
            builder.append(device).append(",");
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PHYSIs_SERIAL_NUM, builder.toString());
        editor.apply();
    }

    public List<String> getDevices(){
        String devices =  pref.getString(PHYSIs_SERIAL_NUM, null);
        if(devices == null)
            return null;
        return new LinkedList<String>(Arrays.asList(devices.split(",")));
    }
}
