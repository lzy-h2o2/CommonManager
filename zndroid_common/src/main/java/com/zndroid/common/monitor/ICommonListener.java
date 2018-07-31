package com.zndroid.common.monitor;

import android.view.View;

/**
 * @author lazy
 * @create 2018/7/31
 * @description
 */
public interface ICommonListener {
    void onDoubleClickListener(View view);
    void onNoDoubleClickListener(View view);
    void onLongClickListener(View view);
}
