package cn.gavinliu.open.gamepad.helper.data;

import io.realm.RealmObject;

/**
 * 手柄按钮
 * <p/>
 * Created by Gavin on 16-2-19.
 */
public class FaceButton extends RealmObject{

    private int x;
    private int y;
    private String key;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
