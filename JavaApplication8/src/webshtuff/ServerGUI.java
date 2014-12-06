package webshtuff;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.ListModel;
import javax.swing.UIManager;

public class ServerGUI extends javax.swing.JFrame {

    private ServerSocket server;
    private Map<Integer, ServerThread> threads;
    private Map<Integer, Boolean> readiness;
    private Boolean isStarting = false;
    public ArrayList<Boolean> playersAlive;
    private double version = 2.3;
    private int numPlayersConnected = 0;
    public ArrayList<String> names;
    public ArrayList<Integer> teams;
    private boolean isAlive = true;
    private int maxPlayers = 6;
    public boolean hardcore = false;
    public StopWatch timer = new StopWatch();
    private Random r = new Random();
    private String teamNums;
    private boolean restart = false;

    public ServerGUI() throws IOException {
        initComponents();
        threads = new HashMap<Integer, ServerThread>();
        readiness = new HashMap<Integer, Boolean>();
        playersAlive = new ArrayList<Boolean>();
        names = new ArrayList<String>();
        teams = new ArrayList<Integer>();
        for (int i = 0; i < maxPlayers; i++) {
            playersAlive.add(false);
            names.add("Player " + i);
        }
        versionLabel.setText("Version " + version);
    }

    public void startThatGame() {
        isStarting = true;
    }

    public boolean isStarting() {
        return isStarting;
    }

    public int getNumPlayersConnected() {
        return numPlayersConnected;
    }

    public void disperseNames() {
        for (int i = 0; i < names.size(); i++) {
            send("NAME " + " " + i + " " + names.get(i));
        }
    }

    public void randomizeTeams() {
        teams = new ArrayList<Integer>();
        for (int i = 0; i < numPlayersConnected; i++) {
            teams.add(-1);
        }
        int num0 = 0;
        int num1 = 0;
        boolean aNum;
        for (int i = 0; i < numPlayersConnected; i++) {
            if (teamsRiggedCheckBox.isSelected()) {
                if (modelIndexOf(team0List.getModel(), names.get(i)) != -1) {
                    teams.set(i, 0);
                    num0++;
                } else if (modelIndexOf(team1List.getModel(), names.get(i)) != -1) {
                    teams.set(i, 1);
                    num1++;
                }
            } else {
                aNum = r.nextBoolean();
                if (i >= numPlayersConnected / 2) {
                    if (num0 > num1) {
                        teams.set(i, 1);
                        num1++;
                    } else {
                        teams.set(i, 0);
                        num0++;
                    }
                } else {
                    if (aNum) {
                        teams.set(i, 0);
                        num0++;
                    } else {
                        teams.set(i, 1);
                        num1++;
                    }
                }
            }
        }
        //print("Curr Teams: " + teams.toString());
    }

    public ArrayList<Integer> getTeams() {
        return teams;
    }

