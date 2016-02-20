package cn.gavinliu.open.gamepad.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Gavin on 16/2/20.
 */
public class KeyButton extends TextView {

    private static final String TAG = "KeyButton";

    public KeyButton(Context context) {
        super(context, null);
    }

    public KeyButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public KeyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
