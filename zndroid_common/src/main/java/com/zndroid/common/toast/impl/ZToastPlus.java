package com.zndroid.common.toast.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zndroid.common.R;
import com.zndroid.common.toast.IToast;

/**
 * @author lazy
 * @create 2018/10/12
 * @description
 */
public class ZToastPlus implements IToast {
    private int mResId;
    private int mWidth;
    private int mHeight;
    private Position mImgPosition = Position.LEFT;
    private ImageView mImageView;

    private RelativeLayout mRootRelativeLayout;

    public enum Position {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.zcomm_toast_plus_layout, null);
        mImageView = new ImageView(context);
        mImageView.setImageResource(mResId);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidth, mHeight);

        int position = RelativeLayout.ALIGN_PARENT_LEFT;
        switch (mImgPosition) {
            case TOP:
                position = RelativeLayout.ALIGN_PARENT_TOP;
                break;
            case BOTTOM:
                position = RelativeLayout.ALIGN_PARENT_BOTTOM;
                break;
            case LEFT:
                position = RelativeLayout.ALIGN_PARENT_LEFT;
                break;
            case RIGHT:
                position = RelativeLayout.ALIGN_PARENT_RIGHT;
                break;
            default:

                break;

        }

        layoutParams.addRule(position, R.id.zcomm_toast_plus_tv);//相对文字的位置
        mRootRelativeLayout.addView(mImageView);
    }

    public ZToastPlus setImagerSrc(int drawableId, Position position) {
        mResId = drawableId;
        mImgPosition = position;
        return this;
    }

    @Override
    public void show(Context context, String content) {


    }

    @Override
    public void showLong(Context context, String content) {

    }

    ////////////////////////////////////////////////
    private ZToastPlus(){}

    private static class $$ { private static final ZToastPlus $ = new ZToastPlus();}
    public static ZToastPlus getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