    public int modelIndexOf(ListModel m, String s) {
        for (int i = 0; i < m.getSize(); i++) {
            if (m.getElementAt(i).equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public void start() {
        playerListStuff();
        while (isAlive) {
            try {
                server = new ServerSocket(40000);
                server.setSoTimeout(500);
                for (int i = 0; i < maxPlayers; i++) {
                    readiness.put(i, true);
                }
                print("Waiting for players to connect...");
                while (!isStarting && isAlive) {
                    boolean socketConnected = false;
                    Socket socket = null;
                    //print("waiting for a connection");
                    while (!socketConnected) {
                        try {
                            //print("trying connection");
                            socket = server.accept();
                            //print("socket accepted connection");
                            socketConnected = true;
                        } catch (Exception e) {
                            if (isStarting) {
                                socketConnected = true;
                                //not really connected
                                //print("The game has started");
                            }
                        }
                    }
                    if (!isStarting) {
                        print("Someone wants to join...");
                        boolean foundANum = false;
                        int pNum = 0;
                        while (!foundANum) {
                            if (!threads.containsKey(pNum)) {
                                foundANum = true;
                            } else {
                                pNum++;
                            }
                            if (pNum > maxPlayers) {
                                print("Max players conncected, unable to join.");
                                foundANum = true;
                            }
                        }
                        if (foundANum) {
                            numPlayersConnected++;
                            numPlayersConnectedLabel.setText("Num Players Connected: " + numPlayersConnected);
                            //print("Loop begins");
                            //print("accepted someone");
                            //print("Connection from " + socket.getInetAddress() + " is fully armed and operational.");
                            ServerThread thread = new ServerThread(socket, this, pNum);
                            threads.put(pNum, thread);
                            thread.start();
                            thread.send("PLAYER " + pNum);
                            send("JOIN " + numPlayersConnected);
                            readiness.put(pNum, false);
                            playerListStuff();
                            print("player " + pNum + " is connected");
                            send("READY " + getNumReady());
                        } else {
                            print("Rejecting request from socket " + socket.getInetAddress() + ", too many people connected.");
                            socket.close();
                        }
                    }
                }
                print("Connection finished, game has begun");
                server.close();
                while (isStarting && isAlive) {
                    if (!hardcore && getTime() > 270) {
                        resetTimer();
                        send("WINNERSC");
                        print("Game ended, restarting...");
                        restart();
                    }
                    if (restart) {
                        if (getTime() >= 10) {
                            resetTimer();
                            restart = false;
                            send("RESTART");
                            unactivateAll();
                            //startThatGame();
                            isStarting = false;
                        }
                    }
                    boolean someoneAlive = false;
                    for (ServerThread t : threads.values()) {
                        if (t.isLiving()) {
                            someoneAlive = true;
                        }
                    }
                    if (!someoneAlive) {
                        print("Everyone left in game, resetting");
                        isStarting = false;
                    }
                }
                cleanConnections();
                print("Connecetion is reset");
            } catch (Exception e) {
                print("ERROR: " + e.getMessage());
                e.printStackTrace(System.out);
                if (e.getMessage().contains("JVM_Bind")) {
                    print("Conflicting Port Error");
                    System.exit(1);
                } else {
                    print("Attempting to reset server...");
                    resetButton.doClick();
                }
            }
        }
        isAlive = true;
        isStarting = true;
        numPlayersConnected = 0;
        print("Full reset complete");
        threads.clear();
        readiness.clear();
        playersAlive = new ArrayList<Boolean>();
        names = new ArrayList<String>();
        teams = new ArrayList<Integer>();
        for (int i = 0; i < maxPlayers; i++) {
            playersAlive.add(false);
            names.add("Player " + i);
        }
        start();
    }

    public void restart() {
        restart = true;
        startTimer();
    }

    public void cleanConnections() {
        print("Cleaning leftover connections...");
        for (ServerThread t : threads.values()) {
            if (t.dieLater) {
                print("ServerThread " + t.getPlayerNum() + " is being deactivated");
                //send("IMLEAVING " + t.getPlayerNum());
                t.goDie();
                //threads.remove(i);
            }
        }
        playerListStuff();
    }

    public void playerListStuff() {
        //print("Player List Stuff is happening");
        ArrayList<String> stuff = new ArrayList<String>();
        for (int i = 0; i < maxPlayers; i++) {
            if (threads.containsKey(i)) {
                if (threads.get(i).isSuspended()) {
                    stuff.add("Player " + i + " is suspended");
                } else {
                    if (readiness.get(i) == true) {
                        stuff.add(names.get(i) + " is ready");
                    } else {
                        stuff.add(names.get(i) + " is not ready");
                    }
                }
            } else {
                stuff.add("Player " + i + " not connected");
            }
        }
        String[] moreStuff = new String[stuff.size()];
        for (int i = 0; i < stuff.size(); i++) {
            moreStuff[i] = stuff.get(i);
        }
        playersConnectedList.setListData(moreStuff);
        teamPoolList.setListData(moreStuff);
    }

    public String setName(int pNum, String name) {
        String newName = name;
        String currName = names.get(pNum);
        if (names.indexOf(name) != -1 && names.indexOf(name) != pNum) {
            newName = "Player " + pNum;
        }
        if (modelIndexOf(team0List.getModel(), currName) != -1) {
            ArrayList<String> stuff = new ArrayList<String>();
            for (int i = 0; i < team0List.getModel().getSize(); i++) {
                stuff.add("" + team0List.getModel().getElementAt(i));
            }
            stuff.set(stuff.indexOf(currName), newName);
            String[] moreStuff = new String[stuff.size()];
            for (int i = 0; i < stuff.size(); i++) {
                moreStuff[i] = stuff.get(i);
            }
            team0List.setListData(moreStuff);
        }
        if (modelIndexOf(team1List.getModel(), currName) != -1) {
            ArrayList<String> stuff = new ArrayList<String>();
            for (int i = 0; i < team1List.getModel().getSize(); i++) {
                stuff.add("" + team1List.getModel().getElementAt(i));
            }
            stuff.set(stuff.indexOf(currName), newName);
            String[] moreStuff = new String[stuff.size()];
            for (int i = 0; i < stuff.size(); i++) {
                moreStuff[i] = stuff.get(i);
            }
            team1List.setListData(moreStuff);
        }
        names.set(pNum, newName);
        playerListStuff();
        return newName;
    }

    public void playerLeft(int pNum) {
        threads.remove(pNum);
        numPlayersConnected--;
        numPlayersConnectedLabel.setText("Num Players Connected: " + numPlayersConnected);
        //System.out.println("Sending to living clients");
        send("JOIN " + numPlayersConnected);
        readiness.put(pNum, true);
        playersAlive.set(pNum, false);
        print("player " + pNum + " is disconnected");
        int numReady = 0;
        for (Boolean b : readiness.values()) {
            if (b) {
                numReady++;
            }
        }
        numReady -= (maxPlayers - threads.size());
        numPlayersReadyLabel.setText("Num Players Ready: " + numReady);
        send("READY " + numReady);
        playerListStuff();
    }

    public int getNumReady() {
        int numReady = 0;
        for (Boolean b : readiness.values()) {
            if (b) {
                numReady++;
            }
        }
        numReady -= (maxPlayers - threads.size());
        return numReady;
    }

    @Override
    public void dispose() {
        for (ServerThread t : threads.values()) {
            t.goDie();
        }
        try {
            server.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        System.out.println("Server Socket Closed");
        System.exit(0);
    }

    public void send(String data) {
        for (ServerThread t : threads.values()) {
            t.send(data);
        }
    }

    public void send(int i, String data) {
        threads.get(i).send(data);
    }

    public void activate(int playerNum) {
        playersAlive.set(playerNum, true);
        readiness.put(playerNum, true);
        print("Player " + playerNum + " is ready");
        disperseNames();
        Boolean gonnaStart = true;
        int numReady = 0;
        for (Boolean b : readiness.values()) {
            if (!b) {
                gonnaStart = false;
            } else {
                numReady++;
            }
        }
        numReady -= (maxPlayers - threads.size());
        numPlayersReadyLabel.setText("Num Players Ready: " + numReady);
        send("READY " + numReady);
        if (gonnaStart) {
            print("Ready to start game...");
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }
        playerListStuff();
    }

    public void disseminate(int playerNum, String data) {
        for (ServerThread t : threads.values()) {
            if (t.getPlayerNum() != playerNum) {
                t.send(data);
            }
        }
    }

    public void unactivate(int playerNum) {
        print("Player " + playerNum + " is not ready");
        readiness.put(playerNum, false);
        playersAlive.set(playerNum, false);
        startButton.setEnabled(false);
        int numReady = 0;
        for (Boolean b : readiness.values()) {
            if (b) {
                numReady++;
            }
        }
        numReady -= (maxPlayers - threads.size());
        numPlayersReadyLabel.setText("Num Players Ready: " + numReady);
        send("READY " + numReady);
        playerListStuff();
    }

    public double getVersion() {
        return version;
    }

    public void unactivateAll() {
        for (ServerThread t : threads.values()) {
            unactivate(t.getPlayerNum());
        }
    }

    public void print(String s) {
        System.out.println(s);
        output.insert(s + "\n", 0);
    }

    public double getTime() {
        return timer.getTime();
    }

    public void resetTimer() {
        timer.reset();
    }

    public void startTimer() {
        timer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        serverTabsPane = new javax.swing.JTabbedPane();
        mainMenuPanel = new javax.swing.JPanel();
        outputScrollPane = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();
        startButton = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();
        gameModeScrollPane = new javax.swing.JScrollPane();
        gameModeList = new javax.swing.JList();
        gameModeLabel = new javax.swing.JLabel();
        nextMapLabel = new javax.swing.JLabel();
        nextMapScrollPane = new javax.swing.JScrollPane();
        nextMapList = new javax.swing.JList();
        numPlayersConnectedLabel = new javax.swing.JLabel();
        numPlayersReadyLabel = new javax.swing.JLabel();
        hardcoreCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        playersConnectedList = new javax.swing.JList();
        playersConnectedLabel = new javax.swing.JLabel();
        kickButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        teamRiggerPanel = new javax.swing.JPanel();
        playersConnectedListScrollPanel = new javax.swing.JScrollPane();
        teamPoolList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        setTeam0Button = new javax.swing.JButton();
        setTeam1Button = new javax.swing.JButton();
        team0Label = new javax.swing.JLabel();
        team0ListScrollPane = new javax.swing.JScrollPane();
        team0List = new javax.swing.JList();
        team1Label = new javax.swing.JLabel();
        team1ListScrollPane = new javax.swing.JScrollPane();
        team1List = new javax.swing.JList();
        teamsRiggedCheckBox = new javax.swing.JCheckBox();
        clearTeamButton = new javax.swing.JButton();
        miniMapPanel = new javax.swing.JPanel();
        chatTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Server");
        setAlwaysOnTop(true);
        setResizable(false);

        serverTabsPane.setToolTipText("");

        outputScrollPane.setToolTipText("");
        outputScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        output.setEditable(false);
        output.setColumns(10);
        output.setLineWrap(true);
        output.setRows(5);
        output.setWrapStyleWord(true);
        output.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        outputScrollPane.setViewportView(output);

        startButton.setText("Start Game");
        startButton.setToolTipText("It startes the game");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        versionLabel.setText("Version derp");

        gameModeScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gameModeScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        gameModeList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Free For All", "Team Deathmatch", "Capture The Flag" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        gameModeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        gameModeList.setToolTipText("Pick the game mode");
        gameModeList.setSelectedIndex(0);
        gameModeScrollPane.setViewportView(gameModeList);

        gameModeLabel.setText("Game Modes:");

        nextMapLabel.setText("Next Map");

        nextMapList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "FFA Map 1", "CTF Map 1", "TDM 2v2 Map 1", "The Pit", "The Pit 2" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        nextMapList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        nextMapList.setToolTipText("");
        nextMapList.setSelectedIndex(0);
        nextMapScrollPane.setViewportView(nextMapList);

        numPlayersConnectedLabel.setText("Num Players Connected: 0");

        numPlayersReadyLabel.setText("Num Players Ready: 0");

        hardcoreCheckBox.setText("Hardcore");
        hardcoreCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hardcoreCheckBoxActionPerformed(evt);
            }
        });

        playersConnectedList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        playersConnectedList.setToolTipText("");
        playersConnectedList.setDragEnabled(true);
        playersConnectedList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                playersConnectedListMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(playersConnectedList);

        playersConnectedLabel.setText("Players Connected");

        kickButton.setText("KICK");
        kickButton.setEnabled(false);
        kickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kickButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset Server");
        resetButton.setToolTipText("Don't press this unless your life depends on it");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainMenuPanelLayout = new javax.swing.GroupLayout(mainMenuPanel);
        mainMenuPanel.setLayout(mainMenuPanelLayout);
        mainMenuPanelLayout.setHorizontalGroup(
            mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMenuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMenuPanelLayout.createSequentialGroup()
                        .addComponent(startButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(versionLabel))
                    .addGroup(mainMenuPanelLayout.createSequentialGroup()
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenuPanelLayout.createSequentialGroup()
                                    .addComponent(playersConnectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(kickButton))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(outputScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(numPlayersReadyLabel)
                            .addComponent(numPlayersConnectedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(gameModeScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainMenuPanelLayout.createSequentialGroup()
                                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(nextMapLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hardcoreCheckBox, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(gameModeLabel, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(nextMapScrollPane))))
                .addContainerGap())
        );
        mainMenuPanelLayout.setVerticalGroup(
            mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMenuPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMenuPanelLayout.createSequentialGroup()
                        .addComponent(gameModeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hardcoreCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameModeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextMapLabel))
                    .addComponent(outputScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMenuPanelLayout.createSequentialGroup()
                        .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(playersConnectedLabel)
                            .addComponent(kickButton, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numPlayersConnectedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numPlayersReadyLabel))
                    .addComponent(nextMapScrollPane))
                .addGap(6, 6, 6)
                .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(startButton)
                        .addComponent(resetButton))
                    .addComponent(versionLabel))
                .addContainerGap())
        );

        serverTabsPane.addTab("Main Menu", mainMenuPanel);

        teamPoolList.setDragEnabled(true);
        playersConnectedListScrollPanel.setViewportView(teamPoolList);

        jLabel1.setText("Players Connected");

        setTeam0Button.setText("Set as Team 0");
        setTeam0Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTeam0ButtonActionPerformed(evt);
            }
        });

        setTeam1Button.setText("Set as Team 1");
        setTeam1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTeam1ButtonActionPerformed(evt);
            }
        });

        team0Label.setText("Team 0");

        team0ListScrollPane.setViewportView(team0List);

        team1Label.setText("Team 1");

        team1ListScrollPane.setViewportView(team1List);

        teamsRiggedCheckBox.setText("Teams Rigged");

        clearTeamButton.setText("Clear Teams");
        clearTeamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearTeamButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout teamRiggerPanelLayout = new javax.swing.GroupLayout(teamRiggerPanel);
        teamRiggerPanel.setLayout(teamRiggerPanelLayout);
        teamRiggerPanelLayout.setHorizontalGroup(
            teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teamRiggerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(teamRiggerPanelLayout.createSequentialGroup()
                        .addComponent(playersConnectedListScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(setTeam0Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(setTeam1Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(teamsRiggedCheckBox)
                            .addComponent(clearTeamButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(teamRiggerPanelLayout.createSequentialGroup()
                        .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(team0ListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(team0Label, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(team1Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(team1ListScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        teamRiggerPanelLayout.setVerticalGroup(
            teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, teamRiggerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(teamRiggerPanelLayout.createSequentialGroup()
                        .addComponent(setTeam0Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(setTeam1Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearTeamButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(teamsRiggedCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(playersConnectedListScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(team1Label)
                    .addComponent(team0Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teamRiggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(team1ListScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(team0ListScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        serverTabsPane.addTab("Team Rigger", teamRiggerPanel);

        chatTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatTextFieldKeyPressed(evt);
            }
        });

        chatTextArea.setColumns(20);
        chatTextArea.setRows(5);
        jScrollPane2.setViewportView(chatTextArea);

        javax.swing.GroupLayout miniMapPanelLayout = new javax.swing.GroupLayout(miniMapPanel);
        miniMapPanel.setLayout(miniMapPanelLayout);
        miniMapPanelLayout.setHorizontalGroup(
            miniMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miniMapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(miniMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(chatTextField))
                .addContainerGap())
        );
        miniMapPanelLayout.setVerticalGroup(
            miniMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miniMapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        serverTabsPane.addTab("Chat", miniMapPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );

        serverTabsPane.addTab("Score Board", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );

        serverTabsPane.addTab("Mini Map", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serverTabsPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serverTabsPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        teamNums = "";
        //print("randomize");
        randomizeTeams();
        //print("done randomizing, lets make a string");
        for (int i = 0; i < numPlayersConnected; i++) {
            teamNums += " " + teams.get(i);
        }
        //print("done, sending teams");
        send("TEAMS " + gameModeList.getSelectedIndex() + " " + teamNums);
        print("Final Teams: " + teamNums);
        isStarting = true;
        if (!hardcore) {
            timer.start();
        }
        //print("sending go");
        send("GO " + nextMapList.getSelectedIndex() + " " + hardcore);
        print("Game started");
    }//GEN-LAST:event_startButtonActionPerformed

    private void hardcoreCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hardcoreCheckBoxActionPerformed
        hardcore = hardcoreCheckBox.isSelected();
    }//GEN-LAST:event_hardcoreCheckBoxActionPerformed

    private void playersConnectedListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playersConnectedListMousePressed
        //print("You clicked on " + this.playersConnectedList.getSelectedIndex());
        if (threads.containsKey(this.playersConnectedList.getSelectedIndex())) {
            //print("You clicked on a working thread " + this.playersConnectedList.getSelectedIndex());
            this.kickButton.setEnabled(true);
        } else {
            this.kickButton.setEnabled(false);
        }
    }//GEN-LAST:event_playersConnectedListMousePressed

    private void kickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kickButtonActionPerformed
        print("Kicking Player " + playersConnectedList.getSelectedIndex());
        if (playersConnectedList.getSelectedIndex() < 0) {
            print("ERROR: kick index is negative");
        } else {
            send("IMLEAVING " + this.playersConnectedList.getSelectedIndex());
            //print("disabling kick button");
            kickButton.setEnabled(false);
            this.playersConnectedList.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_kickButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        print("Resetting server...");
        for (ServerThread t : threads.values()) {
            send("IMLEAVING " + t.getPlayerNum());
        }
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }
        isStarting = false;
        isAlive = false;
    }//GEN-LAST:event_resetButtonActionPerformed

    private void setTeam0ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTeam0ButtonActionPerformed
        ArrayList<String> stuff = new ArrayList<String>();
        for (int i = 0; i < numPlayersConnected; i++) {
            if (teamPoolList.isSelectedIndex(i)) {
                stuff.add((String) teamPoolList.getModel().getElementAt(i));
            }
        }
        String[] moreStuff = new String[stuff.size()];
        for (int i = 0; i < stuff.size(); i++) {
            moreStuff[i] = stuff.get(i);
        }
        print("Team 0 = " + stuff);
        team0List.setListData(moreStuff);
        if (moreStuff.length > 0) {
            teamsRiggedCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_setTeam0ButtonActionPerformed

    private void setTeam1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTeam1ButtonActionPerformed
        ArrayList<String> stuff = new ArrayList<String>();
        for (int i = 0; i < numPlayersConnected; i++) {
            if (teamPoolList.isSelectedIndex(i)) {
                stuff.add((String) teamPoolList.getModel().getElementAt(i));
            }
        }
        String[] moreStuff = new String[stuff.size()];
        for (int i = 0; i < stuff.size(); i++) {
            moreStuff[i] = stuff.get(i);
        }
        print("Team 1 = " + stuff);
        team1List.setListData(moreStuff);
        if (moreStuff.length > 0) {
            teamsRiggedCheckBox.setSelected(true);
        }
    }//GEN-LAST:event_setTeam1ButtonActionPerformed

    private void clearTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearTeamButtonActionPerformed
        team0List.setListData(new String[0]);
        team1List.setListData(new String[0]);
        teamsRiggedCheckBox.setSelected(false);
    }//GEN-LAST:event_clearTeamButtonActionPerformed

    private void chatTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chatTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            chat("CHAT -1 OVERLORD: " + this.chatTextField.getText());
            send("CHAT -1 OVERLORD: " + this.chatTextField.getText());
            this.chatTextField.setText("");
        }
    }//GEN-LAST:event_chatTextFieldKeyPressed

    public void chat(String data) {
        String message = "";
        StringTokenizer st = new StringTokenizer(data);
        st.nextToken();
        st.nextToken();
        while (st.hasMoreTokens()) {
            message += st.nextToken() + " ";
        }
        message = message.substring(0, message.length() - 1);
        this.chatTextArea.insert(message + "\n", 0);
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("BROKEN!");
            //System.exit(1);
        }
        ServerGUI finna = null;
        while (finna == null) {
            System.out.println("Trying new server");
            try {
                finna = new ServerGUI();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        System.out.println("gotta new server");
        finna.setVisible(true);
        finna.start();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JTextField chatTextField;
    private javax.swing.JButton clearTeamButton;
    private javax.swing.JLabel gameModeLabel;
    private javax.swing.JList gameModeList;
    private javax.swing.JScrollPane gameModeScrollPane;
    private javax.swing.JCheckBox hardcoreCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton kickButton;
    private javax.swing.JPanel mainMenuPanel;
    private javax.swing.JPanel miniMapPanel;
    private javax.swing.JLabel nextMapLabel;
    private javax.swing.JList nextMapList;
    private javax.swing.JScrollPane nextMapScrollPane;
    private javax.swing.JLabel numPlayersConnectedLabel;
    private javax.swing.JLabel numPlayersReadyLabel;
    private javax.swing.JTextArea output;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JLabel playersConnectedLabel;
    private javax.swing.JList playersConnectedList;
    private javax.swing.JScrollPane playersConnectedListScrollPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JTabbedPane serverTabsPane;
    private javax.swing.JButton setTeam0Button;
    private javax.swing.JButton setTeam1Button;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel team0Label;
    private javax.swing.JList team0List;
    private javax.swing.JScrollPane team0ListScrollPane;
    private javax.swing.JLabel team1Label;
    private javax.swing.JList team1List;
    private javax.swing.JScrollPane team1ListScrollPane;
    private javax.swing.JList teamPoolList;
    private javax.swing.JPanel teamRiggerPanel;
    private javax.swing.JCheckBox teamsRiggedCheckBox;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}