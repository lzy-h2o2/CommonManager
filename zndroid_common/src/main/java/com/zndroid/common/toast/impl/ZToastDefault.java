package com.zndroid.common.toast.impl;

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
    public void show(String msg) {

    }

    ////////////////////////////////////////////////
    private ZToastDefault(){}

    private static class $$ { private static final ZToastDefault $ = new ZToastDefault();}
    public ZToastDefault getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
