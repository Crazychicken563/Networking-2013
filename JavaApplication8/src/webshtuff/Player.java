package webshtuff;

import java.util.ArrayList;

public class Player {

    private double x;
    private double y;
    private boolean xNeg;
    private boolean isJumping;
    private double health;
    private double bulletDamage;
    private int airBurst;
    private int canFall;
    private double speed;
    private int jump;
    private double cooldown;
    private boolean sheildin = false;
    private double sheild = 0;
    private double currSheild = 0;
    private double sheildDelay = 0;
    private HandlesEverythingExceptTheGUI overlord;
    private String playerName;
    private int kills = 0;
    private int deaths = 0;
    private int flagCaps = 0;
    public boolean justCapped = false;
    private boolean alive;
    private int teamNum;
    private boolean carryingFlag = false;
    private double angle = 0;

    public Player(HandlesEverythingExceptTheGUI overlord, double xCoord, double yCoord, double angle, boolean xNeg, double health, int damage, int airBurst, int jump, int drop, int speed, int cooldown, int shield, String name) {
        x = xCoord;
        y = yCoord;
        this.xNeg = xNeg;
        alive = true;
        this.overlord = overlord;
        //if (health != 4) {
        this.health = 10 + (health * 3.0);
        if (shield != 0) {
            sheild = shield * 2;
            currSheild = sheild;
            sheildin = true;
        }
        this.airBurst = airBurst;
        canFall = drop;
        if (speed != 4) {
            this.speed = 1 + (speed * 1.5 / 10);
        } else {
            this.speed = 2;
        }
        if (jump == 2) {
            this.jump = -1;
        } else {
            this.jump = 30 + (jump * 25);
            //System.out.println(this.jump);
        }
        this.cooldown = (2 + (cooldown / 1.5)) / 100.0;//66% almost 2x
        bulletDamage = 1 + (damage * 2.5 / 10);
        if (airBurst >= 1) {
            bulletDamage = bulletDamage * 0.75;
        }
        isJumping = false;
        if (name == null || name.equals("")) {
            playerName = "Player " + this.overlord.client.getPlayerNum();
        } else {
            playerName = name;
        }
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setName(String name) {
        playerName = name;
    }

    public String getName() {
        return playerName;
    }

    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public void setHealth(double h) {
        health = h;
    }

    public void loseHealth(double gop) {
        double h = gop;
        //if (carryingFlag) {
        //    h = h * 1.35;
        //}
        double healthGoinDown = 0;
        currSheild -= h;
        if (currSheild < 0) {
            healthGoinDown = Math.abs(currSheild);
            currSheild = 0;
            if (sheildin) {
                sheildDelay = 5;
            }
        } else {
            if (sheildin) {
                sheildDelay = 2;
            }
        }
        health -= healthGoinDown;
        if (health <= 0 && alive) {
            alive = false;
            overlord.client.someoneDied(overlord.client.getPlayerNum());
        }
    }

    public void died() {
        deaths++;
    }

    public void regen() {
        if (sheildin) {
            if (sheildDelay > 0) {
                sheildDelay -= 0.01;
            }
            if (currSheild < sheild && sheildDelay <= 0) {
                currSheild += 0.003;
            }
        }
    }

    public double getSheild() {
        return currSheild;
    }

    public double getMaxSheild() {
        return sheild;
    }

    public int contact(ArrayList<Bullet> bullets, double distance) {
        int bulletLocationInList = -1;
        for (int i = 0; i < bullets.size(); i++) {
            if (distance(getX() + 38, getY() + 9, bullets.get(i).getX(), bullets.get(i).getY()) <= distance) {
                bulletLocationInList = i;
                i = bullets.size();
            }
        }
        return bulletLocationInList;
    }

    public double getBulletDamage() {
        return bulletDamage;
    }

    public boolean canFall() {
        return canFall == 1;
    }

    public int airBurst() {
        return airBurst;
    }

    public double getSpeed() {
        return speed;
    }

    public int jumpHeight() {
        return jump;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHealth() {
        return health;
    }

    public void setY(double newY) {
        y = newY;
    }

    public void setX(double newX) {
        x = newX;
    }

    public void setXNeg(boolean b) {
        xNeg = b;
    }

    public void finnaJump(boolean derp) {
        isJumping = derp;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void killedAGuy() {
        kills++;
    }

    @Override
    public String toString() {
        return playerName + " on team " + teamNum + " @ position (" + x + "," + y + ")";
    }

    public void setTeamNum(int num) {
        teamNum = num;
    }

    public void pickedUpFlag() {
        carryingFlag = true;
    }

    public void droppedFlag() {
        carryingFlag = false;
    }

    public void capFlag() {
        droppedFlag();
        if (!justCapped) {
            flagCaps++;
            justCapped = true;
        }
    }

    public boolean isCarryingFlag() {
        return carryingFlag;
    }

    public int getTeamNum() {
        return teamNum;
    }

    public int getFlagCaps() {
        return flagCaps;
    }

    public void setFlagCaps(int f) {
        flagCaps = f;
    }

    public Point platformCheck(ArrayList<Platform> a) {
        double heightBuffer = 2;
        Double legitX = x + 38;
        Double legitY = y - heightBuffer;
        for (Platform p : a) {
            double legitLeftRange;
            double legitRightRange;
            if (p.getX() < p.getX2()) {
                legitLeftRange = p.getX() - 0.1;
                legitRightRange = p.getX2() + 0.1;
            } else {
                legitLeftRange = p.getX2() - 0.1;
                legitRightRange = p.getX() + 0.1;
            }
            if (legitLeftRange < legitX && legitRightRange > legitX) {
                double slope = p.getSlope();
                double yInt = p.getY() - (slope * p.getX());
                double yIntercept = p.getSlope() * legitX + yInt;
                if (Math.abs(yIntercept - legitY) <= heightBuffer) {
                    calcAngle(p);
                    return new Point(legitX, yIntercept);
                }
            }
        }
        return null;
    }

    public boolean checkLower(ArrayList<Platform> a) {
        double heightBuffer = 2;
        Double legitX = x + 38;
        Double legitY = y - heightBuffer + 2;
        for (Platform p : a) {
            double legitLeftRange;
            double legitRightRange;
            if (p.getX() < p.getX2()) {
                legitLeftRange = p.getX() - 0.1;
                legitRightRange = p.getX2() + 0.1;
            } else {
                legitLeftRange = p.getX2() - 0.1;
                legitRightRange = p.getX() + 0.1;
            }
            if (legitLeftRange < legitX && legitRightRange > legitX) {
                double slope = p.getSlope();
                double yInt = p.getY() - (slope * p.getX());
                double yIntercept = p.getSlope() * legitX + yInt;
                if (Math.abs(yIntercept - legitY) <= heightBuffer) {
                    calcAngle(p);
                    return true;
                }
            }
        }
        return false;
    }

    public double getAngle() {
        return angle;
    }

    public void calcAngle(Platform p) {
        angle = Math.toDegrees(Math.atan((p.getY2() - p.getY()) / (p.getX2() - p.getX())));
    }

    public void setAngle(double a) {
        angle = a;
    }

    public boolean xNeg() {
        return xNeg;
    }
}
