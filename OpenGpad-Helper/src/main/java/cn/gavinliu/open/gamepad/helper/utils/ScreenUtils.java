package cn.gavinliu.open.gamepad.helper.utils;

import android.content.Context;

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

}
