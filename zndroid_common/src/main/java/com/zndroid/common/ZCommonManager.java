package com.zndroid.common;

/**
 * @author lazy
 * @create 2018/7/31
 * @description
 */
public class ZCommonManager {
    //////////////////////////////////////////////
    private ZCommonManager(){}

    private static class M { private static final ZCommonManager $ = new ZCommonManager();}
    public ZCommonManager getManager() {
        return M.$;
    }
    //////////////////////////////////////////////
}
