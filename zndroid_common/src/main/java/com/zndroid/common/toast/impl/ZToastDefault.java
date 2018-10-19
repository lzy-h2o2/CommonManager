package com.zndroid.common.toast.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.zndroid.common.toast.IToast;

/**
 * @author lazy
 * @create 2018/8/31
 * @description default 'Toast' replace system 'Toast' and it support running on sub thread.
 */
public class ZToastDefault implements IToast {
    private static Toast mToast;
    private Handler mHandler;
    private Context mContext;

    private final String KEY = "zcomm_toast_msg";

    /**
     * short time show (2 seconds)  units ：ms
     * please call it at last
     *
     * @param content - String
     * */
    @Override
    public void show(@NonNull String content) {
        pushArgsToMessage(content, IToast.SHOW_SHORT);
    }

    /**
     * short time show (3.5 seconds)  units ：ms
     * please call it at last
     *
     * @param content - String
     * */
    @Override
    public void showLong(@NonNull String content) {
        pushArgsToMessage(content, IToast.SHOW_LONG);
    }

    /**
     * attach Context
     * please call it at first
     * */
    @Override
    public ZToastDefault with(@NonNull Context context) {
        mContext = context.getApplicationContext();
        return this;
    }

    private void _show(String content, int duration) {
        if (null == mToast)
            mToast = Toast.makeText(mContext, content, duration == IToast.SHOW_LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        else
            mToast.setText(content);

        mToast.show();
    }

    private void pushArgsToMessage(String content, int time) {
        if (null == mContext)
            throw new UnsupportedOperationException("'context' is 'null', please check it.");

        Bundle b = new Bundle();
        b.putString(KEY, content);

        Message m = mHandler.obtainMessage();
        m.setData(b);
        m.what = time;

        mHandler.sendMessage(m);
    }

    ////////////////////////////////////////////////
    private ZToastDefault(){
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case IToast.SHOW_SHORT:
                        _show(msg.getData().getString(KEY), IToast.SHOW_SHORT);
                        break;
                    case IToast.SHOW_LONG:
                        _show(msg.getData().getString(KEY), IToast.SHOW_LONG);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private static class $$ { private static final ZToastDefault $ = new ZToastDefault();}
    public static ZToastDefault getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
