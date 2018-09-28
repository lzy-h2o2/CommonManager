package com.zndroid.common.virbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * @author lazy
 * @create 2018/8/7
 * @description 底部虚拟按键的隐藏与关闭
 * （to see '/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/PhoneStatusBar.java' 的 addNavigationBar()方法）
 * 也可以修改源码处理显示逻辑
 *
 * 另外顺便说一下隐藏顶部状态栏的做法，比较简单：
 * 在对应Activity或者直接在Manifest的application节点更改属性"android:theme"为"android:Theme.NoTitleBar.Fullscreen"，
 * 当然也可以继承这个theme或者在自己定义的theme style下添加如下两个属性值：
 * <code>
         "android:windowNoTitle"=true
         "android:windowFullscreen"=true
 * </code>
 * 当然通过代码也可以达到效果,在setContentView(....)上面添加如下代码
 * <code>
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
 * </code>
 */
public class VirtualBarHelper {
    private final static String TAG = "NavigationBarHelper";

    private VirtualBarHelper(){}

    private static class $$ {
        private static final VirtualBarHelper $ = new VirtualBarHelper();
    }

    public static VirtualBarHelper getHelper() {
        return $$.$;
    }

    /**
     * <P>判断是否有虚拟按键</P>
     * @param context
     * @return
     */
    public boolean deviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.e("VirtualBarHelper", e.getMessage());
        }
        return hasNavigationBar;
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
        boolean isImmersiveModeEnabled = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        }
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
            //先取 非 后再 与， 把对应位置的1 置成0，原本为0的还是0
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            if (Build.VERSION.SDK_INT >= 19) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }

    public void hideBar(Activity activity) {
        // The UI options currently enabled are represented by a bit field.
        // getSystemUiVisibility() gives us that bit field.
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        }
        if (!isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode on. ");

            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

//            if (Build.VERSION.SDK_INT >= 16) {
//                newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//            }//这样为全部隐藏

            if (Build.VERSION.SDK_INT >= 19) {
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

            Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.systemUiVisibility = newUiOptions;
            window.setAttributes(params);
        }
    }
}
