package com.physi.beam.monitor.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.physi.beam.monitor.R;

/**
 * Created by Heo on 2018-02-09.
 */

public class NotifyDialog {

    public void show(Context context, String title, String message,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(btnText, clickListener)
                .setCancelable(false).create().show();
    }

    public void show(Context context, int title, int message,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, clickListener)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String title, int layoutResId,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layoutResId)
                .setPositiveButton(btnText, clickListener)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String title, View view)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(android.R.string.cancel, null)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String message,
                     String positiveBtnText, DialogInterface.OnClickListener positiveClickListener,
                     String negativeBtnText, DialogInterface.OnClickListener negativeClickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(null)
                .setMessage(message)
                .setPositiveButton(positiveBtnText, positiveClickListener)
                .setNegativeButton(negativeBtnText, negativeClickListener)
                .setCancelable(false).create().show();
    }
}
