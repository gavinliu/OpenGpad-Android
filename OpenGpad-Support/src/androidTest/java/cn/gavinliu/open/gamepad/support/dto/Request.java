package cn.gavinliu.open.gamepad.support.dto;

import java.util.HashMap;

/**
 * Created by GavinLiu on 2016-01-20
 */
public class Request {

    private ConstantEnum.Command command;

    private HashMap<String, String> params;

    public ConstantEnum.Command getCommand() {
        return command;
    }

    public void setCommand(ConstantEnum.Command command) {
        this.command = command;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}
