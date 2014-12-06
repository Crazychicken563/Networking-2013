package webshtuff;

public class Platform {

    private double xCoord;
    private double yCoord;
    private double xCoord2;
    private double yCoord2;
    private double length;
    private double slope;
    //Four data points

    public Platform(double x, double y, double x2, double y2) {
        if (x <= x2) {
            xCoord = x;
            yCoord = y;
            xCoord2 = x2;
            yCoord2 = y2;
        } else {
            xCoord = x2;
            yCoord = y2;
            xCoord2 = x;
            yCoord2 = y;
        }
        length = Math.sqrt((xCoord2 - xCoord) * (xCoord2 - xCoord) + (yCoord2 - yCoord) * (yCoord2 - yCoord));
        slope = (yCoord2 - yCoord) / (xCoord2 - xCoord);
    }

    public double getX() {
        return xCoord;
    }

    public double getY() {
        return yCoord;
    }

    public double getLength() {
        return length;
    }

    public double getSlope() {
        return slope;
    }

    public double getX2() {
        return xCoord2;
    }

    public double getY2() {
        return yCoord2;
    }
}