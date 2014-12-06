package webshtuff;

public class EnemyBullet extends Bullet {

    private int playerNum;
    private int index;
    private int airBurstin;

    public EnemyBullet(double x, double y, double angle, boolean xNeg, int pNum, int i, int b) {
        super(x, y, angle, xNeg);
        playerNum = pNum;
        index = i;
        airBurstin = b;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getIndex() {
        return index;
    }

    public void decIndex() {
        index--;
    }

    public int getAirBurst() {
        return airBurstin;
    }
}
