package com.yikego.market;

import android.util.Log;

/**
 * Created by wll on 14-9-13.
 */
public class LogUtils {
    private static boolean LOG_FLAG = true;

    public static void v(String tag, String msg){
        if (LOG_FLAG)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg){
        if (LOG_FLAG)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg){
        if (LOG_FLAG)
            Log.e(tag, msg);
    }
    
    public static void i(String tag, String msg){
        if (LOG_FLAG)
            Log.i(tag, msg);
    }
    public static void w(String tag, String msg){
        if (LOG_FLAG)
            Log.w(tag, msg);
    }
}
