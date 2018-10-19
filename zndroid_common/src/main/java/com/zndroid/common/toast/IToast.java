package com.zndroid.common.toast;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author lazy
 * @create 2018/8/31
 * @description
 */
public interface IToast {
    int SHOW_SHORT = 0x000;
    int SHOW_LONG = 0x111;

    IToast with(@NonNull Context context);
    /** default time (short)*/
    void show(@NonNull String content);
    /** long time (long)*/
    void showLong(@NonNull String content);
}
