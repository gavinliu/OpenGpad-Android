package cn.gavinliu.open.gamepad.helper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.base.BaseService;
import cn.gavinliu.open.gamepad.helper.ui.MainActivity;

/**
 * 连接PC的Service
 * <p/>
 * Created by Gavin on 16-2-19.
 */
public class ConnectionService extends BaseService {

    public static final String ACTION_SHOW_PANEL = "ACTION_SHOWE_PANEL";

    WindowManager mWM;

    Button mManagerButton;

    View editPanel;

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : "";

        if (ACTION_SHOW_PANEL.equals(action) && mManagerButton == null) {
            mManagerButton = new Button(getApplicationContext());
            mManagerButton.setText(getString(R.string.manage));
            mManagerButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);

                    editPanel = LayoutInflater.from(getApplication()).inflate(R.layout.edit_panel, null, false);

                    WindowManager.LayoutParams layoutParams = createWindowLayoutParams();
                    layoutParams.x = 0;
                    layoutParams.y = 0;
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

                    mWM.addView(editPanel, layoutParams);

                    editPanel.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mManagerButton.setVisibility(View.VISIBLE);
                            mWM.removeView(editPanel);
                        }
                    });

                }
            });

            WindowManager.LayoutParams layoutParams = createWindowLayoutParams();
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = 48 * 3;
            layoutParams.height = 48 * 3;
            mWM.addView(mManagerButton, layoutParams);
        } else {
            notify(getString(R.string.no_connection));
        }

        return START_STICKY;
    }

    private void notify(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();

        startForegroundCompat(R.string.app_name, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForegroundCompat(R.string.app_name);

        if (mManagerButton != null) {
            mWM.removeView(mManagerButton);
            mManagerButton = null;
        }
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowLayoutParams.alpha = 1.0f;
        windowLayoutParams.gravity = Gravity.START | Gravity.TOP;

        return windowLayoutParams;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
