package cn.gavinliu.open.gamepad.helper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import cn.gavinliu.open.gamepad.helper.R;
import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.data.Rules;
import cn.gavinliu.open.gamepad.helper.service.ConnectionService;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);

        ListView listView = (ListView) findViewById(android.R.id.list);

        realm = Realm.getDefaultInstance();
        RealmResults<Rules> ruleList = realm.where(Rules.class).findAll();
        Adapter adapter = new Adapter(this, ruleList, true);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ConnectionService.class));
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, ConnectionService.class);
        intent.setAction(ConnectionService.ACTION_HIDE_PANEL);
        startService(intent);
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

    public class Adapter extends RealmBaseAdapter<Rules> {

        public Adapter(Context context, RealmResults<Rules> realmResults, boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.rules_item, null);
            }

            TextView id = (TextView) convertView.findViewById(R.id.rules_id);
            id.setText(realmResults.get(position).getId());

            String str = "";
            for (FaceButton faceButton : realmResults.get(position).getFaceButtons()) {
                str += "\n" + faceButton.getKey() + " (" + faceButton.getX() + "," + faceButton.getY() + ")";
            }

            TextView rules = (TextView) convertView.findViewById(R.id.rules);
            rules.setText(str);

            return convertView;
        }
    }


}
