package pl.symulacja.robota.dane;

public class Robot {
    private int X;
    private int Y;
    private int R;

    public Robot(int x, int y, int r) {
        X = x;
        Y = y;
        R = r;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }
}
