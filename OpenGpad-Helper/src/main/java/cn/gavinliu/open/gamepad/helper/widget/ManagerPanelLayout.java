package cn.gavinliu.open.gamepad.helper.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.db.DBManager;

/**
 * Created by Gavin on 16-2-23.
 */
public class ManagerPanelLayout extends FrameLayout implements View.OnClickListener {

    private View titleContainer;
    private Button btn_close;
    private Button btn_add;
    private Button btn_save;

    private View keyboardContainer;
    private GridView grid1;
    private GridView grid2;
    private GridView grid3;
    private Button btn_confirm;
    private Button btn_cancel;

    private FrameLayout keyContainer;

    private List<String> keys;
    private final String[] key1 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    private final String[] key2 = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    private final String[] key3 = {"Z", "X", "C", "V", "B", "N", "M"};

    public ManagerPanelLayout(Context context) {
        super(context);
    }

    public ManagerPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManagerPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        keys = new ArrayList<>();

        titleContainer = findViewById(R.id.title_container);
        btn_close = (Button) findViewById(R.id.close);
        btn_add = (Button) findViewById(R.id.add_key);
        btn_save = (Button) findViewById(R.id.save);

        keyboardContainer = findViewById(R.id.keyboard_container);
        grid1 = (GridView) findViewById(R.id.grid);
        grid2 = (GridView) findViewById(R.id.grid2);
        grid3 = (GridView) findViewById(R.id.grid3);
        btn_confirm = (Button) findViewById(R.id.confirm);
        btn_cancel = (Button) findViewById(R.id.cancel);

        keyContainer = (FrameLayout) findViewById(R.id.key_container);


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys.clear();
                keyContainer.removeAllViews();
                ManagerPanelLayout.this.setVisibility(GONE);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardContainer.setVisibility(View.VISIBLE);
                titleContainer.setVisibility(GONE);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyContainer.getChildCount() > 0) {
                    save();
                    Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_LONG).show();
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

        setupKeyboardView(key1, grid1);
        setupKeyboardView(key2, grid2);
        setupKeyboardView(key3, grid3);

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardContainer.setVisibility(View.GONE);
                titleContainer.setVisibility(VISIBLE);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            List<String> tempKeys;

            @Override
            public void onClick(View v) {
                tempKeys = new ArrayList<>();

                getSelectButton(grid1, key1);
                getSelectButton(grid2, key2);
                getSelectButton(grid3, key3);
                checkKeyCount();

                keyboardContainer.setVisibility(View.GONE);
                titleContainer.setVisibility(VISIBLE);
            }

            private void getSelectButton(GridView gridView, String[] ss) {
                long[] ids = gridView.getCheckedItemIds();
                for (long id : ids) {
                    String str = ss[(int) id];
                    tempKeys.add(str);

                    if (!keys.contains(str)) {
                        keys.add(str);

                        KeyButton button = new KeyButton(getContext());
                        button.setBackgroundResource(R.drawable.key_bg);
                        button.setText(str);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.key_width),
                                getResources().getDimensionPixelSize(R.dimen.key_height));
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

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setupKeyboardView(String[] key, GridView gridView) {
        List<String> data = Arrays.asList(key);
        KeyBordAdapter adapter = new KeyBordAdapter();
        adapter.setData(data);
        gridView.setNumColumns(data.size());
        gridView.setAdapter(adapter);
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
            TextView button = new TextView(getContext());
            button.setText(mData.get(position));
            button.setHeight(48 * 3);
            button.setBackgroundResource(R.drawable.keyboard_bg);
            button.setGravity(Gravity.CENTER);

            return button;
        }
    }
}
