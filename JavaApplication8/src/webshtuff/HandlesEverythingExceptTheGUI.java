package webshtuff;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javax.swing.JComponent;
import sun.awt.image.ToolkitImage;

public class HandlesEverythingExceptTheGUI extends JComponent implements KeyListener {

    private int width;
    private int height;
    private boolean left = false;
    private boolean right = false;
    private boolean down = false;
    private boolean isFalling = false;
    private boolean makeBullet = false;
    private boolean reallyFast = false;
    private boolean easterEgg;
    private int jumpCounter;
    private int bulletDelay;
    public double dx = 1;
    public double dy = 1;
    private ArrayList<Platform> platforms;
    private ArrayList<Bullet> bullets;
    public ArrayList<EnemyBullet> bullets2;
    private ArrayList<Player> players;
    private Image P1TankR;
    private Image P1TankL;
    private Image P1TankRJ;
    private Image P1TankLJ;
    private Image P2TankR;
    private Image P2TankL;
    private Image P2TankRJ;
    private Image P2TankLJ;
    private Image Explosion;
    private Image Explosion2;
    private Image backGround;
    private ArrayList<Image> platformSprites = new ArrayList<Image>();
    public Client client;
    public Point airBurstLocation;
    public Point airBurst2Location;
    private int airBurstDelay;
    private int airBurstDelay2;
    public double coolDown;
    public boolean overHeat;
    public boolean youDead = false;
    private int maxHealth;
    public boolean displayWinner = false;
    public String winnerName = "";
    public StopWatch counter = new StopWatch();
    private Scanner mapReader = null;
    private int gameMode;
    public Flag[] flags;
    private Image Flag0L;
    private Image Flag0R;
    private Image Flag1L;
    private Image Flag1R;
    private Image Flag0Spawn;
    private Image Flag1Spawn;
    private StopWatch timer = new StopWatch();
    private boolean preGame = true;
    private double sideShift;
    private double vertShift;
    private Point Spawn0;
    private Point Spawn1;
    private TextChat chat;
    public ArrayList<TextChat> chats = new ArrayList<TextChat>();
    public double flagHealthBack = 0;
    private boolean dispScores = false;
    private int theme;

