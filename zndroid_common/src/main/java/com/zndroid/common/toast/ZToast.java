package com.zndroid.common.toast;

import com.zndroid.common.toast.impl.ZToastDefault;
import com.zndroid.common.toast.impl.ZToastPlus;

/**
 * @author lazy
 * @create 2018/9/13
 * @description
 */
public class ZToast {

    ////////////////////////////////////////////////
    private ZToast(){}

    public static ZToastDefault getDefault() {
        return ZToastDefault.getToast();
    }

    public static ZToastPlus getToastPlus() {
        return ZToastPlus.getToast();
    }
    ////////////////////////////////////////////////
}
