package cn.gavinliu.open.gamepad.helper.db;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Gavin on 16/2/20.
 */
public class DBManager {

    private static DBManager instance;

    private Realm mRealm;

    private DBManager(Context ctx) {
        mRealm = Realm.getInstance(ctx);
    }

    public static void createInstance(Context ctx) {
        instance = new DBManager(ctx);
    }

    public static DBManager getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void save(RealmObject realmObject) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(realmObject);
        mRealm.commitTransaction();
    }

    public <T extends RealmObject> List<T> find(Class<T> clazz) {
        return mRealm.where(clazz).findAll();
    }

}
