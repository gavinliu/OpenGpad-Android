package cn.gavinliu.open.gamepad.helper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.base.BaseService;
import cn.gavinliu.open.gamepad.helper.data.Rules;
import cn.gavinliu.open.gamepad.helper.db.RealmJsonAdapter;
import cn.gavinliu.open.gamepad.helper.ui.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 连接PC的Service
 * <p/>
 * Created by Gavin on 16-2-19.
 */
public class ConnectionService extends BaseService {

    public static final String ACTION_SHOW_PANEL = "ACTION_SHOW_PANEL";
    public static final String ACTION_HIDE_PANEL = "ACTION_HIDE_PANEL";

    private static final String HEART_BEAT_PACKET = "HEART_BEAT_PACKET";

    private static final String ACTION_GET_RULES = "ACTION_GET_RULES";
    private static final String ACTION_HEART_BEAT = "ACTION_HEART_BEAT";

    private WindowManager mWM;

    private View mManagerPanel;
    private Button mManagerButton;

    private ConnectionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String action = intent != null ? intent.getAction() : "";

        if (ACTION_SHOW_PANEL.equals(action) && mManagerButton == null) {
            mManagerButton = new Button(getApplicationContext());
            mManagerButton.setText(getString(R.string.manage));
            mManagerButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if (mManagerPanel == null) {
                        mManagerPanel = LayoutInflater.from(getApplication()).inflate(R.layout.manager_panel, null, false);
                        WindowManager.LayoutParams layoutParams = createWindowLayoutParams();
                        layoutParams.x = 0;
                        layoutParams.y = 0;
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

                        mWM.addView(mManagerPanel, layoutParams);
                    } else {
                        mManagerPanel.setVisibility(View.VISIBLE);
                    }
                }
            });

            WindowManager.LayoutParams layoutParams = createWindowLayoutParams();
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = 48 * 3;
            layoutParams.height = 48 * 3;
            mWM.addView(mManagerButton, layoutParams);
        } else if (ACTION_HIDE_PANEL.equals(action)) {
            removeFloatView();
        } else {
            if (thread == null) {
                thread = new ConnectionThread();
                thread.start();
            }

            notify(getString(R.string.no_connection));
        }

        return START_STICKY;
    }

    private void notify(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(getString(R.string.app_name))
                .setTicker(text)
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

        removeFloatView();
        thread.close();
    }

    private void removeFloatView() {
        if (mManagerPanel != null) {
            mWM.removeView(mManagerPanel);
            mManagerPanel = null;
        }
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

    private class ConnectionThread extends Thread {

        private final String TAG = "ConnectionThread";

        private ServerSocket server;

        private boolean loop;


        public ConnectionThread() {
            loop = true;
        }

        @Override
        public void run() {
            try {
                server = new ServerSocket(9001);
                while (loop) {
                    Socket socket = server.accept();
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                    byte[] bytes = new byte[1024];
                    int len;
                    StringBuilder builder = new StringBuilder();
                    if ((len = inputStream.read(bytes)) != -1) {
                        builder.append(new String(bytes, 0, len, "UTF-8"));
                    }

                    String action = builder.toString();
                    Log.d(TAG, action);

                    if (ACTION_GET_RULES.equals(action)) {
                        Realm realm = Realm.getDefaultInstance();
                        RealmResults<Rules> ruleList = realm.where(Rules.class).findAll();
                        String json = RealmJsonAdapter.rulesToJsonString(ruleList);
                        realm.close();

                        byte[] jsonBytes = json.getBytes();


                        for (int i = 0; i < jsonBytes.length; ) {
                            int length;

                            if ((jsonBytes.length - i) >= 1024) {
                                length = 1024;
                            } else {
                                length = jsonBytes.length - i;
                            }

                            outputStream.write(jsonBytes, i, length);
                            i = i + length;
                        }

                        Log.d(TAG, json);

                    } else if (ACTION_HEART_BEAT.equals(action)) {
                        outputStream.writeBytes(HEART_BEAT_PACKET);
                    } else {
                        outputStream.writeBytes(HEART_BEAT_PACKET);
                    }

                    outputStream.flush();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            loop = false;

            if (server != null && !server.isClosed()) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
