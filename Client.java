package webshtuff;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class Client {

    private Socket skt = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Integer playerNum = 0;
    private Boolean isStarting = false;
    private ClientThread thread = null;
    private TanksApp game;
    private int numPlayersConnected = 1;
    private int numPlayersReady = 0;
    private ArrayList<DataBox> prevGameData;
    public boolean iSendGone = false;

    public Client(TanksApp g, String ip) throws IOException {
        game = g;
        //game.printProcess("I AM THE CLIENT HERE WE GO");
        prevGameData = new ArrayList<DataBox>();
        for (int i = 0; i < 6; i++) {
            prevGameData.add(new DataBox("Player " + i + " not connected"));
        }
        boolean isConnected = false;
        int fails = 0;
        while (!isConnected) {
            try {
                skt = new Socket(ip, 40000);
                out = new PrintWriter(skt.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                game.printProcess("CONNECTION IS A THING");
                isConnected = true;
                thread = new ClientThread(this);
                thread.start();
            } catch (Exception e) {
                game.printProcess("Error in client: " + e.getMessage());
                fails++;
                if (fails > 0) {
                    isConnected = true;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                game.printProcess("Error in client: " + ex.getMessage());
            }
        }
        if (skt != null && fails == 0) {
            game.printProcess("I, CLIENT, HAVE COME");
            game.confirmConnection();
        } else {
            game.printProcess("I, CLIENT, HAVE NOT COME");
            game.connectionFailed();
        }
    }

    public void dispose() throws IOException {
        game.printProcess("Client " + playerNum + " shutting down");
        skt.close();
        game.printProcess("skt in client " + playerNum + " has been closed");
        getMasterUnit().quit();
    }

    public void reset() {
        try {
            game.printProcess("Client " + playerNum + " shutting down");
            skt.close();
            game.printProcess("skt in client " + playerNum + " has been closed");
        } catch (Exception e) {
        }
    }

    public void send(String data) {
        try {
            out.println(data);
            out.flush();
        } catch (Exception e) {
            game.printProcess("Send Failed, Error: " + e.getMessage());
        }
    }

    public void ready(Boolean b) {
        send("READY " + b);
    }

    public void hit(int playerNumHit, double damage, int playerNum, int index) {
        send("HIT " + playerNumHit + " " + damage + " " + playerNum + " " + index);
    }

    public Boolean isRunning() {
        return isStarting;
    }

    public void setGameData(int pNum, DataBox data) {
        prevGameData.set(pNum, data);
    }

    public ArrayList<DataBox> getGameData() {
        ArrayList<DataBox> sortedData = new ArrayList<DataBox>();
        if (getMasterUnit().gameMode != 0) {
            ArrayList<DataBox> team0PlayerData = new ArrayList<DataBox>();
            ArrayList<DataBox> team1PlayerData = new ArrayList<DataBox>();
            for (int i = 0; i < numPlayersConnected; i++) {
                if (getMasterUnit().teams.get(i) == 0) {
                    team0PlayerData.add(prevGameData.get(i));
                } else {
                    team1PlayerData.add(prevGameData.get(i));
                }
            }
            Collections.sort(team0PlayerData);
            Collections.sort(team1PlayerData);
            if (getMasterUnit().gameMode == 2) {
                if (getMasterUnit().getHandles().getBestTeamCTF() == 0) {
                    sortedData.add(new DataBox("--" + getMasterUnit().team0Name + "--"));
                    sortedData.addAll(team0PlayerData);
                    sortedData.add(new DataBox("--" + getMasterUnit().team1Name + "--"));
                    sortedData.addAll(team1PlayerData);
                } else {
                    sortedData.add(new DataBox("--" + getMasterUnit().team1Name + "--"));
                    sortedData.addAll(team1PlayerData);
                    sortedData.add(new DataBox("--" + getMasterUnit().team0Name + "--"));
                    sortedData.addAll(team0PlayerData);
                }
            }
            if (getMasterUnit().gameMode == 1) {
                if (getMasterUnit().getHandles().getBestTeam() == 0) {
                    sortedData.add(new DataBox("--" + getMasterUnit().team0Name + "--"));
                    sortedData.addAll(team0PlayerData);
                    sortedData.add(new DataBox("--" + getMasterUnit().team1Name + "--"));
                    sortedData.addAll(team1PlayerData);
                } else {
                    sortedData.add(new DataBox("--" + getMasterUnit().team1Name + "--"));
                    sortedData.addAll(team1PlayerData);
                    sortedData.add(new DataBox("--" + getMasterUnit().team0Name + "--"));
                    sortedData.addAll(team0PlayerData);
                }
            }
        } else {
            Collections.sort(prevGameData);
            sortedData.add(new DataBox("--SCORES--"));
            sortedData.addAll(prevGameData);
        }
        //sortedData.addAll(prevGameData);
        return sortedData;
    }

    public void resetData() {
        prevGameData = new ArrayList<DataBox>();
        for (int i = 0; i < 6; i++) {
            prevGameData.add(new DataBox("Player " + i + " not connected"));
        }
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getNumPlayersReady() {
        return numPlayersReady;
    }

    public int getNumPlayersConnected() {
        return numPlayersConnected;
    }

    public void start() {
        isStarting = true;
        resetData();
    }

    public void terminate() {
        isStarting = false;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Socket getSocket() {
        return skt;
    }

    public void checkVersion() {
        send("VERSION");
    }

    public void someoneMoved(int pN, double x, double y, double angle, boolean xNeg, boolean jumping) {
        send("MOVE " + pN + " " + x + " " + y + " " + angle + " " + xNeg + " " + jumping);
    }

    public void someoneShot(double x, double y, double angle, boolean xNeg, int playerNum, int index, int airBurst) {
        //game.printProcess("Player "+playerNum+" shot a bullet");
        send("BULLET " + x + " " + y + " " + angle + " " + xNeg + " " + playerNum + " " + index + " " + airBurst);
    }

    public void setPlayerNum(int pN) {
        playerNum = pN;
        //game.printProcess("I AM A CLIENT AND MY NEW PLAYER NUMBER IS " + playerNum.toString());
    }

    public void setNumPlayersConnected(int nPC) {
        numPlayersConnected = nPC;
    }

    public void setReady(int r) {
        numPlayersReady = r;
    }

    public TanksApp getMasterUnit() {
        return game;
    }

    public void someoneDied(int pN) {
        send("DIED " + pN);
    }

    public void resetReady() {
        numPlayersReady = 0;
    }

    public void disperseName(int pNum, String n) {
        //game.printProcess("Sending name " + n + " from player " + pNum);
        send("NAME " + " " + pNum + " " + n);
    }

    public void disconnecting(int pNum) {
        game.printProcess("Client " + playerNum + " sending disconnect data to server");
        send("IMLEAVING " + pNum);
        iSendGone = true;
        game.printProcess("Client " + playerNum + " confirms data sent");
    }

    public void uKilledMe(int pNumOfKiller) {
        send("KILLED " + pNumOfKiller + " " + playerNum);
    }

    public void getThatFlag(int pNum, int teamNum) {
        send("FLAGUP " + pNum + " " + teamNum);
    }

    public void droppedThatFlag(int pNum, int teamNum) {
        send("FLAGDOWN " + pNum + " " + teamNum);
    }

    public void flagToBase(int teamNum) {
        send("FLAGBACK " + teamNum);
    }

    public void flagCapped(int pNum, int teamNum) {
        send("FLAGCAP " + pNum + " " + teamNum);
    }

    public void chat(TextChat chatBlob) {
        send("CHAT " + " " + chatBlob.isTeamChat() + " " + chatBlob.getMessage());
    }

    public void ping() {
        send("PING");
    }
}