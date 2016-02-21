package cn.gavinliu.open.gamepad.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import cn.gavinliu.open.gamepad.helper.data.FaceButton;

/**
 * Created by Gavin on 16/2/20.
 */
public class KeyButton extends Button {

    private static final String TAG = "KeyButton";

    private FaceButton faceButton;

    public KeyButton(Context context) {
        super(context, null);
        init();
    }

    public KeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        faceButton = new FaceButton();
    }

    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                x = event.getX();
                y = event.getY();

                break;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();

                setX(getX() + newX - x);
                setY(getY() + newY - y);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }

    public FaceButton getFaceButton() {
        int x = (int) (getX() + getWidth() / 2);
        int y = (int) (getY() + getHeight() / 2);

        faceButton.setX(x);
        faceButton.setY(y);
        faceButton.setKey(getText().toString());

        Log.d(TAG, "KEY: " + getText() + " (" + x + "," + y + ")");
        return faceButton;
    }
}
