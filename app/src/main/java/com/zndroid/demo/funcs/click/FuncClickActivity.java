package com.zndroid.demo.funcs.click;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zndroid.common.monitor.impl.DoubleClickListener;
import com.zndroid.common.monitor.impl.LongClickListener;
import com.zndroid.common.monitor.impl.NoDoubleClickListener;
import com.zndroid.demo.BaseActivity;
import com.zndroid.demo.R;

/**
 * @author lazy
 * @create 2018/8/10
 * @description
 */
public class FuncClickActivity extends BaseActivity {
    private Button btn0, btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.funcs_click_layout);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        btn0.setOnLongClickListener(new LongClickListener() {
            @Override
            public void onLongClickListener(View view) {
                Toast.makeText(FuncClickActivity.this, "on long click", Toast.LENGTH_SHORT).show();
            }
        });

        btn1.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClickListener(View view) {
                Toast.makeText(FuncClickActivity.this, "on double click", Toast.LENGTH_SHORT).show();
            }
        });

        btn2.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClickListener(View view) {
                Toast.makeText(FuncClickActivity.this, "on no double click", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
