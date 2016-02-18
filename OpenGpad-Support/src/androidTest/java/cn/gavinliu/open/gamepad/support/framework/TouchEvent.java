package cn.gavinliu.open.gamepad.support.framework;

/**
 * Created by Gavin on 16/1/23.
 */
public class TouchEvent {

    public static final int ACTION_DOWN = 1;
    public static final int ACTION_UP = 2;
    public static final int ACTION_SWIPE = 3;

    private int action;
    private int x, y;
    private int endX, endY;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

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

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    @Override
    public String toString() {
        String str = "Action: " + action + ", (" + x + ", " + y + ")";
        return str;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + action;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TouchEvent) {
            TouchEvent other = (TouchEvent) o;
            return other.getX() == x && other.getY() == y;
        }

        return super.equals(o);
    }
}
