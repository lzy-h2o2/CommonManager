package com.zndroid.common.toast;

import android.content.Context;

/**
 * @author lazy
 * @create 2018/8/31
 * @description
 */
public interface IToast {
    /** default time (short)*/
    void show(Context context, String content);
    /** long time (long)*/
    void showLong(Context context, String content);
}
