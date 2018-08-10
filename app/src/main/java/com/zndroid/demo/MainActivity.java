package com.zndroid.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zndroid.demo.funcs.click.FuncClickActivity;
import com.zndroid.demo.funcs.log.FuncLogActivity;
import com.zndroid.demo.funcs.virbar.FuncVirBarActivity;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
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
