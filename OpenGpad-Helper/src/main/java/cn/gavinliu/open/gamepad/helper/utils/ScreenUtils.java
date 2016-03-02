package cn.gavinliu.open.gamepad.helper.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by Gavin on 16/2/20.
 */
public class ScreenUtils {

    private Context mContext;

    private static ScreenUtils mInstance;

    public synchronized static void createInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new ScreenUtils(ctx);
        }
    }

    public static ScreenUtils getInstance() {
        return mInstance;
    }

    private ScreenUtils(Context ctx) {
        mContext = ctx;
    }

    public int getWidth() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public int getHeight() {
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }

    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

}
