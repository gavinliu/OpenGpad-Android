package cn.gavinliu.open.gamepad.helper.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

import cn.gavinliu.open.gamepad.helper.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFloatView();
    }

    // test
    private void addFloatView() {
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        windowLayoutParams.alpha = 1.0f;
        windowLayoutParams.gravity = Gravity.START | Gravity.TOP;

        Button button = new Button(getApplicationContext());

        windowLayoutParams.x = 0;
        windowLayoutParams.y = 500;
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowManager.addView(button, windowLayoutParams);
    }

}
