package cn.gavinliu.open.gamepad.support;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cn.gavinliu.open.gamepad.support.framework.TouchEvent;
import cn.gavinliu.open.gamepad.support.framework.WFTouchLayer;

/**
 * Created by Gavin on 2016/01/05.
 */
@RunWith(AndroidJUnit4.class)
public class SupportKit {

    private static final String TAG = "SupportKit";

    private Context mContext;
    private UiDevice mDevice;
    private boolean mIsLoop;
    private WFTouchLayer mTouchLayer;
    private Instrumentation mInstrumentation;

    private List<TouchEvent> eventList;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mTouchLayer = new WFTouchLayer(mDevice, mInstrumentation);

        mIsLoop = true;

        eventList = new ArrayList<TouchEvent>();
    }

    @After
    public void setDown() {
        mIsLoop = false;
        eventList.clear();
    }

    @Test
    public void startTest() throws Exception {
        ServerSocket server = new ServerSocket(9000);

        while (mIsLoop) {
            Socket socket = server.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            byte[] bytes = new byte[1024];
            int len;
            StringBuilder builder = new StringBuilder();
            if ((len = inputStream.read(bytes)) != -1) {
                builder.append(new String(bytes, 0, len, "UTF-8"));
            }

            String request = builder.toString();
            Log.d(TAG, "request: " + request);

            if ("CLOSE".equals(request)) {
                socket.close();
                mIsLoop = false;
                return;
            }

            TouchEvent event = JSON.parseObject(request, TouchEvent.class);

            eventList.remove(event);
            eventList.add(event);

            Log.d(TAG, "Size :" + eventList.size());
            mTouchLayer.multiTouch(eventList);

            socket.close();
        }

        server.close();
    }

}
