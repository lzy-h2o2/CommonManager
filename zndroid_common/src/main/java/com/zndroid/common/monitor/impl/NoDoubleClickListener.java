package com.zndroid.common.monitor.impl;

import android.view.View;

import com.zndroid.common.monitor.CommonClickListener;

/**
 * @author lazy
 * @create 2018/7/31
 * @description please use 'your_view.setOnClickListener(new NoDoubleClickListener...)'
 */
public abstract class NoDoubleClickListener extends CommonClickListener implements View.OnClickListener {

    public abstract void onNoDoubleClickListener(View view);

    @Override
    public void onClick(View view) {
        int KEY = -44;
        long DELAY_TIME = 500;

        long lastTime = view.getTag(KEY) == null ? 0 : (long) view.getTag(KEY);
        if (System.currentTimeMillis() - lastTime > DELAY_TIME) {
            onNoDoubleClickListener(view);
        }
        view.setTag(KEY, System.currentTimeMillis());
    }
}
