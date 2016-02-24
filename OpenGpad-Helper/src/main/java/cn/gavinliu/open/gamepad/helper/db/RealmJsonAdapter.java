package cn.gavinliu.open.gamepad.helper.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.gavinliu.open.gamepad.helper.data.FaceButton;
import cn.gavinliu.open.gamepad.helper.data.Rules;

/**
 * Created by Gavin on 16-2-24.
 */
public class RealmJsonAdapter {

    public static String rulesToJsonString(List<Rules> rulesList) {
        JSONArray jsonArray = new JSONArray();
        for (Rules rules : rulesList) {
            jsonArray.put(rulesToJSON(rules));
        }

        return jsonArray.toString();
    }

    private static JSONObject rulesToJSON(Rules rules) {
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (FaceButton faceButton : rules.getFaceButtons()) {
            jsonArray.put(faceButtonToJSON(faceButton));
        }

        try {
            object.put("id", rules.getId());
            object.put("faceButtons", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static JSONObject faceButtonToJSON(FaceButton faceButton) {
        JSONObject object = new JSONObject();
        try {
            object.put("x", faceButton.getX());
            object.put("y", faceButton.getY());
            object.put("key", faceButton.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
