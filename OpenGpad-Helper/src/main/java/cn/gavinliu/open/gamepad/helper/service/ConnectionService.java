package cn.gavinliu.open.gamepad.helper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.base.BaseService;
import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.db.DBManager;
import cn.gavinliu.open.gamepad.helper.ui.MainActivity;
import cn.gavinliu.open.gamepad.helper.widget.KeyButton;

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
    View keyboardContainer;
    FrameLayout keyContainer;


    List<String> keys;

    String[] ss = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    String[] ss2 = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    String[] ss3 = {"Z", "X", "C", "V", "B", "N", "M"};

    @Override
    public void onCreate() {
        super.onCreate();
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        keys = new ArrayList<>();
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
                    v.setVisibility(View.GONE);

                    editPanel = LayoutInflater.from(getApplication()).inflate(R.layout.edit_panel, null, false);

                    keyContainer = (FrameLayout) editPanel.findViewById(R.id.key_container);

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

                            keys.clear();
                            keyContainer.removeAllViews();
                        }
                    });

                    keyboardContainer = editPanel.findViewById(R.id.keyboard_container);

                    editPanel.findViewById(R.id.add_key).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            keyboardContainer.setVisibility(View.VISIBLE);
                        }
                    });

                    editPanel.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (keyContainer.getChildCount() > 0) {
                                save();
                            }
                        }

                        private void save() {
                            List<FaceButton> faceButtonList = new ArrayList<>();
                            for (int i = 0; i < keyContainer.getChildCount(); i++) {
                                View view = keyContainer.getChildAt(i);
                                if (view instanceof KeyButton) {
                                    KeyButton button = (KeyButton) view;

                                    FaceButton data = button.getFaceButton();
                                    faceButtonList.add(data);
                                }
                            }
                            DBManager.getInstance().saveRules(faceButtonList);
                        }
                    });


                    List<String> data = Arrays.asList(ss);
                    final GridView gridView = (GridView) editPanel.findViewById(R.id.grid);
                    KeyBordAdapter adapter = new KeyBordAdapter();
                    adapter.setData(data);
                    gridView.setNumColumns(data.size());
                    gridView.setAdapter(adapter);

                    List<String> data2 = Arrays.asList(ss2);
                    final GridView gridView2 = (GridView) editPanel.findViewById(R.id.grid2);
                    KeyBordAdapter adapter2 = new KeyBordAdapter();
                    adapter2.setData(data2);
                    gridView2.setNumColumns(data2.size());
                    gridView2.setAdapter(adapter2);

                    List<String> data3 = Arrays.asList(ss3);
                    final GridView gridView3 = (GridView) editPanel.findViewById(R.id.grid3);
                    KeyBordAdapter adapter3 = new KeyBordAdapter();
                    adapter3.setData(data3);
                    gridView3.setNumColumns(data3.size());
                    gridView3.setAdapter(adapter3);

                    editPanel.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {

                        List<String> tempKeys;

                        @Override
                        public void onClick(View v) {
                            tempKeys = new ArrayList<>();

                            getSelectButton(gridView, ss);
                            getSelectButton(gridView2, ss2);
                            getSelectButton(gridView3, ss3);
                            checkKeyCount();

                            keyboardContainer.setVisibility(View.GONE);
                        }

                        private void getSelectButton(GridView gridView, String[] ss) {
                            long[] ids = gridView.getCheckedItemIds();
                            for (long id : ids) {
                                String str = ss[(int) id];
                                tempKeys.add(str);

                                if (!keys.contains(str)) {
                                    keys.add(str);

                                    KeyButton button = new KeyButton(getApplication());
                                    button.setText(str);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(48 * 3, 48 * 3);
                                    params.gravity = Gravity.CENTER;
                                    keyContainer.addView(button, params);
                                }
                            }
                        }

                        private void checkKeyCount() {
                            for (String key : keys) {
                                boolean isDelete = false;

                                for (int i = 0; i < tempKeys.size(); i++) {
                                    String temp = tempKeys.get(i);

                                    if (key.equals(temp)) {
                                        break;
                                    }

                                    if (i == tempKeys.size() - 1) {
                                        isDelete = true;
                                    }
                                }

                                if (isDelete) {
                                    for (int i = 0; i < keyContainer.getChildCount(); i++) {
                                        View view = keyContainer.getChildAt(i);
                                        if (view instanceof TextView) {
                                            String text = (String) ((TextView) view).getText();
                                            if (key.equals(text)) {
                                                keyContainer.removeView(view);
                                            }
                                        }
                                    }
                                }
                            }
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

    private class KeyBordAdapter extends BaseAdapter {

        List<String> mData;

        public void setData(List<String> data) {
            mData = data;
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView button = new TextView(getApplication());
            button.setText(mData.get(position));
            button.setHeight(48 * 3);
            button.setBackgroundResource(R.drawable.keyboard_bg);
            button.setGravity(Gravity.CENTER);

            return button;
        }
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
