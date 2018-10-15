package com.zndroid.common.toast.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zndroid.common.R;
import com.zndroid.common.toast.IToast;

/**
 * @author lazy
 * @create 2018/10/12
 * @description
 */
public class ZToastPlus implements IToast {
    private int mWidth;
    private int mHeight;
    private int mResId = -1;
    private int mAnimationsId = -1;

    private final int SHORT_DURATION_TIMEOUT = 4000;//units ： ms
    private final int LONG_DURATION_TIMEOUT = 7000;//units ： ms

    private final int WHAT_SHOW = 0x123;
    private final int WHAT_HIDE = 0x223;

    private boolean isShow = false;
    private boolean isShowImage = false;
    private boolean isClickable = false;
    private boolean isShowAnimation = false;

    private final String KEY = "zcomm_toast_msg";

    private ImgPosition mImgPosition = ImgPosition.LEFT;
    private ToastPosition mToastPosition = ToastPosition.BOTTOM;


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private View mContainerView;
    private ImageView mImageView;
    private TextView mTextView;
    private RelativeLayout mRootRelativeLayout;

    private Handler mHandler;
    private CallBack mCallBack;

    public enum ImgPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public enum ToastPosition {
        TOP,
        CENTER,
        BOTTOM
    }

    public interface CallBack {
        void onClick();
    }

    private void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        View view = LayoutInflater.from(context).inflate(R.layout.zcomm_toast_plus_layout, null);
        mContainerView = view;

        mRootRelativeLayout = (RelativeLayout) view.findViewById(R.id.zcomm_toast_plus_root_rl);
        mTextView = (TextView) view.findViewById(R.id.zcomm_toast_plus_tv);

        switch (mToastPosition) {
            case TOP:
                mLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case BOTTOM:
                mLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case CENTER:
                mLayoutParams.gravity = Gravity.CENTER;
                break;
        }

        //添加图片
        if (isShowImage) {
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
                    isShowImage = false;
                    break;

            }

            mImageView = new ImageView(context);
            mImageView.setImageResource(mResId);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidth, mHeight);

            layoutParams.addRule(position, R.id.zcomm_toast_plus_tv);//相对文字的位置
            mRootRelativeLayout.addView(mImageView);
        }

        //添加动画
        if (isShowAnimation) {
            mLayoutParams.windowAnimations = mAnimationsId;
        }

        //是否可点击
        if (!isClickable) {
            mLayoutParams.flags = mLayoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        } else {
            mRootRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _close();
                    if (null != mCallBack)
                        mCallBack.onClick();
                }
            });
        }

    }

    private void catchHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == WHAT_SHOW) {
                    mHandler.sendEmptyMessageDelayed(WHAT_HIDE, msg.arg1);
                    _show(msg.getData().getString(KEY));
                }
                else if (msg.what == WHAT_HIDE)
                    _close();

                super.handleMessage(msg);
            }
        };
    }

    /////////////////////////
    public ZToastPlus setImagerSrc(int drawableId, ImgPosition imgPosition) {
        isShowImage = true;
        mResId = drawableId;
        mImgPosition = imgPosition;
        return this;
    }

    public ZToastPlus withAnimation(boolean showAnimation, int animationsId) {
        mAnimationsId = animationsId;
        isShowAnimation = showAnimation;
        return this;
    }

    public ZToastPlus canClick(boolean clickable, CallBack callBack) {
        isClickable = clickable;
        mCallBack = callBack;
        return this;
    }

    public ZToastPlus showOn(ToastPosition toastPosition) {
        mToastPosition = toastPosition;
        return this;
    }
    /////////////////////////

    private void pushArgsToMessage(Context context, String content, int time) {
        if (null == context)
            throw new UnsupportedOperationException("'context' is 'null', please check it.");

        if (mHandler.hasMessages(WHAT_SHOW))
            mHandler.removeMessages(WHAT_SHOW);//处理 多次触发操作

        Bundle b = new Bundle();
        b.putString(KEY, content);

        Message m = new Message();
        m.setData(b);
        m.arg1 = time;
        m.what = WHAT_SHOW;

        mHandler.sendMessageDelayed(m, 0);//立即显示，之所以用sendMessageDelayed，因为这个方法可取消，用于处理多次触发操作
    }

    private void _show(String msg) {
        if (isShow && null != msg) {
            isShow = true;

            mTextView.setText(msg);
            mWindowManager.addView(mContainerView, mLayoutParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void _close() {
        if (null != mWindowManager && mContainerView.isAttachedToWindow())
            mWindowManager.removeViewImmediate(mContainerView);

        isShow = false;
    }

    @Override
    public void show(Context context, String content) {
        showDefinedTime(context, content, SHORT_DURATION_TIMEOUT);
    }

    @Override
    public void showLong(Context context, String content) {
        showDefinedTime(context, content, LONG_DURATION_TIMEOUT);
    }

    public void showDefinedTime(Context context, String content, int duration) {
        init(context);
        catchHandler();
        pushArgsToMessage(context, content, duration);
    }

    ////////////////////////////////////////////////
    private ZToastPlus(){}

    private static class $$ { private static final ZToastPlus $ = new ZToastPlus();}
    public static ZToastPlus getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
