package cn.gavinliu.open.gamepad.support.dto;

/**
 * Created by GavinLiu on 2016-01-20
 */
public class Response<T> {

    private int code;

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
