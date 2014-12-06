package webshtuff;

public class Bullet {

    private double x;
    private double y;
    private double dx;
    private double dy;
    private double velocity = 4;

    public Bullet(double xCoord, double yCoord, double angle, boolean xNeg) {
        x = xCoord;
        y = yCoord;
        dx = velocity * Math.cos(Math.toRadians(angle));
        dy = velocity * Math.sin(Math.toRadians(angle));
        if (xNeg) {
            dx = -dx;
            dy = -dy;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void updateLocation() {
        x += dx;
        y += dy;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