    public void init(Client cl, int health, int damage, int airBurst, int jump, int drop, int speed, int cooldown, int shield, boolean swag, int map, int gm, ArrayList<Integer> teams, int theme, ArrayList<String> names) {
        timer.start();
        client = cl;
        //CTF SUCKS
        //MAKE THE MAP
        try {
            //mapReader = new Scanner(new File(getClass().getResource("maps.txt").getPath()));
            mapReader = new Scanner(new File("maps.txt"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        platforms = new ArrayList<Platform>();
        players = new ArrayList<Player>();
        Queue<Integer> ffaSpawns = new LinkedList<Integer>();
        String mapIndex = "MAP " + map;
        System.out.println("We are looking for map " + mapIndex);
        while (!mapReader.nextLine().equals(mapIndex)) {
            //System.out.println("chillin");
        }
        System.out.println("found dat map");
        String nextMap = "MAP " + (map + 1);
        this.theme = theme;
        mapReader.nextLine();//skipping scale label
        width = Integer.parseInt(mapReader.nextLine());
        height = Integer.parseInt(mapReader.nextLine());
        mapReader.nextLine();//skipping spawn label
        Spawn0 = new Point(Integer.parseInt(mapReader.nextLine()), Integer.parseInt(mapReader.nextLine()));
        Spawn1 = new Point(Integer.parseInt(mapReader.nextLine()), Integer.parseInt(mapReader.nextLine()));
        mapReader.nextLine();//skipping ffascale label
        String currThing = mapReader.nextLine();
        while (!currThing.equals("FLAGS")) {
            //System.out.println("generating map " + platforms.size());
            ffaSpawns.add(Integer.parseInt(currThing));
            currThing = mapReader.nextLine();
        }
        //ffaSpawns.add(mapReader.nextInt());
        //mapReader.nextLine();//skipping flags label
        flags = new Flag[2];
        flags[0] = new Flag(Integer.parseInt(mapReader.nextLine()), Integer.parseInt(mapReader.nextLine()));
        flags[1] = new Flag(Integer.parseInt(mapReader.nextLine()), Integer.parseInt(mapReader.nextLine()));
        mapReader.nextLine();//skipping platforms label
        currThing = mapReader.nextLine();
        while (!currThing.equals(nextMap)) {
            //System.out.println("generating map " + platforms.size());
            platforms.add(new Platform(Double.parseDouble(currThing), Double.parseDouble(mapReader.nextLine()), Double.parseDouble(mapReader.nextLine()), Double.parseDouble(mapReader.nextLine())));
            currThing = mapReader.nextLine();
        }
        platforms.add(new Platform(0, height, width, height));
        //SPAWN THE PLAYERS
        if (gm == 0) {
            for (int i = 0; i < client.getNumPlayersConnected(); i++) {
                players.add(new Player(this, ffaSpawns.poll(), ffaSpawns.poll(), 0, ffaSpawns.poll() == -1, health, damage, airBurst, jump, drop, speed, cooldown, shield, names.get(i)));
            }
        } else {
            int numRed = 0;
            int numBlue = 0;
            for (int i = 0; i < client.getNumPlayersConnected(); i++) {
                if (teams.get(i) == 0) {
                    players.add(new Player(this, Spawn0.getIntX() + (numRed * 50), Spawn0.getIntY(), 0, false, health, damage, airBurst, jump, drop, speed, cooldown, shield, names.get(i)));
                    numRed++;
                } else {
                    players.add(new Player(this, Spawn1.getIntX() - (numBlue * 50), Spawn1.getIntY(), 0, true, health, damage, airBurst, jump, drop, speed, cooldown, shield, names.get(i)));
                    numBlue++;
                }
            }
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTeamNum(teams.get(i));
        }
        //INSTANCE DATA
        gameMode = gm;
        left = false;
        right = false;
        down = false;
        isFalling = false;
        makeBullet = false;
        easterEgg = swag;
        jumpCounter = 0;
        bulletDelay = 0;
        bullets = new ArrayList<Bullet>();
        bullets2 = new ArrayList<EnemyBullet>();
        //MAKE THOSE SPRITES
        URL theURL;
        if (swag) {
            theURL = getClass().getResource("nyanRS.gif");
            P1TankR = getToolkit().getImage(theURL);
            theURL = getClass().getResource("nyanLS.gif");
            P1TankL = getToolkit().getImage(theURL);
            theURL = getClass().getResource("nyanRS.gif");
            P1TankRJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("nyanLS.gif");
            P1TankLJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("tacnaynRS.gif");
            P2TankR = getToolkit().getImage(theURL);
            theURL = getClass().getResource("tacnaynLS.gif");
            P2TankL = getToolkit().getImage(theURL);
            theURL = getClass().getResource("tacnaynRS.gif");
            P2TankRJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("tacnaynLS.gif");
            P2TankLJ = getToolkit().getImage(theURL);
        } else {
            theURL = getClass().getResource("P1TankR.png");
            P1TankR = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P1TankL.png");
            P1TankL = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P1TankRJ.png");
            P1TankRJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P1TankLJ.png");
            P1TankLJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P2TankR.png");
            P2TankR = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P2TankL.png");
            P2TankL = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P2TankRJ.png");
            P2TankRJ = getToolkit().getImage(theURL);
            theURL = getClass().getResource("P2TankLJ.png");
            P2TankLJ = getToolkit().getImage(theURL);
        }
        theURL = getClass().getResource("Explosion.png");
        Explosion = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Explosion2.png");
        Explosion2 = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag0L.png");
        Flag0L = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag0R.png");
        Flag0R = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag1L.png");
        Flag1L = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag1R.png");
        Flag1R = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag0Spawn.png");
        Flag0Spawn = getToolkit().getImage(theURL);
        theURL = getClass().getResource("Flag1Spawn.png");
        Flag1Spawn = getToolkit().getImage(theURL);
        if (swag) {
            theURL = getClass().getResource("NyanBK.png");
            backGround = getToolkit().getImage(theURL);
            for (int i = 1; i < 8; i++) {
                theURL = getClass().getResource("nyan" + i + ".png");
                platformSprites.add(getToolkit().getImage(theURL));
            }
        } else {
            if (this.theme == 1) {
                theURL = getClass().getResource("SpaceBK.jpg");
                backGround = getToolkit().getImage(theURL);
                for (int i = 1; i < 5; i++) {
                    theURL = getClass().getResource("AsteroidPlatform" + i + ".png");
                    platformSprites.add(getToolkit().getImage(theURL));
                }
                for (int i = 1; i < 5; i++) {
                    theURL = getClass().getResource("AsteroidPlatform" + i + ".png");
                    platformSprites.add(getToolkit().getImage(theURL));
                }
            } else if (this.theme == 2) {
                theURL = getClass().getResource("JungleBK.jpg");
                backGround = getToolkit().getImage(theURL);
                for (int i = 1; i < 8; i++) {
                    theURL = getClass().getResource("GrassPlatform.png");
                    platformSprites.add(getToolkit().getImage(theURL));
                }
            }
        }
        //DO SOME UPGRADES AND STUFF
        double fun = players.get(client.getPlayerNum()).getSpeed();
        if (fun != 2) {
            dx = 2 * fun;
        } else {
            reallyFast = true;
        }
        //MORE INSTANCE DATA
        dy = 2;
        airBurstLocation = new Point(-100, -100);
        airBurst2Location = new Point(-100, -100);
        airBurstDelay = 0;
        airBurstDelay2 = 0;
        coolDown = 0;
        overHeat = false;
        maxHealth = (int) players.get(client.getPlayerNum()).getHealth();
        //AFTER WE HAVE MADE THE MAP AND PLAYERS, LETS DO SOME MATH
        //1200 IS SCREEN WIDTH AND THAT IS FINAL
        //600 IS HALF OF 1200
        //DO SOME MORE STUFF
        this.addKeyListener(this);
        if (!client.getMasterUnit().hardcoreOn) {
            counter.start();
        }
        /*
         System.out.println("I AM A HEETG AND MY PLAYERS HAVE THESE TEAM NUMBERS:");
         for (Player p : players) {
         System.out.println(p);
         }
         * */
        if (swag) {
            this.theme = 3;
        }
        for (int i = 0; i < client.getPlayerNum() + 1; i++) {
            client.someoneMoved(client.getPlayerNum(), players.get(client.getPlayerNum()).getX(), players.get(client.getPlayerNum()).getY(), players.get(client.getPlayerNum()).getAngle(), players.get(client.getPlayerNum()).xNeg(), players.get(client.getPlayerNum()).isJumping());
            //client.disperseName(client.getPlayerNum(), players.get(client.getPlayerNum()).getName());
        }
    }

    public void run() {
        repaint();
        try {
            if (chats.size() > 10) {
                chats.remove(0);
            }
            for (int i = 0; i < chats.size(); i++) {
                if (chats.get(i).isDead()) {
                    chats.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
        }
        if (preGame) {
            calcShift();
            if (timer.getTime() > 10) {
                preGame = false;
            }
        }
        if (!youDead & !preGame) {
            changePosition();
            players.get(client.getPlayerNum()).regen();
            if (reallyFast) {
                //this is so stupid
                changePosition();
                changePosition();
                changePosition();
            }
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).updateLocation();
                if (bullets.get(i).getX() < 0 || bullets.get(i).getX() > width) {
                    bullets.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < bullets2.size(); i++) {
                bullets2.get(i).updateLocation();
                if (bullets2.get(i).getX() < 0 || bullets2.get(i).getX() > width) {
                    bullets2.remove(i);
                    i--;
                }
            }
            //im not going to balance the hitbox
            int distance = 14;//balance the hit box
            if (players.get(client.getPlayerNum()).airBurst() == 1) {
                distance = 40;//balance the hit box
            } else if (players.get(client.getPlayerNum()).airBurst() == 2) {
                distance = 56;//balance the hit box
            }
            for (int i = 0; i < client.getNumPlayersConnected(); i++) {
                if (players.get(i).getTeamNum() != players.get(client.getPlayerNum()).getTeamNum()) {
                    int bulletHit = players.get(i).contact(bullets, distance);
                    if (bulletHit != -1) {
                        //System.out.println("I AM PLAYER " + client.getPlayerNum() + " AND I HIT PLAYER " + i);
                        if (players.get(client.getPlayerNum()).airBurst() == 1) {
                            airBurstLocation.setLocation(bullets.get(bulletHit).getX(), bullets.get(bulletHit).getY());
                        } else if (players.get(client.getPlayerNum()).airBurst() == 2) {
                            airBurst2Location.setLocation(bullets.get(bulletHit).getX(), bullets.get(bulletHit).getY());
                        }
                        bullets.remove(bulletHit);
                        client.hit(i, players.get(client.getPlayerNum()).getBulletDamage(), client.getPlayerNum(), bulletHit);
                    }
                }
            }
            if (makeBullet && !overHeat) {
                if (bulletDelay == 0) {
                    bulletDelay = 10;
                    bullets.add(new Bullet((int) (players.get(client.getPlayerNum()).getX()) + 38, (int) (players.get(client.getPlayerNum()).getY()), players.get(client.getPlayerNum()).getAngle(), players.get(client.getPlayerNum()).xNeg()));
                    client.someoneShot((players.get(client.getPlayerNum()).getX()) + 38, (players.get(client.getPlayerNum()).getY()), players.get(client.getPlayerNum()).getAngle(), players.get(client.getPlayerNum()).xNeg(), client.getPlayerNum(), bullets.size() - 1, players.get(client.getPlayerNum()).airBurst());
                    makeBullet = false;
                    coolDown += 1.5;
                }
            }
            if (bulletDelay != 0) {
                bulletDelay--;
            }
            if (airBurstLocation.getX() != -100 && airBurstDelay == -1) {
                airBurstDelay = 8;
            }
            if (airBurst2Location.getX() != -100 && airBurstDelay2 == -1) {
                airBurstDelay2 = 8;
            }
            if (airBurstDelay >= 0) {
                airBurstDelay--;
            }
            if (airBurstDelay2 >= 0) {
                airBurstDelay2--;
            }
            if (airBurstDelay == 0) {
                airBurstLocation.setLocation(-100, -100);
                airBurstDelay--;
            }
            if (airBurstDelay2 == 0) {
                airBurst2Location.setLocation(-100, -100);
                airBurstDelay2--;
            }
            if (coolDown >= 10) {
                overHeat = true;
            }
            if (coolDown > 0) {
                if (!overHeat) {
                    coolDown -= players.get(client.getPlayerNum()).getCooldown();
                }
                coolDown -= players.get(client.getPlayerNum()).getCooldown();
            }
            if (coolDown <= 0.000001) {
                overHeat = false;
            }
        }
    }

    public void changePosition() {
        Point intersect = players.get(client.getPlayerNum()).platformCheck(platforms);
        if (players.get(client.getPlayerNum()).jumpHeight() != -1) {
            if (players.get(client.getPlayerNum()).isJumping() && !isFalling) {
                if (players.get(client.getPlayerNum()).getY() >= 0) {
                    players.get(client.getPlayerNum()).setY(players.get(client.getPlayerNum()).getY() - dy);
                }
                if (jumpCounter > 0) {
                    jumpCounter--;
                } else {
                    players.get(client.getPlayerNum()).finnaJump(false);
                    isFalling = true;
                }
            } else if (intersect == null || down) {
                if (players.get(client.getPlayerNum()).getY() < height) {
                    players.get(client.getPlayerNum()).setY(players.get(client.getPlayerNum()).getY() + dy);
                }
                if (players.get(client.getPlayerNum()).checkLower(platforms)) {
                    isFalling = false;
                } else {
                    isFalling = true;
                }
            } else {
                players.get(client.getPlayerNum()).setY(intersect.getY());
                isFalling = false;
            }
        } else {
            if (players.get(client.getPlayerNum()).isJumping()) {
                if (players.get(client.getPlayerNum()).getY() >= 0) {
                    players.get(client.getPlayerNum()).setY(players.get(client.getPlayerNum()).getY() - dy);
                }
                isFalling = false;
            } else if (intersect == null || down) {
                if (players.get(client.getPlayerNum()).getY() < height) {
                    players.get(client.getPlayerNum()).setY(players.get(client.getPlayerNum()).getY() + dy);
                }
                if (players.get(client.getPlayerNum()).checkLower(platforms)) {
                    isFalling = false;
                } else {
                    isFalling = true;
                }
            } else {
                players.get(client.getPlayerNum()).setY(intersect.getY());
                isFalling = false;
            }
        }
        if (!isFalling && players.get(client.getPlayerNum()).isJumping()) {
            if (left) {
                players.get(client.getPlayerNum()).setAngle(-7);
            } else if (right) {
                players.get(client.getPlayerNum()).setAngle(7);
            } else {
                players.get(client.getPlayerNum()).setAngle(0);
            }
        }
        if (isFalling) {
            players.get(client.getPlayerNum()).setAngle(0);
        }
        if (left && players.get(client.getPlayerNum()).getX() - dx + 19 > 0) {
            players.get(client.getPlayerNum()).setX(players.get(client.getPlayerNum()).getX() - dx);
            players.get(client.getPlayerNum()).setXNeg(true);
        }
        if (right && players.get(client.getPlayerNum()).getX() + dx + 56 < width) {
            players.get(client.getPlayerNum()).setX(players.get(client.getPlayerNum()).getX() + dx);
            players.get(client.getPlayerNum()).setXNeg(false);
        }
        if (gameMode == 2) {
            if (flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].isCapturable() && distance(players.get(client.getPlayerNum()).getX() - 20, players.get(client.getPlayerNum()).getY(), flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].getX(), flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].getY()) < 20) {
                int carryOverDeaths = players.get(client.getPlayerNum()).getDeaths();
                int carryOverKills = players.get(client.getPlayerNum()).getKills();
                int carryOverTeamNum = players.get(client.getPlayerNum()).getTeamNum();
                int carryOverFlagCaps = players.get(client.getPlayerNum()).getFlagCaps();
                String carryOverName = players.get(client.getPlayerNum()).getName();
                double xCoord = players.get(client.getPlayerNum()).getX();
                double yCoord = players.get(client.getPlayerNum()).getY();
                boolean xNeg = players.get(client.getPlayerNum()).xNeg();
                double angle = players.get(client.getPlayerNum()).getAngle();
                flagHealthBack = players.get(client.getPlayerNum()).getHealth() - 10;
                double inHealth = 10;
                if (flagHealthBack < 0) {
                    flagHealthBack = 0;
                    inHealth = players.get(client.getPlayerNum()).getHealth();
                }
                players.set(client.getPlayerNum(), new Player(this, xCoord, yCoord, angle, xNeg, 0, 0, 0, 0, 0, 0, 0, 0, carryOverName));
                players.get(client.getPlayerNum()).setHealth(inHealth);
                players.get(client.getPlayerNum()).setDeaths(carryOverDeaths);
                players.get(client.getPlayerNum()).setKills(carryOverKills);
                players.get(client.getPlayerNum()).setTeamNum(carryOverTeamNum);
                players.get(client.getPlayerNum()).setFlagCaps(carryOverFlagCaps);
                //players.get(client.getPlayerNum()).pickedUpFlag();
                double fun = players.get(client.getPlayerNum()).getSpeed();
                if (fun != 2) {
                    dx = 2 * fun;
                } else {
                    reallyFast = true;
                }
                client.getThatFlag(client.getPlayerNum(), players.get(client.getPlayerNum()).getTeamNum());
            } else if (flags[players.get(client.getPlayerNum()).getTeamNum()].isCapturable() && !flags[players.get(client.getPlayerNum()).getTeamNum()].atBase() && distance(players.get(client.getPlayerNum()).getX() - 20, players.get(client.getPlayerNum()).getY(), flags[players.get(client.getPlayerNum()).getTeamNum()].getX(), flags[players.get(client.getPlayerNum()).getTeamNum()].getY()) < 20) {
                client.flagToBase(players.get(client.getPlayerNum()).getTeamNum());
            }
            if (players.get(client.getPlayerNum()).isCarryingFlag()) {
                flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].setX(players.get(client.getPlayerNum()).getX());
                flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].setY(players.get(client.getPlayerNum()).getY());
                if (flags[players.get(client.getPlayerNum()).getTeamNum()].atBase() && distance(flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].getX() - 20, flags[flipTeam(players.get(client.getPlayerNum()).getTeamNum())].getY(), flags[players.get(client.getPlayerNum()).getTeamNum()].getBaseX(), flags[players.get(client.getPlayerNum()).getTeamNum()].getBaseY()) < 20) {
                    int carryOverDeaths = players.get(client.getPlayerNum()).getDeaths();
                    int carryOverKills = players.get(client.getPlayerNum()).getKills();
                    int carryOverTeamNum = players.get(client.getPlayerNum()).getTeamNum();
                    int carryOverFlagCaps = players.get(client.getPlayerNum()).getFlagCaps();
                    double xCoord = players.get(client.getPlayerNum()).getX();
                    double yCoord = players.get(client.getPlayerNum()).getY();
                    boolean xNeg = players.get(client.getPlayerNum()).xNeg();
                    double angle = players.get(client.getPlayerNum()).getAngle();
                    double moreHealth = players.get(client.getPlayerNum()).getHealth();
                    int health, damage, airBurst, jump, drop, speed, cooldown, shield;
                    health = client.getMasterUnit().healthUpgrade;
                    damage = client.getMasterUnit().damageUpgrade;
                    airBurst = client.getMasterUnit().airBurstUpgrade;
                    jump = client.getMasterUnit().jumpUpgrade;
                    drop = client.getMasterUnit().dropUpgrade;
                    speed = client.getMasterUnit().speedUpgrade;
                    cooldown = client.getMasterUnit().cooldownUpgrade;
                    shield = client.getMasterUnit().shieldUpgrade;
                    String name = players.get(client.getPlayerNum()).getName();
                    players.set(client.getPlayerNum(), new Player(client.getMasterUnit().getHandles(), xCoord, yCoord, angle, xNeg, health, damage, airBurst, jump, drop, speed, cooldown, shield, name));
                    players.get(client.getPlayerNum()).setHealth(moreHealth + flagHealthBack);
                    players.get(client.getPlayerNum()).setDeaths(carryOverDeaths);
                    players.get(client.getPlayerNum()).setKills(carryOverKills);
                    players.get(client.getPlayerNum()).setTeamNum(carryOverTeamNum);
                    players.get(client.getPlayerNum()).setFlagCaps(carryOverFlagCaps);
                    //players.get(client.getPlayerNum()).capFlag();
                    double fun = players.get(client.getPlayerNum()).getSpeed();
                    if (fun != 2) {
                        dx = 2 * fun;
                    } else {
                        reallyFast = true;
                    }
                    client.flagCapped(client.getPlayerNum(), players.get(client.getPlayerNum()).getTeamNum());
                }
            }
        }
        calcShift();
        //players.get(client.getPlayerNum()).addTrail(players.get(client.getPlayerNum()).getX(), players.get(client.getPlayerNum()).getY());
        client.someoneMoved(client.getPlayerNum(), players.get(client.getPlayerNum()).getX(), players.get(client.getPlayerNum()).getY(), players.get(client.getPlayerNum()).getAngle(), players.get(client.getPlayerNum()).xNeg(), players.get(client.getPlayerNum()).isJumping());
    }

    public void calcShift() {
        if (width > this.getWidth()) {
            if (players.get(client.getPlayerNum()).getX() > (this.getWidth() / 2) && players.get(client.getPlayerNum()).getX() < (width - (this.getWidth() / 2))) {
                sideShift = (this.getWidth() / 2) - players.get(client.getPlayerNum()).getX();
            } else {
                if (players.get(client.getPlayerNum()).getX() < (this.getWidth() / 2)) {
                    sideShift = 0;
                }
                if (players.get(client.getPlayerNum()).getX() > (width - (this.getWidth() / 2))) {
                    sideShift = (this.getWidth()) - width;
                }
            }
        } else {
            sideShift = 0;
        }
        if (height > this.getHeight()) {
            if (players.get(client.getPlayerNum()).getY() > (this.getHeight() / 2) && players.get(client.getPlayerNum()).getY() < (height - (this.getHeight() / 2))) {
                vertShift = (this.getHeight() / 2) - players.get(client.getPlayerNum()).getY();
            } else {
                if (players.get(client.getPlayerNum()).getY() < (this.getHeight() / 2)) {
                    vertShift = 0;
                }
                if (players.get(client.getPlayerNum()).getY() > (height - (this.getHeight() / 2))) {
                    vertShift = (this.getHeight()) - height;
                }
            }
        } else {
            vertShift = 0;
        }
    }

    public int flipTeam(int x) {
        if (x == 0) {
            return 1;
        }
        return 0;
    }

    public Point spawnPlayer(int teamNum) {
        Point spawn = new Point(0, 0);
        Point curr = new Point(0, 0);
        double ranking = -1;
        double currRanking;
        boolean doILikeIt;
        for (int i = 120; i < width - 120; i += 10) {
            for (int j = 120; j < height - 120; j += 10) {
                curr.setLocation(i, j);
                currRanking = 0;
                for (Player p : players) {
                    if (p.getTeamNum() != teamNum) {
                        double dist = distance(curr.getX(), curr.getY(), p.getX(), p.getY());
                        currRanking += dist;
                    } else {
                        //double dist = distance(curr.getX(), curr.getY(), p.getX(), p.getY());
                        //currRanking += dist;
                    }
                }
                if (gameMode == 2) {
                    double dist = (client.getNumPlayersConnected() / 2) * distance(curr.getX(), curr.getY(), flags[teamNum].getBaseX(), flags[teamNum].getBaseY());
                    if (dist < 300) {
                        doILikeIt = true;
                    } else {
                        doILikeIt = false;
                    }
                    //currRanking -= dist;
                } else {
                    doILikeIt = true;
                }
                if (doILikeIt && currRanking > ranking) {
                    ranking = currRanking;
                    spawn = new Point(curr);
                }
            }
        }
        calcShift();
        return spawn;
    }

    public double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public int getBestPlayer() {
        int best = 0;
        int pNum = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getKills() > best) {
                best = players.get(i).getKills();
                pNum = i;
            }
        }
        return pNum;
    }

    public int getBestCTFPlayer() {
        int best = 0;
        int pNum = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getFlagCaps() > best) {
                best = players.get(i).getFlagCaps();
                pNum = i;
            }
        }
        return pNum;
    }

    public int getBestTeamCTF() {
        int team0 = 0;
        int team1 = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getTeamNum() == 0) {
                team0 += players.get(i).getFlagCaps();
            } else {
                team1 += players.get(i).getFlagCaps();
            }
        }
        if (team0 > team1) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getBestTeam() {
        int team0 = 0;
        int team1 = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getTeamNum() == 0) {
                team0 += players.get(i).getKills();
            } else {
                team1 += players.get(i).getKills();
            }
        }
        if (team0 > team1) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (theme == 0) {
            g.setColor(Color.white);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g.drawImage(backGround, 0, 0, this);
        }
        g.setColor(Color.black);
        //SIDE SHIFT HERE
        for (int i = 0; i < platforms.size(); i++) {
            double x1 = platforms.get(i).getX() + sideShift;
            double y1 = platforms.get(i).getY() + vertShift;
            double x2 = platforms.get(i).getX2() + sideShift;
            double y2 = platforms.get(i).getY2() + vertShift;
            if (theme == 0) {
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            } else {
                double length = Math.abs(platforms.get(i).getX() - platforms.get(i).getX2());
                double xShift = 0;
                double yShift = (y2 - y1) / ((length - xShift) / 10);
                if (platformSprites.get(0).getWidth(this) == 20) {
                    xShift = 10;
                }
                double yCounter = 0;
                boolean b = true;
                for (int j = 0; j < length - xShift; j += 10) {
                    g.drawImage(platformSprites.get((j / 10) % 7), (int) x1 + j, (int) (y1 + yCounter * yShift), this);
                    yCounter++;
                    b = false;
                }
                if (b) {
                    g.drawImage(platformSprites.get(0), (int) x1, (int) y1, this);
                }
            }
        }
        if (theme != 0) {
            g.setColor(Color.white);
        }
        g.drawString("Health", 5, 15);
        g.drawString("Heat", 5, 30);
        g.setColor(Color.blue);
        g.fillRect(45, 5, (int) (players.get(client.getPlayerNum()).getHealth() * 20), 10);
        g.setColor(Color.green);
        g.fillRect(45, 5, (int) (players.get(client.getPlayerNum()).getSheild() * 20 * (maxHealth / players.get(client.getPlayerNum()).getMaxSheild())), 10);
        g.setColor(Color.blue);
        g.drawRect(45, 5, maxHealth * 20, 10);
        if (overHeat) {
            g.setColor(Color.red);
        }
        g.fillRect(36, 20, (int) (coolDown * 10), 10);
        g.drawRect(36, 20, 100, 10);
        g.setColor(Color.black);
        if (theme != 0) {
            g.setColor(Color.white);
        }
        if (!client.getMasterUnit().hardcoreOn && !displayWinner) {
            g.drawString("Time remaining: " + (int) (270 - counter.getTime()) + " sec", this.getWidth() - 200, 15);
        }
        if (gameMode == 2) {
            g.drawImage(Flag0Spawn, (int) (flags[0].getBaseX() + sideShift), (int) ((flags[0].getBaseY() - 46) + vertShift), this);///kijgiytfrdestdrfytgyhujihgfghhkgyuggbyhjubglkuygbliybgilygbligbibgiy
            g.drawImage(Flag1Spawn, (int) (flags[1].getBaseX() + sideShift), (int) ((flags[1].getBaseY() - 46) + vertShift), this);
        }
        for (int i = 0; i < bullets.size(); i++) {
            g.setColor(Color.black);
            g.fillOval((int) (bullets.get(i).getX() + sideShift), (int) ((bullets.get(i).getY() - 20) + vertShift), 5, 5);
            g.setColor(Color.white);
            g.fillOval((int) (bullets.get(i).getX() + sideShift) + 1, (int) ((bullets.get(i).getY() - 19) + vertShift), 3, 3);
        }
        //System.out.println("DRWAIN SONE BULLETS");
        for (int i = 0; i < bullets2.size(); i++) {
            g.setColor(Color.black);
            g.fillOval((int) (bullets2.get(i).getX() + sideShift), (int) ((bullets2.get(i).getY() - 20) + vertShift), 5, 5);
            if (players.get(bullets2.get(i).getPlayerNum()).getTeamNum() == players.get(client.getPlayerNum()).getTeamNum()) {
                g.setColor(Color.white);
            } else {
                g.setColor(Color.red);
            }
            g.fillOval((int) (bullets2.get(i).getX() + sideShift) + 1, (int) ((bullets2.get(i).getY() - 19) + vertShift), 3, 3);
        }
        if (gameMode == 2) {
            int dir0 = 0;
            int dir1 = 0;
            for (Player p : players) {
                if (p.getTeamNum() == 1 && p.isCarryingFlag()) {
                    if (p.xNeg()) {
                        dir0 = -1;
                    } else {
                        dir0 = 1;
                    }
                }
                if (p.getTeamNum() == 0 && p.isCarryingFlag()) {
                    if (p.xNeg()) {
                        dir1 = -1;
                    } else {
                        dir1 = 1;
                    }
                }
            }
            if (dir0 == 1) {
                g.drawImage(Flag0R, (int) ((flags[0].getX() + 40) + sideShift), (int) ((flags[0].getY() - 40) + vertShift), this);
            } else if (dir0 == -1) {
                g.drawImage(Flag0L, (int) ((flags[0].getX() + 40) + sideShift), (int) ((flags[0].getY() - 40) + vertShift), this);
            } else {
                g.drawImage(Flag0L, (int) ((flags[0].getX() + 19) + sideShift), (int) ((flags[0].getY() - 20) + vertShift), this);
            }
            if (dir1 == 1) {
                g.drawImage(Flag1R, (int) ((flags[1].getX() + 40) + sideShift), (int) ((flags[1].getY() - 40) + vertShift), this);
            } else if (dir1 == -1) {
                g.drawImage(Flag1L, (int) ((flags[1].getX() + 40) + sideShift), (int) ((flags[1].getY() - 40) + vertShift), this);
            } else {
                g.drawImage(Flag1L, (int) ((flags[1].getX() + 19) + sideShift), (int) ((flags[1].getY() - 20) + vertShift), this);
            }
        }
        int datXCoord;
        int datYCoord;
        g.setColor(Color.black);
        if (theme != 0) {
            g.setColor(Color.white);
        }
        //BufferedImage b = ((ToolkitImage) P1TankL).getBufferedImage();
        for (int i = 0; i < client.getNumPlayersConnected(); i++) {
            datXCoord = (int) ((players.get(i).getX() + 19) + sideShift);
            if (easterEgg) {
                datYCoord = (int) ((players.get(i).getY() - 27) + vertShift);
            } else {
                datYCoord = (int) ((players.get(i).getY() - 20) + vertShift);
            }
            g.drawString(players.get(i).getName(), datXCoord, datYCoord);
            if (players.get(i).getTeamNum() != players.get(client.getPlayerNum()).getTeamNum()) {
                if (players.get(i).xNeg()) {
                    if (players.get(i).isJumping()) {
                        //g.drawImage(P2TankLJ, datXCoord, datYCoord, this);
                        drawImageRotated(g, P2TankLJ, datXCoord, datYCoord, players.get(i).getAngle());
                    } else {
                        //g.drawImage(P2TankL, datXCoord, datYCoord, this);
                        drawImageRotated(g, P2TankL, datXCoord, datYCoord, players.get(i).getAngle());
                    }
                } else {
                    if (players.get(i).isJumping()) {
                        //g.drawImage(P2TankRJ, datXCoord, datYCoord, this);
                        drawImageRotated(g, P2TankRJ, datXCoord, datYCoord, players.get(i).getAngle());
                    } else {
                        //g.drawImage(P2TankR, datXCoord, datYCoord, this);
                        drawImageRotated(g, P2TankR, datXCoord, datYCoord, players.get(i).getAngle());
                    }
                }
            } else {
                if (players.get(i).xNeg()) {
                    if (players.get(i).isJumping()) {
                        //g.drawImage(P1TankLJ, datXCoord, datYCoord, this);
                        drawImageRotated(g, P1TankLJ, datXCoord, datYCoord, players.get(i).getAngle());
                    } else {
                        //g.drawImage(P1TankL, datXCoord, datYCoord, this);
                        drawImageRotated(g, P1TankL, datXCoord, datYCoord, players.get(i).getAngle());
                    }
                } else {
                    if (players.get(i).isJumping()) {
                        //g.drawImage(P1TankRJ, datXCoord, datYCoord, this);
                        drawImageRotated(g, P1TankRJ, datXCoord, datYCoord, players.get(i).getAngle());
                    } else {
                        //g.drawImage(P1TankR, datXCoord, datYCoord, this);
                        drawImageRotated(g, P1TankR, datXCoord, datYCoord, players.get(i).getAngle());
                    }
                }
            }
        }
        if (chat != null) {
            g.drawString(chat.getMessage(), 15, this.getHeight() / 2);
        }
        for (int i = 0; i < chats.size(); i++) {
            g.drawString(chats.get(i).getMessage(), 15, (this.getHeight() - 20) - (i * 20));
        }
        if (displayWinner) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", 1, 40));
            g.drawString(winnerName + " wins!", 200, 200);
            g.setFont(new Font("Arial", 5, 12));
            g.drawString("Restarting in " + (int) (10 - counter.getTime()) + " seconds", 200, 230);
        }
        if (preGame) {
            g.setColor(Color.red);
            if (gameMode != 0) {
                g.setFont(new Font("Arial", 1, 30));
                g.drawString(client.getMasterUnit().team0Name, (int) (100 + sideShift), 300);
                g.drawString(client.getMasterUnit().team1Name, (int) ((width - 400) + sideShift), 300);
            }
            g.setFont(new Font("Arial", 5, 20));
            g.drawString("Game starts in " + (int) (10 - timer.getTime()) + " sec", this.getWidth() / 2 - 50, 400);
        }
        g.drawImage(Explosion, (int) ((airBurstLocation.getX() - 20) + sideShift), (int) ((airBurstLocation.getY() - 40) + vertShift), this);
        g.drawImage(Explosion2, (int) ((airBurst2Location.getX() - 30) + sideShift), (int) ((airBurst2Location.getY() - 60) + vertShift), this);
        if (dispScores) {
            g.setFont(new Font("Arial", 5, 16));
            //System.out.println("scoress");
            int xPlus = 0;
            if (gameMode == 2) {
                xPlus = 100;
            }
            g.setColor(Color.white);
            g.fillRect((this.getWidth() / 2) - 160, 80, 260 + xPlus, (players.size() * 20) + 10);
            g.setColor(Color.black);
            g.drawRect((this.getWidth() / 2) - 160, 80, 260 + xPlus, (players.size() * 20) + 10);
            for (int i = 0; i < players.size(); i++) {
                String asdf = "";
                asdf += players.get(i).getName() + " got " + players.get(i).getKills() + " kills and " + players.get(i).getDeaths() + " deaths";
                if (gameMode == 2) {
                    asdf += " and " + players.get(i).getFlagCaps() + " flag caps";
                }
                g.drawString(asdf, (this.getWidth() / 2) - 150, 100 + (i * 20));
            }
        }
    }

    public void drawImageRotated(Graphics g, Image image, double x, double y, double angle) {
        //BufferedImage newImage = (BufferedImage) image;
        BufferedImage newImage = ((ToolkitImage) image).getBufferedImage();
        AffineTransform tx;
        int change1 = image.getWidth(this) / 2;
        int change2 = image.getHeight(this) / 2;
        tx = AffineTransform.getRotateInstance(Math.toRadians(angle), change1, change2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        try {
            g.drawImage(op.filter(newImage, null), (int) (x), (int) (y), null);
        } catch (Exception e) {
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void dispMessage(TextChat m) {
        chats.add(m);
        m.display();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (chat != null) {
            //System.out.println(e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                //System.out.println("yo u pressed enter");
                client.chat(chat);
                chat = null;
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                chat.delete();
            } else {
                chat.append(e.getKeyChar());
            }
        } else {
            if (e.getKeyCode() == 69) {
                dispScores = true;
            }
            if (!youDead) {
                if (e.getKeyCode() == 32) {
                    makeBullet = true;
                }
                if (e.getKeyCode() == 37 || e.getKeyCode() == 65) {
                    left = true;
                }
                if (e.getKeyCode() == 38 || e.getKeyCode() == 87) {
                    if (players.get(client.getPlayerNum()).jumpHeight() != -1) {
                        if (!players.get(client.getPlayerNum()).isJumping() && !isFalling) {
                            players.get(client.getPlayerNum()).finnaJump(true);
                            jumpCounter = players.get(client.getPlayerNum()).jumpHeight();
                        }
                    } else {
                        players.get(client.getPlayerNum()).finnaJump(true);
                    }
                }
                if (e.getKeyCode() == 39 || e.getKeyCode() == 68) {
                    right = true;
                }
                if (e.getKeyCode() == 40 || e.getKeyCode() == 83) {
                    if (players.get(client.getPlayerNum()).canFall() && !players.get(client.getPlayerNum()).isCarryingFlag()) {
                        down = true;
                    }
                }
            }
            if (e.getKeyCode() == 84) {
                chat = new TextChat();
                chat.append(players.get(client.getPlayerNum()).getName() + ": ");
            }
            if (e.getKeyCode() == 89) {
                chat = new TextChat();
                chat.teamChat(players.get(client.getPlayerNum()).getTeamNum());
                chat.append(players.get(client.getPlayerNum()).getName() + ": ");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 69) {
            //System.out.println("Released "+e.getKeyCode());
            dispScores = false;
        }
        if (!youDead) {
            if (e.getKeyCode() == 32) {
                makeBullet = false;
            }
            if (e.getKeyCode() == 37 || e.getKeyCode() == 65) {
                left = false;
            }
            if (e.getKeyCode() == 39 || e.getKeyCode() == 68) {
                right = false;
            }
            if (e.getKeyCode() == 40 || e.getKeyCode() == 83) {
                down = false;
            }
            if (e.getKeyCode() == 38 || e.getKeyCode() == 87) {
                players.get(client.getPlayerNum()).finnaJump(false);
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}