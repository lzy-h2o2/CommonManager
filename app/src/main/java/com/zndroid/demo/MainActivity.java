package com.zndroid.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.zndroid.demo.funcs.click.FuncClickActivity;
import com.zndroid.demo.funcs.log.FuncLogActivity;
import com.zndroid.demo.funcs.toast.FuncToastActivity;
import com.zndroid.demo.funcs.virbar.FuncVirBarActivity;
import com.zndroid.demo.funcs.widgets.FuncWidgetsActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends BaseActivity implements View.OnLongClickListener{

    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);

        root.setOnLongClickListener(this);

//        setHideBar("1");
    }

    public void setHideBar(String value) {
        try {
            FileOutputStream fos = new FileOutputStream("var/etc/sys_bar");//test for dnake
            fos.write(value.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test0:
                jump(FuncClickActivity.class);
                break;
            case R.id.test1:
                jump(FuncLogActivity.class);
                break;
            case R.id.test2:
                jump(FuncVirBarActivity.class);
                break;
            case R.id.test3:
                jump(FuncToastActivity.class);
                break;
            case R.id.test4:
                jump(FuncWidgetsActivity.class);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                finish();
                break;
        }
        return true;
    }

    private void jump(Class c) {
        startActivity(new Intent(this, c));
    }
}
