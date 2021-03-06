package cn.gavinliu.open.gamepad.helper.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Gavin on 16/2/21.
 */
public class Rules extends RealmObject {

    @PrimaryKey
    private String id;
    private RealmList<FaceButton> faceButtons;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<FaceButton> getFaceButtons() {
        return faceButtons;
    }

    public void setFaceButtons(RealmList<FaceButton> faceButtons) {
        this.faceButtons = faceButtons;
    }
}
