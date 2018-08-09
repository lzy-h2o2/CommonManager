package com.zndroid.common.virbar;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author lazy
 * @create 2018/8/7
 * @description 底部虚拟按键的隐藏与关闭
 * （to see '/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/PhoneStatusBar.java'）
 * 也可以修改源码处理显示逻辑
 */
public class VirtualBarHelper {
    private final static String TAG = "NavigationBarHelper";

    private VirtualBarHelper(){}

    private static class $$ {
        private static VirtualBarHelper $ = new VirtualBarHelper();
    }

    public static VirtualBarHelper getHelper() {
        return $$.$;
    }

    /** *
     *  判断设备是否存在NavigationBar
     *  @return true 存在, false 不存在
     *  */
    public boolean deviceHasNavigationBar() {
        boolean haveNav = false;
        try {
            //1.通过WindowManagerGlobal获取windowManagerService
            // 反射方法：IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
            Class<?> windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal");
            Method getWmServiceMethod = windowManagerGlobalClass.getDeclaredMethod("getWindowManagerService");
            getWmServiceMethod.setAccessible(true);
            //getWindowManagerService是静态方法，所以invoke null
            Object iWindowManager = getWmServiceMethod.invoke(null);
            // 2.获取windowMangerService的hasNavigationBar方法返回值
            // 反射方法：haveNav = windowManagerService.hasNavigationBar();
            Class<?> iWindowManagerClass = iWindowManager.getClass();
            Method hasNavBarMethod = iWindowManagerClass.getDeclaredMethod("hasNavigationBar");
            hasNavBarMethod.setAccessible(true);
            haveNav = (Boolean) hasNavBarMethod.invoke(iWindowManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return haveNav;
    }

    public void showBar(Activity activity){
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
            //先取 非 后再 与， 把对应位置的1 置成0，原本为0的还是0
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            } if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }

    public void hideBar(Activity activity) {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (!isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode on. ");
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
//            if (Build.VERSION.SDK_INT >= 16) {
//                newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//            }//这样为全部隐藏
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }
}
