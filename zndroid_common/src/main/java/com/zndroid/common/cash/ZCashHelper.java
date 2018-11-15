package com.zndroid.common.cash;

import android.content.Context;
import android.os.Environment;

/**
 * @author lazy
 * @create 2018/11/15
 * @description catch cash and store log on your local when runtime
 */
public class ZCashHelper {
    private boolean isShowTip = false;
    private boolean isStoreLocal = false;

    private String localPath = "";

    private CallBack callBack;

    public void init(Context context) {
        localPath = Environment.getDataDirectory().getAbsolutePath();
    }

    ////////////////////////////////////////////////////////
    private ZCashHelper(){}

    private static class $$ {
        private static final ZCashHelper $ = new ZCashHelper();
    }

    public static ZCashHelper getHelper() {
        return $$.$;
    }
    ////////////////////////////////////////////////////////

    public interface CallBack {
        void onNextDo();
    }
}
