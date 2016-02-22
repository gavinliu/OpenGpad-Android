package cn.gavinliu.open.gamepad.helper.db;

import android.content.Context;

import java.util.List;
import java.util.UUID;

import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.data.Rules;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

/**
 * Created by Gavin on 16/2/20.
 */
public class DBManager {

    private static DBManager instance;

    private DBManager(Context ctx) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(ctx).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static void createInstance(Context ctx) {
        instance = new DBManager(ctx);
    }

    public static DBManager getInstance() {
        return instance;
    }

    public void save(RealmObject realmObject) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealm(realmObject);
        realm.commitTransaction();
    }

    public <T extends RealmObject> List<T> find(Class<T> clazz) {
        Realm realm = Realm.getDefaultInstance();
        
        return realm.where(clazz).findAll();
    }

    public void saveRules(List<FaceButton> faceButtons) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Rules rules = new Rules();
        rules.setId(UUID.randomUUID().toString());
        rules = realm.copyToRealm(rules);

        for (FaceButton faceButton : faceButtons) {
            rules.getFaceButtons().add(faceButton);
        }

        realm.commitTransaction();
    }

}
