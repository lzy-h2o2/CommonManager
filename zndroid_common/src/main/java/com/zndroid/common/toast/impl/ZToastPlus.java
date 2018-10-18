package com.zndroid.common.toast.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zndroid.common.R;
import com.zndroid.common.toast.IToast;

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

    private float scale;
    private float offset = 64.0f;

    private final long SHORT_DURATION_TIMEOUT = 2000;//units ： ms
    private final long LONG_DURATION_TIMEOUT = 3500;//units ： ms

    private final int WHAT_SHOW = 0x123;
    private final int WHAT_HIDE = 0x223;

    private boolean isShow = false;
    private boolean isShowImage = false;
    private boolean isClickable = false;
    private boolean isShowAnimation = false;

    private final String KEY = "zcomm_toast_msg";
    private final String TIME = "zcomm_toast_time";

    private ImgPosition mImgPosition = ImgPosition.LEFT;
    private ToastPosition mToastPosition = ToastPosition.BOTTOM;


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private Bitmap mBitmap = null;
    private View mContainerView;
    private ImageView mImageView;
    private TextView mTextView;
    private RelativeLayout mRootRelativeLayout;

    private Handler mHandler;
    private Context mContext;
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

    private int dp2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public ZToastPlus with(Context context) {
        mContext = context.getApplicationContext();
        scale = context.getResources().getDisplayMetrics().density;
        return this;
    }

    private void init() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = WRAP_CONTENT;
        mLayoutParams.height = WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;//@deprecated for non-system apps. Use {@link #TYPE_APPLICATION_OVERLAY} instead.

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        /**
         * 这里想说明一下mLayoutParams.y设置的目的
         源码中状态栏、虚拟按键资源如下：
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
                mLayoutParams.y = dp2px(offset);
                break;
            case BOTTOM:
                mLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                mLayoutParams.y = dp2px(offset);
                break;
            case CENTER:
                mLayoutParams.gravity = Gravity.CENTER;
                break;
        }

        int positionId = R.layout.zcomm_layout_toast_plus;//默认不显示图片样式
        if (isShowImage) {
            switch (mImgPosition) {
                case TOP:
                    positionId = R.layout.zcomm_layout_toast_plus_top;
                    break;
                case BOTTOM:
                    positionId = R.layout.zcomm_layout_toast_plus_bottom;
                    break;
                case LEFT:
                    positionId = R.layout.zcomm_layout_toast_plus_left;
                    break;
                case RIGHT:
                    positionId = R.layout.zcomm_layout_toast_plus_right;
                    break;

            }
        }

        mContainerView = LayoutInflater.from(mContext).inflate(positionId, null);

        //添加图片
        if (isShowImage) {
            ImageView mImageView = (ImageView) mContainerView.findViewById(R.id.zcomm_toast_plus_img);
            ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
            layoutParams.width = mWidth;
            layoutParams.height = mHeight;

            if (-1 != mResId)
                mImageView.setImageResource(mResId);
            if (null != mBitmap)
                mImageView.setImageBitmap(mBitmap);//允许动态接收图片资源
        }

        mRootRelativeLayout = (RelativeLayout) mContainerView.findViewById(R.id.zcomm_toast_plus_root_rl);
        mTextView = (TextView) mContainerView.findViewById(R.id.zcomm_toast_plus_tv);

        mRootRelativeLayout.setBackgroundResource(mBackgroundId);

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
                    mHandler.sendEmptyMessageDelayed(WHAT_HIDE, msg.getData().getLong(TIME, SHORT_DURATION_TIMEOUT));
                    _show(msg.getData().getString(KEY));
                }
                else if (msg.what == WHAT_HIDE)
                    _close();

                super.handleMessage(msg);
            }
        };
    }

    /////////////////////////
    /**
     * setting Image src of bitmap for show Image Toast
     *
     * @param bitmap
     * @param imgPosition
     * @param height_dp
     * @param width_dp
     * */
    public ZToastPlus setImageSrc(@NonNull Bitmap bitmap, @NonNull ImgPosition imgPosition, float width_dp, float height_dp) {
        isShowImage = true;

        mBitmap = bitmap;
        mImgPosition = imgPosition;
        mWidth = dp2px(width_dp);
        mHeight = dp2px(height_dp);
        return this;
    }

    /**
     * setting Image src of drawableResId for show Image Toast
     *
     * @param drawableResId
     * @param imgPosition
     * @param height_dp
     * @param width_dp
     * */
    public ZToastPlus setImageSrc(@DrawableRes int drawableResId, @NonNull ImgPosition imgPosition, float width_dp, float height_dp) {
        isShowImage = true;

        mResId = drawableResId;
        mImgPosition = imgPosition;
        mWidth = dp2px(width_dp);
        mHeight = dp2px(height_dp);
        return this;
    }

    /**
     * setting Toast animation of animationsId
     *
     * @param animationsId
     * */
    public ZToastPlus withAnimation(@AnimatorRes int animationsId) {
        isShowAnimation = true;

        mAnimationsId = animationsId;
        return this;
    }

    /**
     * setting Toast can click or not
     *
     * @param clickable
     * @param callBack
     * */
    public ZToastPlus canClick(boolean clickable, @NonNull CallBack callBack) {
        isClickable = clickable;
        mCallBack = callBack;
        return this;
    }

    /**
     * 设置Toast显示位置
     *
     * @param toastPosition
     * */
    public ZToastPlus showOn(@Nullable ToastPosition toastPosition) {
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
    public ZToastPlus showOn(@NonNull ToastPosition toastPosition, float offset) {
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
    public ZToastPlus setmBackgroundId(@DrawableRes int mDrawableResId) {
        this.mBackgroundId = mDrawableResId;
        return this;
    }

    /////////////////////////
    private void pushArgsToMessage(Context context, String content, long time) {
        if (null == context)
            throw new UnsupportedOperationException("'context' is 'null', please check it.");

        if (mHandler.hasMessages(WHAT_SHOW)) {
            mHandler.removeMessages(WHAT_SHOW);//处理 多次触发操作
        }

        Bundle b = new Bundle();
        b.putString(KEY, content);
        b.putLong(TIME, time);

        Message m = mHandler.obtainMessage();
        m.setData(b);
        m.what = WHAT_SHOW;

        mHandler.sendMessageDelayed(m, 0);//立即显示，之所以用sendMessageDelayed，因为这个方法可取消，用于处理多次触发操作
    }

    private void _show(String msg) {
        if (!isShow && null != msg) {
            isShow = true;

            mTextView.setText(msg);

            mWindowManager.addView(mRootRelativeLayout, mLayoutParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void _close() {
        if (null != mWindowManager && mContainerView.isAttachedToWindow())
            mWindowManager.removeView(mContainerView);

        isShow = false;
    }

    /**
     * short time show (2 seconds)  units ：ms
     *
     * @param content - String
     * */
    @Override
    public void show(String content) {
        showDefinedTime(content, SHORT_DURATION_TIMEOUT);
    }

    /**
     * long time show (3.5 seconds)  units ：ms
     *
     * @param content - String
     * */
    @Override
    public void showLong(String content) {
        showDefinedTime(content, LONG_DURATION_TIMEOUT);
    }

    /**
     * defined time to show   units ：ms
     *
     * @param content - String
     * @param duration - long  units ：ms
     * */
    public void showDefinedTime(String content, long duration) {
        if (isShow)
            return;//防止多次触发

        init();
        catchHandler();
        pushArgsToMessage(mContext, content, duration);
    }

    ////////////////////////////////////////////////
    private ZToastPlus(){}

    private static class $$ { private static final ZToastPlus $ = new ZToastPlus();}
    public static ZToastPlus getToast() {
        return $$.$;
    }
    ////////////////////////////////////////////////
}
