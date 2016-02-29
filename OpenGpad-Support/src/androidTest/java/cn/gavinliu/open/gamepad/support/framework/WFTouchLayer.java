package cn.gavinliu.open.gamepad.support.framework;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.os.SystemClock;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 触摸层：对屏幕点按，滑动，多点操作进行封装。
 * <p/>
 * Created by Gavin on 2016/01/05.
 */
public class WFTouchLayer {

    private static final String TAG = "WFTouchLayer";

    private UiDevice mDevice;
    private UiAutomation mUiAutomation;

    public WFTouchLayer(UiDevice mDevice, Instrumentation mInstrumentation) {
        this.mDevice = mDevice;
        mUiAutomation = mInstrumentation.getUiAutomation();
    }

    /**
     * 点击
     */
    public void tap(int x, int y) {
        mDevice.click(x, y);
    }

    /**
     * 滑动
     */
    public void swipe(final int x1, final int y1, final int x2, final int y2) {
        mDevice.swipe(x1, y1, x2, y2, 1000);
    }

    /**
     * 按下
     */
    public void touchDown(int x, int y, int duration) {
        mTouchDownTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(mTouchDownTime, mTouchDownTime + duration, MotionEvent.ACTION_DOWN, x, y, 0);
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        mUiAutomation.injectInputEvent(event, true);
    }

    /**
     * 抬起
     */
    public void touchUp(int x, int y) {
        MotionEvent event = MotionEvent.obtain(mTouchDownTime, mTouchDownTime, MotionEvent.ACTION_UP, x, y, 0);
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        mUiAutomation.injectInputEvent(event, true);
    }

    /**
     * 多点
     */
    public synchronized void multiTouch(List<TouchEvent> events) {
        if (preTouch) {
            touchUp(mTouchX, mTouchY);
        } else {
            performMultiPointerUp();
        }

        Log.d(TAG, "touchUp: " + mTouchX + "," + mTouchY);

        if (events.isEmpty()) {
            return;
        }

        List<PointerCoords[]> coordsList = new ArrayList<PointerCoords[]>();

        for (TouchEvent event : events) {
            switch (event.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    PointerCoords[] pointers = new PointerCoords[1];
                    pointers[0] = createPoint(event.getX(), event.getY());

                    coordsList.add(pointers);
                    break;

                case TouchEvent.ACTION_UP:

                    break;

                case TouchEvent.ACTION_SWIPE:

                    break;
            }
        }

        PointerCoords[][] pointers = coordsList.toArray(new PointerCoords[1][2]);

        Log.d(TAG, "Size: " + coordsList.size());
        if (coordsList.size() > 1) {
            performMultiPointers(pointers);
            performMultiPointerDown();
            preTouch = false;
        } else if (coordsList.size() == 1) {
            if (coordsList.get(0) != null && coordsList.get(0).length > 0) {
                mTouchX = (int) pointers[0][0].x;
                mTouchY = (int) pointers[0][0].y;
                touchDown(mTouchX, mTouchY, 0);
                preTouch = true;
                Log.d(TAG, "touchDown: " + mTouchX + "," + mTouchY);
            }
        } else {
            Log.d(TAG, "tap: ");
        }

    }

    private int mTouchX, mTouchY;
    private long mTouchDownTime;

    private PointerCoords[][] mTouches;
    private PointerCoords[] mPointerCoords;
    private PointerProperties[] mProperties;

    private int mMaxSteps;
    private long mDownTime;

    private boolean preTouch = false;

    /**
     * step 1
     */
    private void performMultiPointers(PointerCoords[]... touches) {
        if (touches.length < 2) {
            throw new IllegalArgumentException("Must provide coordinates for at least 2 pointers");
        }

        mTouches = touches;

        mMaxSteps = 0;
        for (PointerCoords[] touch : touches) {
            mMaxSteps = (mMaxSteps < touch.length) ? touch.length : mMaxSteps;
        }

        // specify the properties for each pointer as finger touch
        mProperties = new PointerProperties[touches.length];
        mPointerCoords = new PointerCoords[touches.length];
        for (int x = 0; x < touches.length; x++) {
            MotionEvent.PointerProperties prop = new MotionEvent.PointerProperties();
            prop.id = x;
            prop.toolType = MotionEvent.TOOL_TYPE_FINGER;
            mProperties[x] = prop;

            // for each pointer set the first coordinates for touch down
            mPointerCoords[x] = touches[x][0];
        }
    }

    /**
     * step 2
     */
    private boolean performMultiPointerDown() {
        if (mTouches == null) {
            return false;
        }

        boolean ret = true;
        MotionEvent event;

        // Touch down all pointers
        mDownTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 1,
                mProperties, mPointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        ret &= mUiAutomation.injectInputEvent(event, true);

        for (int x = 1; x < mTouches.length; x++) {
            event = MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_DOWN, x), x + 1, mProperties,
                    mPointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
            ret &= mUiAutomation.injectInputEvent(event, true);
        }

//        // Move all pointers
//        for (int i = 1; i < mMaxSteps - 1; i++) {
//            // for each pointer
//            for (int x = 0; x < mTouches.length; x++) {
//                // check if it has coordinates to move
//                if (mTouches[x].length > i)
//                    mPointerCoords[x] = mTouches[x][i];
//                else
//                    mPointerCoords[x] = mTouches[x][mTouches[x].length - 1];
//            }
//
//            event = MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(),
//                    MotionEvent.ACTION_MOVE, mTouches.length, mProperties, mPointerCoords, 0, 0, 1, 1,
//                    0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
//
//            ret &= mUiAutomation.injectInputEvent(event, true);
//            SystemClock.sleep(5);
//        }

        return ret;
    }

    /**
     * step 3
     */
    private boolean performMultiPointerUp() {
        if (mTouches == null) {
            return false;
        }

        boolean ret = true;
        MotionEvent event;

        // For each pointer get the last coordinates
        for (int x = 0; x < mTouches.length; x++)
            mPointerCoords[x] = mTouches[x][mTouches[x].length - 1];

        // touch up
        for (int x = 1; x < mTouches.length; x++) {
            event = MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_UP, x), x + 1, mProperties,
                    mPointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
            ret &= mUiAutomation.injectInputEvent(event, true);
        }

        // first to touch down is last up
        event = MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 1,
                mProperties, mPointerCoords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        ret &= mUiAutomation.injectInputEvent(event, true);

        return ret;
    }

    private int getPointerAction(int motionEvent, int index) {
        return motionEvent + (index << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
    }

    private PointerCoords createPoint(int x, int y) {
        PointerCoords pointerCoords = new PointerCoords();
        pointerCoords.x = x;
        pointerCoords.y = y;
        pointerCoords.pressure = 1;
        pointerCoords.size = 1;
        return pointerCoords;
    }

}
