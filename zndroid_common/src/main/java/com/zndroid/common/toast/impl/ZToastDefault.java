package com.zndroid.common.toast.impl;

import android.content.Context;
import android.widget.Toast;

import com.zndroid.common.toast.IToast;

/**
 * @author lazy
 * @create 2018/8/31
 * @description
 */
public class ZToastDefault implements IToast{
    private static Toast mToast;
    @Override
    public void show(Context context, String content) {
        if (null == mToast)
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        else
            mToast.setText(content);

        mToast.show();
    }

    @Override
    public void showLong(Context context, String content) {
        if (null == mToast)
            mToast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        else
            mToast.setText(content);

        mToast.show();
    }

    ////////////////////////////////////////////////
    private ZToastDefault(){}

    private static class $$ { private static final ZToastDefault $ = new ZToastDefault();}
    public static ZToastDefault getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
