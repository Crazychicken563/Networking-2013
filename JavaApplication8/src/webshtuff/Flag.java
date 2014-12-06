package webshtuff;

public class Flag {

    private double x;
    private double y;
    private boolean capturable = true;
    private Point base;

    public Flag(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
        base = new Point(x, y);//patch
        x = x - 10;
    }

    public boolean isCapturable() {
        return capturable;
    }

    public void setCapturable(boolean b) {
        capturable = b;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double exs) {
        x = exs;
    }

    public void setY(double why) {
        y = why;
    }

    public double getBaseX() {
        return base.getX();
    }

    public double getBaseY() {
        return base.getY();
    }

    public boolean atBase() {
        if (base.getX() - 10 == x && base.getY() == y) {
            return true;
        }
        return false;
    }

    public void returnToBase() {
        x = base.getX() - 10;
        y = base.getY();
    }
}
