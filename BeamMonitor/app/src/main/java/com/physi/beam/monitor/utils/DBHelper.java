package com.physi.beam.monitor.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.physi.beam.monitor.data.DeviceData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getName();
    private Context context;

    public static final String DEVICE_TABLE= "DeviceList";
    public static final String LOG_TABLE= "LogList";

    private static final String DATABASE = "BeamMonitor.db";
    private static final int VERSION = 1;

    public static final String COL_NO = "_no";
    public static final String COL_DEVICE = "device";
    public static final String COL_LOCATION = "location";
    public static final String COL_LOG = "log";

//    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }
//
//    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//    }

    public DBHelper(Context context){
        super(context, DATABASE, null, VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + LOG_TABLE + " (" +
                COL_DEVICE + " TEXT NOT NULL, " +
                COL_LOG + " TEXT NOT NULL )";
        db.execSQL(sql);

        sql = "CREATE TABLE " + DEVICE_TABLE + " (" +
                COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COL_DEVICE + " TEXT NOT NULL, " +
                COL_LOCATION + " TEXT NOT NULL )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }

    public boolean insertData(String table, ContentValues values){
        return getWritableDatabase().insert(table, null, values) > 0;
    }

    public boolean updateData(String table, ContentValues values, String targetColumn, String targetValue){
        return getWritableDatabase().update(table, values, targetColumn + " = '" + targetValue + "'",null) > 0;
    }

    public boolean deleteData(String table, String targetColumn, String targetValue){
        return getWritableDatabase().delete(table, targetColumn + " = '" + targetValue + "'",null) > 0;
    }

    public List<DeviceData> getDeviceList(){
        List<DeviceData> devices = new LinkedList<>();
        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + DEVICE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                devices.add(new DeviceData(cursor.getString(cursor.getColumnIndex(COL_NO)),
                        cursor.getString(cursor.getColumnIndex(COL_DEVICE)),
                        cursor.getString(cursor.getColumnIndex(COL_LOCATION))
                ));
            } while (cursor.moveToNext());
        }
        return devices;
    }

    public List<String> getLogList(String device){
        List<String> Logs = new LinkedList<>();
        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + LOG_TABLE + " WHERE " + COL_DEVICE + " = '" + device + "'" , null);
        if (cursor.moveToFirst()) {
            do {
                Logs.add(
                        cursor.getString(cursor.getColumnIndex(COL_LOG))
                );
            } while (cursor.moveToNext());
        }
        return Logs;
    }

}
