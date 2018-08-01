package com.zndroid.common.monitor.impl;

import com.zndroid.common.monitor.CommonClickListener;

import android.view.View;

/**
 * @author lazy
 * @create 2018/7/31
 * @description please use 'your_view.setOnLongClickListener(new LongClickListener...)'
 */
public abstract class LongClickListener extends CommonClickListener implements View.OnLongClickListener {
    public abstract void onLongClickListener(View view);

    @Override
    public boolean onLongClick(View view) {
        onLongClickListener(view);
        return true;
    }
}
