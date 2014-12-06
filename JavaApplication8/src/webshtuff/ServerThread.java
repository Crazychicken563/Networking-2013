package webshtuff;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    private BufferedReader in;
    private PrintWriter out;
    public ServerGUI server;
    public int playerNum;
    private boolean imAlive = true;
    public boolean dieLater = false;

    public ServerThread(Socket socket, ServerGUI s, int pNum) throws IOException {
        //socket.setSoTimeout(100);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        server = s;
        playerNum = pNum;
    }

    public void goDie() {
        //server.print("lets die");
        imAlive = false;
        dieLater = false;
    }

    public void goDieLater() {
        imAlive = false;
        dieLater = true;
    }

    public boolean isLiving() {
        return imAlive;
    }

    public boolean isSuspended() {
        return dieLater && !imAlive;
    }

    @Override
    public void run() {
        try {
            while (imAlive) {
                if (dieLater) {
                    imAlive = false;
                }
                receive();
            }
        } catch (IOException e) {
            server.print("SERVERTHREAD " + playerNum + " was hit by a gamma ray");
            if (!server.isStarting()) {
                goDie();
            } else {
                goDieLater();
            }
            server.disseminate(playerNum, "IMLEAVING " + playerNum);
        }
        if (dieLater) {
            server.print("ServerThread " + playerNum + " suspended");
        }
        while (dieLater) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        server.print("ServerThread " + playerNum + " terminated");
        server.playerLeft(playerNum);
        try {
            in.close();
            out.close();
        } catch (Exception e) {
            System.err.println("Error closing socket in ServerThread " + playerNum);
            System.err.println(e.getMessage());
        }
    }

    public String receive() throws IOException {
        String data = in.readLine();
        if (data != null) {
            //server.print("WE GOT SOME DATA: " + data);
            StringTokenizer st = new StringTokenizer(data);
            String firstToken = st.nextToken();
            if (firstToken.equals("BULLET")) {
                disseminate(data);
            } else if (firstToken.equals("MOVE")) {
                //server.print(data);
                disseminate(data);
            } else if (firstToken.equals("HIT")) {
                //server.print(numPlayersAlive);
                disseminate(data);
                //use game class to update errythang
            } else if (firstToken.equals("KILLED")) {
                //int killNum = Integer.parseInt(st.nextToken());
                //int deadNum = Integer.parseInt(st.nextToken());
                //server.print("I AM SERVERTHREAD " + playerNum + " AND Player " + killNum + " killed player " + deadNum);
                server.send(data);
            } else if (firstToken.equals("DIED")) {
                int pNum = Integer.parseInt(st.nextToken());
                server.send(data);
                //server.print("Death points goin up for player " + pNum);
                if (server.hardcore) {
                    server.playersAlive.set(pNum, Boolean.FALSE);
                    int numAlive = 6;
                    int livingPlayer = 0;
                    ArrayList<Boolean> temp = server.playersAlive;
                    for (int i = 0; i < 6; i++) {
                        if (!temp.get(i).booleanValue()) {
                            numAlive--;
                        } else {
                            livingPlayer = i;
                        }
                    }
                    //server.print("NUM ALIVE " + numAlive + "");
                    if (numAlive == 1) {
                        server.send("WINNER " + server.names.get(livingPlayer));
                        server.print("Hardcore game ended...");
                        //server.print("Winner was " + server.names.get(livingPlayer) + ". Restarting...");
                        server.restart();
                    }
                }
            } else if (firstToken.equals("FLAGUP")) {
                //server.print(data);
                server.send(data);
            } else if (firstToken.equals("FLAGDOWN")) {
                //server.print(data);
                server.send(data);
            } else if (firstToken.equals("FLAGBACK")) {
                //server.print(data);
                server.send(data);
            } else if (firstToken.equals("FLAGCAP")) {
                //server.print(data);
                server.send(data);
            } else if (firstToken.equals("READY")) {
                //server.print("SOMEONE EES READY");
                if (st.hasMoreTokens() && st.nextToken().equals("true")) {
                    //server.print("ACTIVATING " + playerNum);
                    server.activate(playerNum);
                } else {
                    //server.print("UNACTIVATING " + playerNum);
                    server.unactivate(playerNum);
                }
            } else if (firstToken.equals("CHAT")) {
                server.chat(data);
                server.send(data);
            } else if (firstToken.equals("VERSION")) {
                server.send(playerNum, data + " " + server.getVersion());
            } else if (firstToken.equals("NAME")) {
                int pNum = Integer.parseInt(st.nextToken());
                String name = "";
                while (st.hasMoreTokens()) {
                    name += st.nextToken() + " ";
                }
                name = name.substring(0, name.length() - 1);
                String sendName = server.setName(pNum, name);
                server.send("NAME " + pNum + " " + sendName);
            } else if (firstToken.equals("IMLEAVING")) {
                //System.out.println("ServerThread " + playerNum + " dispersing quit info");
                server.send(data);
                if (Integer.parseInt(st.nextToken()) == playerNum) {
                    //System.out.println("ServerThread " + playerNum + " about to quit");
                    if (!server.isStarting()) {
                        goDie();
                    } else {
                        goDieLater();
                        //server.print("ServerThread " + playerNum + " is not gowing away for the sake of the game");
                    }
                }
                server.playerListStuff();
            }
            return data;
        } else {
            server.print("Client " + playerNum + " timed out");
            if (!server.isStarting()) {
                goDie();
            } else {
                goDieLater();
            }
            server.disseminate(playerNum, "IMLEAVING " + playerNum);
            return null;
        }
    }

    public void send(String data) {
        try {
            out.println(data);
            out.flush();
        } catch (Exception e) {
            server.print("Send Failed, Error: " + e.getMessage());
        }
    }

    public void disseminate(String s) {
        server.disseminate(playerNum, s);
    }

    public int getPlayerNum() {
        return playerNum;
    }
}