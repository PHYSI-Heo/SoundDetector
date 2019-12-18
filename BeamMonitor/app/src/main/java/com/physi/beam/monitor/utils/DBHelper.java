package com.physi.beam.monitor.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String TABLE_NAME = "DeviceList";

    private static final String DATABASE_NAME = "BeamMonitor.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COL_DEVICE = "device";
    public static final String COL_LOG = "log";

//    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }
//
//    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//    }

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
        sql.append(COL_DEVICE).append(" TEXT NOT NULL, ");
        sql.append(COL_LOG).append(" TEXT NOT NULL )");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ").append(TABLE_NAME);
        db.execSQL(sql.toString());
        onCreate(db);
    }

    public long insertRow(ContentValues values){
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

//    public long updateRow(String target, ContentValues values){
//        target = COL_DEVICE + " = '" + target + "'";
//        return getWritableDatabase().update(TABLE_NAME, values, target,null);
//    }
//
//    public long deleteRow(String target){
//        target = COL_DEVICE + " = '" + target + "'";
//        return getWritableDatabase().delete(TABLE_NAME, target,null);
//    }

    public String selectRows(String target){
        StringBuilder logData = new StringBuilder();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(TABLE_NAME);
        if(target != null)
            sql.append(" WHERE ").append(COL_DEVICE).append(" = '").append(target).append("'");
        try {
            @SuppressLint("Recycle")
            Cursor cursor = getReadableDatabase().rawQuery(sql.toString(), null);
            while (cursor.moveToNext()) {
                logData.append(cursor.getString(cursor.getColumnIndex(COL_LOG))).append("\n");
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return logData.toString();
    }

}
