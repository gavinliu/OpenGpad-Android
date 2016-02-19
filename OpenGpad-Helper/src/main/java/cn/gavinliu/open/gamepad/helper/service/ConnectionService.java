package cn.gavinliu.open.gamepad.helper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 连接PC的Service
 *
 * Created by Gavin on 16-2-19.
 */
public class ConnectionService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    
}
