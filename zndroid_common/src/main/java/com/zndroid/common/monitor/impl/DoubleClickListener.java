package com.zndroid.common.monitor.impl;

import android.os.SystemClock;
import android.view.View;

import com.zndroid.common.monitor.CommonClickListener;

/**
 * @author lazy
 * @create 2018/7/31
 * @description please use 'your_view.setOnClickListener(new DoubleClickListener...)'
 */
public abstract class DoubleClickListener extends CommonClickListener implements View.OnClickListener {

    public abstract void onDoubleClickListener(View view);

    private final long[] mHits = new long[2];

    @Override
    public void onClick(View view) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();

        int DELAY_TIME = 500;
        if (DELAY_TIME > (SystemClock.uptimeMillis() - mHits[0])) {
            onDoubleClickListener(view);
        }
    }
}
