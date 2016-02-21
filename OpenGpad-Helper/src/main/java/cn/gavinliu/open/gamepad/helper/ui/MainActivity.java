package cn.gavinliu.open.gamepad.helper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.data.Rules;
import cn.gavinliu.open.gamepad.helper.db.DBManager;
import cn.gavinliu.open.gamepad.helper.service.ConnectionService;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getInstance(getApplication());
                Rules dog = realm.where(Rules.class).findAll().last();
                if (dog != null) {
                    for (FaceButton faceButton : dog.getFaceButtons()) {
                        Log.v("Rules", faceButton.getKey() + " (" + faceButton.getX() + "," + faceButton.getY() + ")");
                    }
                }
                realm.close();
            }
        });
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);

                Intent service = new Intent(this, ConnectionService.class);
                service.setAction(ConnectionService.ACTION_SHOW_PANEL);
                startService(service);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ConnectionService.class));
    }
}
