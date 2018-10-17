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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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
    private int mBackgroundId = R.drawable.zcomm_bg_toast_plus;//default background

    private float offset = 64.0f;

    private final int SHORT_DURATION_TIMEOUT = 2000;//units ： ms
    private final int LONG_DURATION_TIMEOUT = 3500;//units ： ms

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

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = WRAP_CONTENT;
        mLayoutParams.height = WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;//@deprecated for non-system apps. Use {@link #TYPE_APPLICATION_OVERLAY} instead.

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        View view = LayoutInflater.from(context).inflate(R.layout.zcomm_layout_toast_plus, null);
        mContainerView = view;

        mRootRelativeLayout = (RelativeLayout) view.findViewById(R.id.zcomm_toast_plus_root_rl);
        mTextView = (TextView) view.findViewById(R.id.zcomm_toast_plus_tv);

        mRootRelativeLayout.setBackgroundResource(mBackgroundId);

        /**
         * 这里想说明一下mLayoutParams.y设置的目的
         源码中状态栏、虚拟按键
         <dimen name="toast_y_offset">24dp</dimen>
         <dimen name="status_bar_height">24dp</dimen>
         <dimen name="navigation_bar_height">48dp</dimen>
         为了显示效果一致，顾，对Y方向添加了偏移量处理
         建议不采用'TOP'形式，因为，状态栏，标题栏高度不可控，如果采用的是默认主题或者原生系统倒是可以通过
         https://blog.csdn.net/a_running_wolf/article/details/50477965
         的方案进行获取，但是不灵活，另外两个方式要在onWindowFocusChanged中处理，
         但是，作为lib无法控制开发者调用的时机，在此，开发者可以通过动态设置偏移量的形式处理 {@link #showOn(ToastPosition, float)}
         * */

        switch (mToastPosition) {
            case TOP:
                mLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                mLayoutParams.y = dp2px(context, offset);
                break;
            case BOTTOM:
                mLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                mLayoutParams.y = dp2px(context, offset);
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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, 60);
            mImageView.setLayoutParams(layoutParams);

            layoutParams.addRule(position, R.id.zcomm_toast_plus_tv);//相对文字的位置
            mRootRelativeLayout.addView(mImageView, layoutParams);
        }

        //添加动画
        if (isShowAnimation) {
            mLayoutParams.windowAnimations = mAnimationsId;
        }

        //是否可点击
        if (!isClickable) {
            mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
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

    /**
     * 设置Toast显示位置
     *
     * @param toastPosition
     * */
    public ZToastPlus showOn(ToastPosition toastPosition) {
        mToastPosition = toastPosition;
        return this;
    }

    /**
     * 设置Toast显示位置
     * 如果设置居中，偏移量无效
     *
     * @param toastPosition
     * @param offset - 自定义偏移量，由开发者自行控制
     * */
    public ZToastPlus showOn(ToastPosition toastPosition, float offset) {
        this.offset = offset;
        mToastPosition = toastPosition;
        return this;
    }

    /**
     * 原生Toast背景：'?android:attr/toastFrameBackground'
     * 是一个引用，所以才会因为版本的不同而不同，在源码中是个.9图，此处暴露出方法提供开发者自定义，当然也可采用默认
     *
     * @param mDrawableResId - drawable 资源id
     * */
    public ZToastPlus setmBackgroundId(int mDrawableResId) {
        this.mBackgroundId = mDrawableResId;
        return this;
    }

    /////////////////////////

    private void pushArgsToMessage(Context context, String content, int time) {
        if (null == context)
            throw new UnsupportedOperationException("'context' is 'null', please check it.");

        if (mHandler.hasMessages(WHAT_SHOW)) {
            mHandler.removeMessages(WHAT_SHOW);//处理 多次触发操作
        }

        Bundle b = new Bundle();
        b.putString(KEY, content);

        Message m = mHandler.obtainMessage();
        m.setData(b);
        m.arg1 = time;
        m.what = WHAT_SHOW;

        mHandler.sendMessageDelayed(m, 0);//立即显示，之所以用sendMessageDelayed，因为这个方法可取消，用于处理多次触发操作
    }

    private void _show(String msg) {
        if (!isShow && null != msg) {
            isShow = true;

            mTextView.setText(msg);
            mWindowManager.addView(mContainerView, mLayoutParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void _close() {
        if (null != mWindowManager && mContainerView.isAttachedToWindow())
            mWindowManager.removeView(mContainerView);

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
        if (isShow)
            return;

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
