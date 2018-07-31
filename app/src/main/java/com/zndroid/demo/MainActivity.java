package com.zndroid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zndroid.common.monitor.impl.DoubleClickListener;
import com.zndroid.common.monitor.impl.LongClickLinstener;
import com.zndroid.common.monitor.impl.NoDoubleClickListener;

public class MainActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.test);

//        textView.setOnClickListener(new DoubleClickListener() {
//            @Override
//            public void onDoubleClickListener(View view) {
//                Toast.makeText(MainActivity.this, "dddddd", Toast.LENGTH_LONG).show();
//            }
//        });

        textView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClickListener(View view) {
                Log.i("hyhy", "nnnn");
            }
        });

//        textView.setOnLongClickListener(new LongClickLinstener() {
//            @Override
//            public void onLongClickListener(View view) {
//                Toast.makeText(MainActivity.this, "llllllll", Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
