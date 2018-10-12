package com.zndroid.common.toast;

import android.content.Context;

/**
 * @author lazy
 * @create 2018/8/31
 * @description
 */
public interface IToast {
    int SHOW_SHORT = 0x000;
    int SHOW_LONG = 0x111;
    int SHOW_DEFINED = 0x222;
    /** default time (short)*/
    void show(Context context, String content);
    /** long time (long)*/
    void showLong(Context context, String content);
}
