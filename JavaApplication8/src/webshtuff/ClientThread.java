package webshtuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientThread extends Thread {

    private Client buddy;
    private BufferedReader in;
    private boolean imAlive = true;
    private int lastHitBy = 0;

    public ClientThread(Client clurnt) {
        buddy = clurnt;
        in = buddy.getIn();
    }

    public void goDie() {
        imAlive = false;
    }

    public int flipTeam(int x) {
        if (x == 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public void run() {
        buddy.getMasterUnit().printProcess("CLIENT THREAD IS A THING");
        buddy.checkVersion();
        //buddy.disperseName(buddy.getPlayerNum(), buddy.getMasterUnit().getPlayerName());
        while (imAlive) {
            try {
                receive();
            } catch (Exception ex) {
                buddy.getMasterUnit().printProcess(ex.getMessage());
                System.err.println(ex.getMessage());
                imAlive = false;
                buddy.getMasterUnit().conLost();
            }
        }
        buddy.getMasterUnit().printProcess("ClientThread " + buddy.getPlayerNum() + " has been discontinued");
        int derp = buddy.getPlayerNum();
        try {
            buddy.dispose();
        } catch (Exception e) {
            buddy.getMasterUnit().printProcess("Error disposing client in ClientThread " + derp);
            buddy.getMasterUnit().printProcess(e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    public String receive() throws IOException {
        String data = in.readLine();
        //buddy.getMasterUnit().printProcess("I AM THE CLIENT AND I HAVE DATA: " + data);
        StringTokenizer st = new StringTokenizer(data);
        String firstToken = st.nextToken();
        if (firstToken.equals("GO")) {
            //buddy.getMasterUnit().printProcess("I AM A CLIENT AND WE ARE STARTING GAME");
            buddy.getMasterUnit().setMap(Integer.parseInt(st.nextToken()));
            buddy.getMasterUnit().setHardcore(Boolean.parseBoolean(st.nextToken()));
            buddy.start();
        } else if (firstToken.equals("TEAMS")) {
            //System.out.println("doin the teams");
            buddy.getMasterUnit().setGameMode(Integer.parseInt(st.nextToken()));
            if (buddy.getMasterUnit().gameMode > 0) {
                for (int i = 0; i < buddy.getNumPlayersConnected(); i++) {
                    buddy.getMasterUnit().teams.set(i, Integer.parseInt(st.nextToken()));
                }
                //System.out.println("These are the normal teams:" + buddy.getMasterUnit().teams);
            } else {
                for (int i = 0; i < buddy.getNumPlayersConnected(); i++) {
                    buddy.getMasterUnit().teams.set(i, 1);
                }
                buddy.getMasterUnit().teams.set(buddy.getPlayerNum(), 0);
                //System.out.println("These are the FFA teams:" + buddy.getMasterUnit().teams);
            }
            //System.out.println("I AM A CLIENTThread AND MY TEAM NUMBER IS " + buddy.getMasterUnit().teams.get(buddy.getPlayerNum()));
        } else if (firstToken.equals("HIT")) {
            boolean uHit = false;
            int aNumber = Integer.parseInt(st.nextToken());
            double aNoThereNumber = Double.parseDouble(st.nextToken());
            if (aNumber == buddy.getPlayerNum()) {
                //buddy.getMasterUnit().printProcess("I AM PLAYER " + buddy.getPlayerNum() + " AND I GOT HIT");
                buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).loseHealth(aNoThereNumber);
                uHit = true;
            }
            ArrayList<EnemyBullet> temp = buddy.getMasterUnit().getHandles().bullets2;
            int playerNum = Integer.parseInt(st.nextToken());
            int index = Integer.parseInt(st.nextToken());
            if (uHit) {
                lastHitBy = playerNum;
            }
            for (int i = 0; i < temp.size(); i++) {
                //buddy.getMasterUnit().printProcess("Are these equal? " + (temp.get(i).getIndex() == index) + " && " + (temp.get(i).getPlayerNum() == playerNum));
                if (temp.get(i).getIndex() == index && temp.get(i).getPlayerNum() == playerNum) {
                    //buddy.getMasterUnit().printProcess("REMOVING BULLET @ " + i + " SHOT BY PLAYER " + playerNum);
                    EnemyBullet derp = temp.remove(i);
                    if (derp.getAirBurst() == 1) {
                        buddy.getMasterUnit().getHandles().airBurstLocation.setLocation(derp.getX(), derp.getY());
                    } else if (derp.getAirBurst() == 2) {
                        buddy.getMasterUnit().getHandles().airBurst2Location.setLocation(derp.getX(), derp.getY());
                    }
                    for (int j = 0; j < temp.size(); j++) {
                        if (temp.get(j).getPlayerNum() == playerNum && temp.get(j).getIndex() > index) {
                            temp.get(j).decIndex();
                        }
                    }
                    i = temp.size();
                }
            }
        } else if (firstToken.equals("DIED")) {
            //System.out.println("Someone died, hardcore = " + buddy.getMasterUnit().hardcoreOn);
            boolean sendKilled = false;
            int pDiddy = Integer.parseInt(st.nextToken());
            if (buddy.getPlayerNum() == pDiddy) {
                //System.out.println("Client " + buddy.getPlayerNum() + " died by player " + lastHitBy + "\'s hand");
                sendKilled = true;
                if (buddy.getMasterUnit().hardcoreOn) {
                    buddy.getMasterUnit().getHandles().youDead = true;
                    int carryOverDeaths = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getDeaths();
                    int carryOverKills = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getKills();
                    int carryOverTeamNum = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getTeamNum();
                    int carryOverFlagCaps = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getFlagCaps();
                    buddy.getMasterUnit().getHandles().getPlayers().set(pDiddy, new Player(buddy.getMasterUnit().getHandles(), -200, -200, 0, false, -5, 0, 0, 0, 0, 0, 0, 0, "")); //-5 health
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setDeaths(carryOverDeaths);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setKills(carryOverKills);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setTeamNum(carryOverTeamNum);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setFlagCaps(carryOverFlagCaps);
                } else {
                    //System.out.println("Respawning player " + buddy.getPlayerNum());
                    Point spawn = buddy.getMasterUnit().getHandles().spawnPlayer(buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getTeamNum());
                    //System.out.println("got the spawn for " + buddy.getPlayerNum());
                    //System.out.println("lets compare:");
                    //System.out.println("Old position: " + buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).toString());
                    //System.out.println("New Position: " + spawn);
                    int health, damage, airBurst, jump, drop, speed, cooldown, shield;
                    health = buddy.getMasterUnit().healthUpgrade;
                    damage = buddy.getMasterUnit().damageUpgrade;
                    airBurst = buddy.getMasterUnit().airBurstUpgrade;
                    jump = buddy.getMasterUnit().jumpUpgrade;
                    drop = buddy.getMasterUnit().dropUpgrade;
                    speed = buddy.getMasterUnit().speedUpgrade;
                    cooldown = buddy.getMasterUnit().cooldownUpgrade;
                    shield = buddy.getMasterUnit().shieldUpgrade;
                    String name = buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getName();
                    //System.out.println("new player " + buddy.getPlayerNum() + " upgrades reset");
                    int carryOverDeaths = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getDeaths();
                    int carryOverKills = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getKills();
                    int carryOverTeamNum = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getTeamNum();
                    int carryOverFlagCaps = buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).getFlagCaps();
                    buddy.getMasterUnit().getHandles().getPlayers().set(pDiddy, new Player(buddy.getMasterUnit().getHandles(), spawn.getX(), spawn.getY(), 0, false, health, damage, airBurst, jump, drop, speed, cooldown, shield, name));
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setDeaths(carryOverDeaths);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setKills(carryOverKills);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setTeamNum(carryOverTeamNum);
                    buddy.getMasterUnit().getHandles().getPlayers().get(pDiddy).setFlagCaps(carryOverFlagCaps);
                    buddy.getMasterUnit().getHandles().coolDown = 0;
                    buddy.getMasterUnit().getHandles().overHeat = false;
                    //System.out.println("new Player " + buddy.getPlayerNum() + " created");
                    if (pDiddy == buddy.getPlayerNum()) {
                        //System.out.println("dispersing name to clients");
                        buddy.disperseName(buddy.getPlayerNum(), name);
                    }
                }
            }
            if (sendKilled) {
                if (buddy.getMasterUnit().gameMode == 2) {
                    buddy.droppedThatFlag(buddy.getPlayerNum(), buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getTeamNum());
                }
                buddy.uKilledMe(lastHitBy);
                buddy.someoneMoved(pDiddy, buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getX(), buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getY(), buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getAngle(), buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).xNeg(), false);
            }
            //buddy.getMasterUnit().printProcess("PLAYER " + pDiddy + " DIED");
        } else if (firstToken.equals("BULLET")) {
            //if (pDiddy != buddy.getPlayerNum()) {
            buddy.getMasterUnit().getHandles().bullets2.add(new EnemyBullet(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Boolean.parseBoolean(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
            //buddy.getMasterUnit().printProcess(buddy.getMasterUnit().getHandles().bullets2);
            //}
        } else if (firstToken.equals("KILLED")) {
            int killNum = Integer.parseInt(st.nextToken());
            int deadNum = Integer.parseInt(st.nextToken());
            //System.out.println("I AM CLIENTTHREAD " + buddy.getPlayerNum() + " AND Player " + killNum + " killed player " + deadNum);
            buddy.getMasterUnit().getHandles().getPlayers().get(killNum).killedAGuy();
            buddy.getMasterUnit().getHandles().getPlayers().get(deadNum).died();
        } else if (firstToken.equals("MOVE")) {
            //buddy.getMasterUnit().printProcess("SOMEONE MOVED");
            try {
                Player p = buddy.getMasterUnit().getHandles().getPlayers().get(Integer.parseInt(st.nextToken()));
                p.setX(Double.parseDouble(st.nextToken()));
                p.setY(Double.parseDouble(st.nextToken()));
                p.setAngle(Double.parseDouble(st.nextToken()));
                p.setXNeg(Boolean.parseBoolean(st.nextToken()));
                p.finnaJump(Boolean.parseBoolean(st.nextToken()));
                //p.addTrail(p.getX(), p.getY());
                if (p.isCarryingFlag()) {
                    buddy.getMasterUnit().getHandles().flags[flipTeam(p.getTeamNum())].setX(p.getX());
                    buddy.getMasterUnit().getHandles().flags[flipTeam(p.getTeamNum())].setY(p.getY());
                }
            } catch (Exception e) {
                //buddy.getMasterUnit().printProcess("CAN'T DO THAT YET");
            }
        } else if (firstToken.equals("FLAGUP")) {
            int playerNum = Integer.parseInt(st.nextToken());
            int teamNum = Integer.parseInt(st.nextToken());
            buddy.getMasterUnit().getHandles().getPlayers().get(playerNum).pickedUpFlag();
            buddy.getMasterUnit().getHandles().flags[flipTeam(teamNum)].setCapturable(false);
        } else if (firstToken.equals("FLAGDOWN")) {
            int playerNum = Integer.parseInt(st.nextToken());
            int teamNum = Integer.parseInt(st.nextToken());
            buddy.getMasterUnit().getHandles().getPlayers().get(playerNum).droppedFlag();
            buddy.getMasterUnit().getHandles().flags[flipTeam(teamNum)].setCapturable(true);
        } else if (firstToken.equals("FLAGBACK")) {
            int teamNum = Integer.parseInt(st.nextToken());
            buddy.getMasterUnit().getHandles().flags[teamNum].returnToBase();
        } else if (firstToken.equals("FLAGCAP")) {
            int playerNum = Integer.parseInt(st.nextToken());
            int teamNum = Integer.parseInt(st.nextToken());
            buddy.getMasterUnit().getHandles().getPlayers().get(playerNum).capFlag();
            buddy.getMasterUnit().getHandles().flags[flipTeam(teamNum)].returnToBase();
            buddy.getMasterUnit().getHandles().flags[flipTeam(teamNum)].setCapturable(true);
            buddy.getMasterUnit().getHandles().getPlayers().get(playerNum).justCapped = false;
        } else if (firstToken.equals("CHAT")) {
            TextChat t = new TextChat();
            String message = "";
            int teamChat = Integer.parseInt(st.nextToken());
            while (st.hasMoreTokens()) {
                message += st.nextToken() + " ";
            }
            message = message.substring(0, message.length() - 1);
            t.append(message);
            if (buddy.isRunning()) {
                if (teamChat == -1) {
                    buddy.getMasterUnit().getHandles().dispMessage(t);
                } else {
                    if (buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getPlayerNum()).getTeamNum() == teamChat) {
                        t.teamChat(teamChat);
                        buddy.getMasterUnit().getHandles().dispMessage(t);
                    }
                }
            } else {
                buddy.getMasterUnit().dispMessage(t);
            }
        } else if (firstToken.equals("PLAYER")) {
            buddy.setPlayerNum(new Integer(st.nextToken()));
        } else if (firstToken.equals("JOIN")) {
            buddy.setNumPlayersConnected(new Integer(st.nextToken()));
        } else if (firstToken.equals("READY")) {
            buddy.setReady(Integer.parseInt(st.nextToken()));
        } else if (firstToken.equals("RESTART")) {
            //buddy.getMasterUnit().printProcess("LOL STILL A STRETCH GOAL or is ist");
            buddy.getMasterUnit().getHandles().counter.reset();
            //buddy.resetData();
            buddy.resetReady();
            buddy.terminate();
        } else if (firstToken.equals("WINNER")) {
            buddy.getMasterUnit().getHandles().counter.reset();
            buddy.getMasterUnit().getHandles().displayWinner = true;
            String name = "";
            while (st.hasMoreTokens()) {
                name += st.nextToken() + " ";
            }
            name = name.substring(0, name.length() - 1);
            buddy.getMasterUnit().getHandles().winnerName = name;
            buddy.getMasterUnit().getHandles().counter.start();
            ArrayList<Player> temp = buddy.getMasterUnit().getHandles().getPlayers();
            for (int i = 0; i < temp.size(); i++) {
                String asdf = "";
                asdf += temp.get(i).getName() + " got " + temp.get(i).getKills() + " kills and " + temp.get(i).getDeaths() + " deaths";
                if (buddy.getMasterUnit().gameMode == 2) {
                    asdf += " and " + temp.get(i).getFlagCaps() + " flag caps";
                }
                DataBox d = new DataBox(asdf);
                d.setKills(temp.get(i).getKills());
                d.setDeaths(temp.get(i).getDeaths());
                if (buddy.getMasterUnit().gameMode == 2) {
                    d.setFlagCaps(temp.get(i).getFlagCaps());
                }
                buddy.setGameData(i, d);
            }
        } else if (firstToken.equals("WINNERSC")) {
            buddy.getMasterUnit().getHandles().counter.reset();
            buddy.getMasterUnit().getHandles().displayWinner = true;
            String name;
            if (buddy.getMasterUnit().gameMode == 0) {
                name = buddy.getMasterUnit().getHandles().getPlayers().get(buddy.getMasterUnit().getHandles().getBestPlayer()).getName();
            } else if (buddy.getMasterUnit().gameMode == 1) {
                if (buddy.getMasterUnit().getHandles().getBestTeam() == 0) {
                    name = buddy.getMasterUnit().team0Name;
                } else {
                    name = buddy.getMasterUnit().team1Name;
                }
            } else {
                if (buddy.getMasterUnit().getHandles().getBestTeamCTF() == 0) {
                    name = buddy.getMasterUnit().team0Name;
                } else {
                    name = buddy.getMasterUnit().team1Name;
                }
            }
            buddy.getMasterUnit().getHandles().winnerName = name;
            buddy.getMasterUnit().getHandles().counter.start();
            ArrayList<Player> temp = buddy.getMasterUnit().getHandles().getPlayers();
            for (int i = 0; i < temp.size(); i++) {
                String asdf = "";
                asdf += temp.get(i).getName() + " got " + temp.get(i).getKills() + " kills and " + temp.get(i).getDeaths() + " deaths";
                if (buddy.getMasterUnit().gameMode == 2) {
                    asdf += " and " + temp.get(i).getFlagCaps() + " flag caps";
                }
                DataBox d = new DataBox(asdf);
                d.setKills(temp.get(i).getKills());
                d.setDeaths(temp.get(i).getDeaths());
                if (buddy.getMasterUnit().gameMode == 2) {
                    d.setFlagCaps(temp.get(i).getFlagCaps());
                }
                buddy.setGameData(i, d);
            }
        } else if (firstToken.equals("VERSION")) {
            if (buddy.getMasterUnit().getVersion() < Double.parseDouble(st.nextToken())) {
                buddy.getMasterUnit().urVersionIsTooOld();
                buddy.getMasterUnit().dispose();
            }
            buddy.getMasterUnit().printProcess("--SERVER CONNECTION ESTABLISHED--");
        } else if (firstToken.equals("NAME")) {
            int pNum = Integer.parseInt(st.nextToken());
            String name = "";
            while (st.hasMoreTokens()) {
                name += st.nextToken() + " ";
            }
            name = name.substring(0, name.length() - 1);
            buddy.getMasterUnit().setTheName(pNum, name);
            if (buddy.isRunning()) {
                try {
                    buddy.getMasterUnit().getHandles().getPlayers().get(pNum).setName(name);
                } catch (Exception e) {
                    System.out.println("CANT DO THAT NAME YET");
                }
            }
        } else if (firstToken.equals("IMLEAVING")) {
            int pDiddy = Integer.parseInt(st.nextToken());
            if (pDiddy == buddy.getPlayerNum()) {
                buddy.getMasterUnit().printProcess("ClientThread about to quit");
                if (!buddy.iSendGone) {
                    buddy.disconnecting(pDiddy);
                } else {
                    goDie();
                }
            } else {
                buddy.getMasterUnit().printProcess("ClientThread " + buddy.getPlayerNum() + " just learned that ClientThread " + pDiddy + " is about to quit");
                if (buddy.getMasterUnit().getHandles() != null) {
                    //buddy.getMasterUnit().printProcess("player " + pDiddy + " left in game");
                    buddy.getMasterUnit().getHandles().getPlayers().set(pDiddy, new Player(buddy.getMasterUnit().getHandles(), -200, -200, 0, false, -5, 0, 0, 0, 0, 0, 0, 0, "")); //-5 health
                } else {
                    //buddy.getMasterUnit().printProcess("Player " + pDiddy + " left.");
                }
            }
            buddy.getMasterUnit().printProcess("PLAYER " + pDiddy + " LEFT THE GAME");
        }
        return data;

    }
}