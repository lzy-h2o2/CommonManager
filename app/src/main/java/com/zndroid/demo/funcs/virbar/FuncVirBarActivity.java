package com.zndroid.demo.funcs.virbar;

import android.os.Bundle;

import com.zndroid.common.virbar.VirtualBarHelper;
import com.zndroid.demo.BaseActivity;
import com.zndroid.demo.R;

/**
 * @author lazy
 * @create 2018/8/10
 * @description
 */
public class FuncVirBarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VirtualBarHelper.getHelper().hideBar(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.funcs_vir_bar_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        VirtualBarHelper.getHelper().hideBar(this);
    }
}
