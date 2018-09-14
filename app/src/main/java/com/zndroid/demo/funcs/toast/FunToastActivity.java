package com.zndroid.demo.funcs.toast;

import android.os.Bundle;
import android.view.View;

import com.zndroid.common.toast.ZToast;
import com.zndroid.demo.BaseActivity;
import com.zndroid.demo.R;

/**
 * @author lazy
 * @create 2018/9/13
 * @description
 */
public class FunToastActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.funcs_toast_layout);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toast_default:
                ZToast.getDefault().show(FunToastActivity.this, "default toast");
                break;
        }
    }
}
