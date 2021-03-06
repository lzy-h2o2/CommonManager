package com.zndroid.demo.funcs.toast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zndroid.common.toast.ZToast;
import com.zndroid.common.toast.impl.ZToastPlus;
import com.zndroid.demo.BaseActivity;
import com.zndroid.demo.R;

/**
 * @author lazy
 * @create 2018/9/13
 * @description
 */
public class FuncToastActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.funcs_toast_layout);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toast_default:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ZToast.getDefault()
                                .with(FuncToastActivity.this)
                                .showLong("default toast");
                    }
                }).start();
                break;
            case R.id.toast_plus:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ZToast.getToastPlus()
                                .with(FuncToastActivity.this)
                                .showOn(ZToastPlus.ToastPosition.BOTTOM)
                                .canClick(new ZToastPlus.CallBack() {
                                    @Override
                                    public void onClick() {
                                        Log.i("hyhy", "im clicked");
                                    }
                                })
                                .withAnimation(ZToastPlus.ToastAnimStyle.ANIMATION_DOCK)
                                .setImageSrc(R.drawable.ic_launcher_background, ZToastPlus.ImgPosition.RIGHT, 10, 10)
                                .showLong("plus toast");
                    }
                }).start();
                break;
        }
    }
}
