package com.zndroid.common.log;

import android.util.Log;

import com.zndroid.common.BuildConfig;

/**
 * @author lazy
 * @create 2018/8/1 23:02
 * @desc
 * @since
 **/
public class ZLogger {

    private static String TAG = "[" + ZLogger.class.getSimpleName() + "]";
    private boolean isDebug = BuildConfig.DEBUG;

    public static void i(String msg) {
        Log.i(TAG, msg);
    }
}
