package com.zndroid.demo.funcs.log;

import android.os.Bundle;

import com.zndroid.common.log.ZLogger;
import com.zndroid.demo.BaseActivity;
import com.zndroid.demo.R;

/**
 * @author lazy
 * @create 2018/8/10
 * @description
 */
public class FuncLogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.funcs_log_layout);
        ZLogger.init(this);
        ZLogger.i("123");
    }
}
