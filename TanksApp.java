package webshtuff;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class TanksApp extends javax.swing.JFrame {

    private boolean playerReady = false;
    private Client client;
    private int upgradePoints = 10;
    public int healthUpgrade = 0;
    public int damageUpgrade = 0;
    public int airBurstUpgrade = 0;
    public int jumpUpgrade = 0;
    public int dropUpgrade = 0;
    public int speedUpgrade = 0;
    public int cooldownUpgrade = 0;
    public int shieldUpgrade = 0;
    private boolean easterEggActive = false;
    private boolean preRun;
    private HandlesEverythingExceptTheGUI gamePanel;
    private double version = 2.3;
    private boolean imAlive = true;
    private StopWatch countThing = new StopWatch();
    public StopWatch pinger = new StopWatch();
    private String serverIp = "";
    public int gameMode = 0;
    private int map = 0;
    private boolean connectionMade = false;
    public boolean hardcoreOn;
    public ArrayList<Integer> teams;
    public String team0Name = "Team Derpasaurus";
    public String team1Name = "Team Herpopotamus";
    public ArrayList<TextChat> chats = new ArrayList<TextChat>();
    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    boolean stopPlayback = true;
    private StopWatch clock = new StopWatch();
    public ArrayList<String> names = new ArrayList<String>();
    public String name = "derp";

    public TanksApp() {
        initComponents();
        versionLabel.setText("Version " + version);
        teams = new ArrayList<Integer>();
        for (int i = 0; i < 6; i++) {
            teams.add(-1);
            names.add("Player " + i);
        }
    }

    @Override
    public void dispose() {
        printProcess("Shutdown sequence initiated");
        if (client == null) {
            System.exit(0);
        }
        client.disconnecting(client.getPlayerNum());
        printProcess("TanksApp has sucsessfully asked client to close");
        countThing.start();
        while (countThing.getTime() < 2) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        printProcess("Disconnect time exceeded, force quitting");
        System.exit(0);
    }

    public void printProcess(String text) {
        System.out.println(text);
        readoutTextField.insert(text + "\n", 0);
    }

    public void setHardcore(boolean b) {
        hardcoreOn = b;
    }

    public void confirmConnection() {
        connectionMade = true;
        printProcess("Client confirms connection");
    }

    public void printResults(String text) {
        resultsTextField.append(text + "\n");
    }

    public void quit() {
        imAlive = false;
        printProcess("MasterUnit has recived death, shutting down");
    }

    public void setMap(int mapNum) {
        map = mapNum;
    }

    public void setGameMode(int gm) {
        gameMode = gm;
    }

    public HandlesEverythingExceptTheGUI getHandles() {
        return gamePanel;
    }

    public double getVersion() {
        return Double.parseDouble(versionLabel.getText().replace("Version ", ""));
    }

    public void urVersionIsTooOld() {
        JOptionPane.showMessageDialog(this, "Ur Version is too old, contact Seva bro.", "Error", 2);
    }

    public void connectionFailed() {
        //printProcess("THE, CLIENT, HAS NOT COME");
        playerReadyButton.setEnabled(false);
        serverIPLabel.setText("Server I.P.");
        //printProcess("not Connected!");
        connectButton.setEnabled(true);
    }

    public void setTheName(int pNum, String name) {
        if (pNum == client.getPlayerNum()) {
            serverNameLabel.setText("Your name is: " + name);
        }
        names.set(pNum, name);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlPanel = new javax.swing.JPanel();
        playerListPanel = new javax.swing.JPanel();
        playerListLabel = new javax.swing.JLabel();
        playerReadyButton = new javax.swing.JToggleButton();
        numPlayersReadyLabel = new javax.swing.JLabel();
        numPlayersConnectedLabel = new javax.swing.JLabel();
        playerNameLabel = new javax.swing.JLabel();
        playerNameTextField = new javax.swing.JTextField();
        serverNameLabel = new javax.swing.JLabel();
        ipTextField = new javax.swing.JTextField();
        serverIPLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        gameResultLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTextField = new javax.swing.JTextArea();
        chatLabel = new javax.swing.JLabel();
        chatInputField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        chatss = new javax.swing.JTextArea();
        readoutScrollPane = new javax.swing.JScrollPane();
        readoutTextField = new javax.swing.JTextArea();
        readoutLabel = new javax.swing.JLabel();
        themes = new javax.swing.JComboBox();
        musicCheckBox = new javax.swing.JCheckBox();
        upgradesPanel = new javax.swing.JPanel();
        upgradesLabel = new javax.swing.JLabel();
        totalUpgradePointsBar = new javax.swing.JProgressBar();
        healthUpgradesLabel = new javax.swing.JLabel();
        damageUpgradesLabel = new javax.swing.JLabel();
        airBurstUpgradesLabel = new javax.swing.JLabel();
        jumpUpgradesLabel = new javax.swing.JLabel();
        dropUpgradesLabel = new javax.swing.JLabel();
        speedUpgradesLabel = new javax.swing.JLabel();
        cooldownUpgradesLabel = new javax.swing.JLabel();
        health1CheckBox = new javax.swing.JCheckBox();
        cooldown1CheckBox = new javax.swing.JCheckBox();
        speed1CheckBox = new javax.swing.JCheckBox();
        jump1CheckBox = new javax.swing.JCheckBox();
        airBurst1CheckBox = new javax.swing.JCheckBox();
        damage1CheckBox = new javax.swing.JCheckBox();
        health2CheckBox = new javax.swing.JCheckBox();
        damage2CheckBox = new javax.swing.JCheckBox();
        airBurst2CheckBox = new javax.swing.JCheckBox();
        jump2CheckBox = new javax.swing.JCheckBox();
        speed2CheckBox = new javax.swing.JCheckBox();
        cooldown2CheckBox = new javax.swing.JCheckBox();
        health3CheckBox = new javax.swing.JCheckBox();
        damage3CheckBox = new javax.swing.JCheckBox();
        speed3CheckBox = new javax.swing.JCheckBox();
        cooldown3CheckBox = new javax.swing.JCheckBox();
        drop1CheckBox = new javax.swing.JCheckBox();
        shield1CheckBox = new javax.swing.JCheckBox();
        speed4CheckBox = new javax.swing.JCheckBox();
        sheildLabel = new javax.swing.JLabel();
        shield2CheckBox = new javax.swing.JCheckBox();
        versionLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tanks");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(1200, 750));
        setMinimumSize(new java.awt.Dimension(600, 650));
        getContentPane().setLayout(new java.awt.CardLayout());

        controlPanel.setPreferredSize(new java.awt.Dimension(1200, 750));
        controlPanel.setLayout(new java.awt.GridLayout(1, 2));

        playerListLabel.setFont(new java.awt.Font("Tunga", 1, 36)); // NOI18N
        playerListLabel.setText("Player List");
        playerListLabel.setToolTipText("");

        playerReadyButton.setText("READY!");
        playerReadyButton.setEnabled(false);
        playerReadyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerReadyButtonActionPerformed(evt);
            }
        });

        numPlayersReadyLabel.setText("Players Ready: 0");

        numPlayersConnectedLabel.setText("Players Connected: 0");

        playerNameLabel.setText("New name:");

        playerNameTextField.setEnabled(false);
        playerNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                playerNameTextFieldKeyPressed(evt);
            }
        });

        serverNameLabel.setText("Your name is: derp");

        ipTextField.setText("10.13.101.183");

        serverIPLabel.setText("Server I.P.");

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        gameResultLabel.setText("Last Game Results");

        resultsTextField.setEditable(false);
        resultsTextField.setColumns(20);
        resultsTextField.setRows(5);
        jScrollPane1.setViewportView(resultsTextField);

        chatLabel.setText("Chat");

        chatInputField.setEnabled(false);
        chatInputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatInputFieldKeyPressed(evt);
            }
        });

        chatss.setEditable(false);
        chatss.setColumns(20);
        chatss.setRows(5);
        chatss.setWrapStyleWord(true);
        jScrollPane2.setViewportView(chatss);

        readoutScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        readoutTextField.setEditable(false);
        readoutTextField.setColumns(20);
        readoutTextField.setRows(5);
        readoutTextField.setWrapStyleWord(true);
        readoutScrollPane.setViewportView(readoutTextField);

        readoutLabel.setText("Process Readout");

        themes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default Theme", "Space Theme", "Jungle Theme" }));

        musicCheckBox.setText("Music");
        musicCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                musicCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout playerListPanelLayout = new javax.swing.GroupLayout(playerListPanel);
        playerListPanel.setLayout(playerListPanelLayout);
        playerListPanelLayout.setHorizontalGroup(
            playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1)
                    .addGroup(playerListPanelLayout.createSequentialGroup()
                        .addComponent(chatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatInputField))
                    .addComponent(readoutScrollPane)
                    .addGroup(playerListPanelLayout.createSequentialGroup()
                        .addGroup(playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerReadyButton)
                            .addComponent(playerListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(serverNameLabel)
                            .addComponent(numPlayersConnectedLabel)
                            .addGroup(playerListPanelLayout.createSequentialGroup()
                                .addComponent(serverIPLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectButton))
                            .addComponent(gameResultLabel)
                            .addComponent(readoutLabel)
                            .addComponent(numPlayersReadyLabel)
                            .addGroup(playerListPanelLayout.createSequentialGroup()
                                .addComponent(playerNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(themes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(musicCheckBox)))
                        .addGap(0, 104, Short.MAX_VALUE)))
                .addContainerGap())
        );
        playerListPanelLayout.setVerticalGroup(
            playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerReadyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numPlayersReadyLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numPlayersConnectedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNameLabel)
                    .addComponent(playerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(themes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(musicCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serverNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverIPLabel)
                    .addComponent(connectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameResultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(playerListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(playerListPanelLayout.createSequentialGroup()
                        .addComponent(chatLabel)
                        .addGap(5, 5, 5))
                    .addComponent(chatInputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(readoutLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(readoutScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        controlPanel.add(playerListPanel);

        upgradesLabel.setFont(new java.awt.Font("Tunga", 1, 36)); // NOI18N
        upgradesLabel.setText("Upgrades");

        totalUpgradePointsBar.setMaximum(10);
        totalUpgradePointsBar.setToolTipText("");
        totalUpgradePointsBar.setValue(10);
        totalUpgradePointsBar.setName(""); // NOI18N
        totalUpgradePointsBar.setStringPainted(true);

        healthUpgradesLabel.setText("Health");

        damageUpgradesLabel.setText("Damage");

        airBurstUpgradesLabel.setText("Air Burst");

        jumpUpgradesLabel.setText("Jump");

        dropUpgradesLabel.setText("Drop");

        speedUpgradesLabel.setText("Speed");

        cooldownUpgradesLabel.setText("Cooldown");

        health1CheckBox.setText("1");
        health1CheckBox.setToolTipText("+30% Health");
        health1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                health1CheckBoxActionPerformed(evt);
            }
        });

        cooldown1CheckBox.setText("1");
        cooldown1CheckBox.setToolTipText("+33% Cooldown Reduction");
        cooldown1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cooldown1CheckBoxActionPerformed(evt);
            }
        });

        speed1CheckBox.setText("1");
        speed1CheckBox.setToolTipText("+15% Speed");
        speed1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speed1CheckBoxActionPerformed(evt);
            }
        });

        jump1CheckBox.setText("2");
        jump1CheckBox.setToolTipText("Double Jump Height");
        jump1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jump1CheckBoxActionPerformed(evt);
            }
        });

        airBurst1CheckBox.setText("3");
        airBurst1CheckBox.setToolTipText("75% Damage, 400% Explosive Radius");
        airBurst1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                airBurst1CheckBoxActionPerformed(evt);
            }
        });

        damage1CheckBox.setText("1");
        damage1CheckBox.setToolTipText("+25% Bullet Damage");
        damage1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                damage1CheckBoxActionPerformed(evt);
            }
        });

        health2CheckBox.setText("1");
        health2CheckBox.setToolTipText("+30% Health");
        health2CheckBox.setEnabled(false);
        health2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                health2CheckBoxActionPerformed(evt);
            }
        });

        damage2CheckBox.setText("1");
        damage2CheckBox.setToolTipText("+25% Bullet Damage");
        damage2CheckBox.setEnabled(false);
        damage2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                damage2CheckBoxActionPerformed(evt);
            }
        });

        airBurst2CheckBox.setText("3");
        airBurst2CheckBox.setToolTipText("75% Damage, 600% Explosive Radius");
        airBurst2CheckBox.setEnabled(false);
        airBurst2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                airBurst2CheckBoxActionPerformed(evt);
            }
        });

        jump2CheckBox.setText("3");
        jump2CheckBox.setToolTipText("Be a Bird");
        jump2CheckBox.setEnabled(false);
        jump2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jump2CheckBoxActionPerformed(evt);
            }
        });

        speed2CheckBox.setText("1");
        speed2CheckBox.setToolTipText("+15% Speed");
        speed2CheckBox.setEnabled(false);
        speed2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speed2CheckBoxActionPerformed(evt);
            }
        });

        cooldown2CheckBox.setText("1");
        cooldown2CheckBox.setToolTipText("+33% Cooldown Reduction");
        cooldown2CheckBox.setEnabled(false);
        cooldown2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cooldown2CheckBoxActionPerformed(evt);
            }
        });

        health3CheckBox.setText("1");
        health3CheckBox.setToolTipText("+30% Health");
        health3CheckBox.setEnabled(false);
        health3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                health3CheckBoxActionPerformed(evt);
            }
        });

        damage3CheckBox.setText("1");
        damage3CheckBox.setToolTipText("+25% Bullet Damage");
        damage3CheckBox.setEnabled(false);
        damage3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                damage3CheckBoxActionPerformed(evt);
            }
        });

        speed3CheckBox.setText("1");
        speed3CheckBox.setToolTipText("+15% Speed");
        speed3CheckBox.setEnabled(false);
        speed3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speed3CheckBoxActionPerformed(evt);
            }
        });

        cooldown3CheckBox.setText("1");
        cooldown3CheckBox.setToolTipText("+33% Cooldown Reduction");
        cooldown3CheckBox.setEnabled(false);
        cooldown3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cooldown3CheckBoxActionPerformed(evt);
            }
        });

        drop1CheckBox.setText("4");
        drop1CheckBox.setToolTipText("Activate Drop Through Platforms");
        drop1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drop1CheckBoxActionPerformed(evt);
            }
        });

        shield1CheckBox.setText("3");
        shield1CheckBox.setToolTipText("Activate Sheild, 50% Power");
        shield1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shield1CheckBoxActionPerformed(evt);
            }
        });

        speed4CheckBox.setText("8");
        speed4CheckBox.setToolTipText("Super Ultra Mega Speed");
        speed4CheckBox.setEnabled(false);
        speed4CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speed4CheckBoxActionPerformed(evt);
            }
        });

        sheildLabel.setText("Shield");

        shield2CheckBox.setText("2");
        shield2CheckBox.setToolTipText("100% Power");
        shield2CheckBox.setEnabled(false);
        shield2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shield2CheckBoxActionPerformed(evt);
            }
        });

        versionLabel.setText("Version derp");
        versionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                versionLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout upgradesPanelLayout = new javax.swing.GroupLayout(upgradesPanel);
        upgradesPanel.setLayout(upgradesPanelLayout);
        upgradesPanelLayout.setHorizontalGroup(
            upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upgradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalUpgradePointsBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, upgradesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(versionLabel))
                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                        .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(upgradesLabel)
                            .addGroup(upgradesPanelLayout.createSequentialGroup()
                                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(damageUpgradesLabel)
                                    .addComponent(airBurstUpgradesLabel)
                                    .addComponent(jumpUpgradesLabel)
                                    .addComponent(speedUpgradesLabel)
                                    .addComponent(dropUpgradesLabel))
                                .addGap(24, 24, 24)
                                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(drop1CheckBox)
                                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                                        .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(speed1CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(airBurst1CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jump1CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(damage1CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(airBurst2CheckBox)
                                            .addComponent(jump2CheckBox)
                                            .addGroup(upgradesPanelLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                                                        .addComponent(damage2CheckBox)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(damage3CheckBox))
                                                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                                                        .addComponent(speed2CheckBox)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(speed3CheckBox)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(speed4CheckBox))))))))
                            .addGroup(upgradesPanelLayout.createSequentialGroup()
                                .addComponent(healthUpgradesLabel)
                                .addGap(36, 36, 36)
                                .addComponent(health1CheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(health2CheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(health3CheckBox))
                            .addGroup(upgradesPanelLayout.createSequentialGroup()
                                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cooldownUpgradesLabel)
                                    .addComponent(sheildLabel))
                                .addGap(18, 18, 18)
                                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                                        .addComponent(cooldown1CheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cooldown2CheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cooldown3CheckBox))
                                    .addGroup(upgradesPanelLayout.createSequentialGroup()
                                        .addComponent(shield1CheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(shield2CheckBox)))))
                        .addGap(0, 191, Short.MAX_VALUE)))
                .addContainerGap())
        );
        upgradesPanelLayout.setVerticalGroup(
            upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(upgradesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(upgradesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalUpgradePointsBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(healthUpgradesLabel)
                    .addComponent(health3CheckBox)
                    .addComponent(health2CheckBox)
                    .addComponent(health1CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(damageUpgradesLabel)
                    .addComponent(damage1CheckBox)
                    .addComponent(damage2CheckBox)
                    .addComponent(damage3CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(airBurstUpgradesLabel)
                    .addComponent(airBurst1CheckBox)
                    .addComponent(airBurst2CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jumpUpgradesLabel)
                    .addComponent(jump1CheckBox)
                    .addComponent(jump2CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dropUpgradesLabel)
                    .addComponent(drop1CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedUpgradesLabel)
                    .addComponent(speed1CheckBox)
                    .addComponent(speed2CheckBox)
                    .addComponent(speed3CheckBox)
                    .addComponent(speed4CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cooldownUpgradesLabel)
                    .addComponent(cooldown1CheckBox)
                    .addComponent(cooldown2CheckBox)
                    .addComponent(cooldown3CheckBox))
                .addGap(18, 18, 18)
                .addGroup(upgradesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sheildLabel)
                    .addComponent(shield1CheckBox)
                    .addComponent(shield2CheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                .addComponent(versionLabel)
                .addContainerGap())
        );

        controlPanel.add(upgradesPanel);

        getContentPane().add(controlPanel, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playerReadyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerReadyButtonActionPerformed
        //COLTON REMOVED IF STATEMENT
        //SEVA PUT IT BACK
        if (client != null) {
            playerReady = !playerReady;
            //printProcess("player1Ready: " + playerReady);
            client.ready(playerReady);
        }
    }//GEN-LAST:event_playerReadyButtonActionPerformed

    // <editor-fold defaultstate="collapsed" desc="Upgrade Handling">
    private void health1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_health1CheckBoxActionPerformed
        if (health1CheckBox.isSelected()) {
            upgradePoints--;
            healthUpgrade++;
            health2CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            healthUpgrade--;
            if (health2CheckBox.isSelected()) {
                health2CheckBox.doClick();
            }
            health2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_health1CheckBoxActionPerformed

    private void health2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_health2CheckBoxActionPerformed
        if (health2CheckBox.isSelected()) {
            upgradePoints--;
            healthUpgrade++;
            health3CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            healthUpgrade--;
            if (health3CheckBox.isSelected()) {
                health3CheckBox.doClick();
            }
            health3CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_health2CheckBoxActionPerformed

    private void health3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_health3CheckBoxActionPerformed
        if (health3CheckBox.isSelected()) {
            upgradePoints--;
            healthUpgrade++;
        } else {
            upgradePoints++;
            healthUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_health3CheckBoxActionPerformed

    private void damage1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_damage1CheckBoxActionPerformed
        if (damage1CheckBox.isSelected()) {
            upgradePoints--;
            damageUpgrade++;
            damage2CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            damageUpgrade--;
            if (damage2CheckBox.isSelected()) {
                damage2CheckBox.doClick();
            }
            damage2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_damage1CheckBoxActionPerformed

    private void damage2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_damage2CheckBoxActionPerformed
        if (damage2CheckBox.isSelected()) {
            upgradePoints--;
            damageUpgrade++;
            damage3CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            damageUpgrade--;
            if (damage3CheckBox.isSelected()) {
                damage3CheckBox.doClick();
            }
            damage3CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_damage2CheckBoxActionPerformed

    private void damage3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_damage3CheckBoxActionPerformed
        if (damage3CheckBox.isSelected()) {
            upgradePoints--;
            damageUpgrade++;
        } else {
            upgradePoints++;
            damageUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_damage3CheckBoxActionPerformed

    private void airBurst1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_airBurst1CheckBoxActionPerformed
        if (airBurst1CheckBox.isSelected()) {
            upgradePoints -= 3;
            airBurstUpgrade++;
            airBurst2CheckBox.setEnabled(true);
        } else {
            upgradePoints += 3;
            airBurstUpgrade--;
            if (airBurst2CheckBox.isSelected()) {
                airBurst2CheckBox.doClick();
            }
            airBurst2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_airBurst1CheckBoxActionPerformed

    private void airBurst2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_airBurst2CheckBoxActionPerformed
        if (airBurst2CheckBox.isSelected()) {
            upgradePoints -= 3;
            airBurstUpgrade++;
        } else {
            upgradePoints += 3;
            airBurstUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_airBurst2CheckBoxActionPerformed

    private void jump1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jump1CheckBoxActionPerformed
        if (jump1CheckBox.isSelected()) {
            upgradePoints -= 2;
            jumpUpgrade++;
            jump2CheckBox.setEnabled(true);
        } else {
            upgradePoints += 2;
            jumpUpgrade--;
            if (jump2CheckBox.isSelected()) {
                jump2CheckBox.doClick();
            }
            jump2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_jump1CheckBoxActionPerformed

    private void jump2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jump2CheckBoxActionPerformed
        if (jump2CheckBox.isSelected()) {
            upgradePoints -= 3;
            jumpUpgrade++;
        } else {
            upgradePoints += 3;
            jumpUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_jump2CheckBoxActionPerformed

    private void drop1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drop1CheckBoxActionPerformed
        if (drop1CheckBox.isSelected()) {
            upgradePoints -= 4;
            dropUpgrade++;
        } else {
            upgradePoints += 4;
            dropUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_drop1CheckBoxActionPerformed

    private void speed1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speed1CheckBoxActionPerformed
        if (speed1CheckBox.isSelected()) {
            upgradePoints--;
            speedUpgrade++;
            speed2CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            speedUpgrade--;
            if (speed2CheckBox.isSelected()) {
                speed2CheckBox.doClick();
            }
            speed2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_speed1CheckBoxActionPerformed

    private void speed2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speed2CheckBoxActionPerformed
        if (speed2CheckBox.isSelected()) {
            upgradePoints--;
            speedUpgrade++;
            speed3CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            speedUpgrade--;
            if (speed3CheckBox.isSelected()) {
                speed3CheckBox.doClick();
            }
            speed3CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_speed2CheckBoxActionPerformed

    private void speed3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speed3CheckBoxActionPerformed
        if (speed3CheckBox.isSelected()) {
            upgradePoints--;
            speedUpgrade++;
            speed4CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            speedUpgrade--;
            if (speed4CheckBox.isSelected()) {
                speed4CheckBox.doClick();
            }
            speed4CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_speed3CheckBoxActionPerformed

    private void cooldown1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cooldown1CheckBoxActionPerformed
        if (cooldown1CheckBox.isSelected()) {
            upgradePoints--;
            cooldownUpgrade++;
            cooldown2CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            cooldownUpgrade--;
            if (cooldown2CheckBox.isSelected()) {
                cooldown2CheckBox.doClick();
            }
            cooldown2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_cooldown1CheckBoxActionPerformed

    private void cooldown2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cooldown2CheckBoxActionPerformed
        if (cooldown2CheckBox.isSelected()) {
            upgradePoints--;
            cooldownUpgrade++;
            cooldown3CheckBox.setEnabled(true);
        } else {
            upgradePoints++;
            cooldownUpgrade--;
            if (cooldown3CheckBox.isSelected()) {
                cooldown3CheckBox.doClick();
            }
            cooldown3CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_cooldown2CheckBoxActionPerformed

    private void cooldown3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cooldown3CheckBoxActionPerformed
        if (cooldown3CheckBox.isSelected()) {
            upgradePoints--;
            cooldownUpgrade++;
        } else {
            upgradePoints++;
            cooldownUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_cooldown3CheckBoxActionPerformed

    private void shield1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shield1CheckBoxActionPerformed
        if (shield1CheckBox.isSelected()) {
            upgradePoints -= 3;
            shieldUpgrade++;
            shield2CheckBox.setEnabled(true);
        } else {
            upgradePoints += 3;
            shieldUpgrade--;
            if (shield2CheckBox.isSelected()) {
                shield2CheckBox.doClick();
            }
            shield2CheckBox.setEnabled(false);
        }
        verifyUpgradeUse();

    }//GEN-LAST:event_shield1CheckBoxActionPerformed

    private void speed4CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speed4CheckBoxActionPerformed
        if (speed4CheckBox.isSelected()) {
            upgradePoints -= 8;
            speedUpgrade++;
        } else {
            upgradePoints += 8;
            speedUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_speed4CheckBoxActionPerformed

    private void shield2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shield2CheckBoxActionPerformed
        if (shield2CheckBox.isSelected()) {
            upgradePoints -= 2;
            shieldUpgrade++;
        } else {
            upgradePoints += 2;
            shieldUpgrade--;
        }
        verifyUpgradeUse();
    }//GEN-LAST:event_shield2CheckBoxActionPerformed
// </editor-fold>

    private void versionLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_versionLabelMousePressed
        if (evt.getClickCount() > 3) {
            easterEggActive = true;
        }
    }//GEN-LAST:event_versionLabelMousePressed

    private void playerNameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_playerNameTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (playerNameTextField.getText().trim().length() != 0) {
                if (!playerNameTextField.getText().equals("OVERLORD")) {
                    client.disperseName(client.getPlayerNum(), playerNameTextField.getText());
                    name = playerNameTextField.getText();
                    playerNameTextField.setText("");
                }
            }
        }
        if (this.playerNameTextField.getText().length() > 7) {
            this.playerNameTextField.setText(this.playerNameTextField.getText().substring(0, 7));
        }
    }//GEN-LAST:event_playerNameTextFieldKeyPressed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        printProcess("Awaiting Server Connection...");
        client = null;
        while (client == null) {
            try {
                //printProcess("WE ARE MAKING NEW CLIENT");
                client = new Client(this, ipTextField.getText());
                //printProcess("WE HAVE MADE NEW CLIENT");
            } catch (IOException ex) {
                //System.err.println("Your game is broken. You failed :(");
                System.err.println("Error in TanksApp: " + ex.getMessage());
            }
        }
        if (client.getSocket() != null) {
            playerReadyButton.setEnabled(true);
            serverIp = ipTextField.getText();
            serverIPLabel.setText("Connecected To:");
            //printProcess("Connected!");
            connectButton.setEnabled(false);
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void chatInputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chatInputFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            TextChat temp = new TextChat();
            temp.append(names.get(client.getPlayerNum()) + ": " + this.chatInputField.getText());
            client.chat(temp);
            chatInputField.setText("");
        }
    }//GEN-LAST:event_chatInputFieldKeyPressed

    private void musicCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_musicCheckBoxActionPerformed
        if (musicCheckBox.isSelected()) {
            try {
                File soundFile = new File(getClass().getResource("Sound0.wav").getPath());
                File soundFile2 = new File(getClass().getResource("Sound1.wav").getPath());
                printProcess("Sound files are primed and ready.");
            } catch (Exception e) {
                printProcess("ERROR: Missing sound files");
                JOptionPane.showMessageDialog(this, "Missing sound files", "Error", 2);
                musicCheckBox.setSelected(false);
            }
        } else {
        }
    }//GEN-LAST:event_musicCheckBoxActionPerformed

    public void verifyUpgradeUse() {
        if (!shield1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(shield1CheckBox.getText()).intValue()) {
                shield1CheckBox.setEnabled(false);
            } else {
                shield1CheckBox.setEnabled(true);
            }
        }
        if (!shield2CheckBox.isSelected() && shield1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(shield2CheckBox.getText()).intValue()) {
                shield2CheckBox.setEnabled(false);
            } else {
                shield2CheckBox.setEnabled(true);
            }
        }
        if (!health1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(health1CheckBox.getText()).intValue()) {
                health1CheckBox.setEnabled(false);
            } else {
                health1CheckBox.setEnabled(true);
            }
        }
        if (!health2CheckBox.isSelected() && health1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(health2CheckBox.getText()).intValue()) {
                health2CheckBox.setEnabled(false);
            } else {
                health2CheckBox.setEnabled(true);
            }
        }
        if (!health3CheckBox.isSelected() && health2CheckBox.isSelected()) {
            if (upgradePoints < new Integer(health3CheckBox.getText()).intValue()) {
                health3CheckBox.setEnabled(false);
            } else {
                health3CheckBox.setEnabled(true);
            }
        }
        if (!damage1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(damage1CheckBox.getText()).intValue()) {
                damage1CheckBox.setEnabled(false);
            } else {
                damage1CheckBox.setEnabled(true);
            }
        }
        if (!damage2CheckBox.isSelected() && damage1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(damage2CheckBox.getText()).intValue()) {
                damage2CheckBox.setEnabled(false);
            } else {
                damage2CheckBox.setEnabled(true);
            }
        }
        if (!damage3CheckBox.isSelected() && damage2CheckBox.isSelected()) {
            if (upgradePoints < new Integer(damage3CheckBox.getText()).intValue()) {
                damage3CheckBox.setEnabled(false);
            } else {
                damage3CheckBox.setEnabled(true);
            }
        }
        if (!airBurst1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(airBurst1CheckBox.getText()).intValue()) {
                airBurst1CheckBox.setEnabled(false);
            } else {
                airBurst1CheckBox.setEnabled(true);
            }
        }
        if (!airBurst2CheckBox.isSelected() && airBurst1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(airBurst2CheckBox.getText()).intValue()) {
                airBurst2CheckBox.setEnabled(false);
            } else {
                airBurst2CheckBox.setEnabled(true);
            }
        }

        if (!jump1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(jump1CheckBox.getText()).intValue()) {
                jump1CheckBox.setEnabled(false);
            } else {
                jump1CheckBox.setEnabled(true);
            }
        }
        if (!jump2CheckBox.isSelected() && jump1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(jump2CheckBox.getText()).intValue()) {
                jump2CheckBox.setEnabled(false);
            } else {
                jump2CheckBox.setEnabled(true);
            }
        }
        if (!drop1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(drop1CheckBox.getText()).intValue()) {
                drop1CheckBox.setEnabled(false);
            } else {
                drop1CheckBox.setEnabled(true);
            }
        }
        if (!speed1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(speed1CheckBox.getText()).intValue()) {
                speed1CheckBox.setEnabled(false);
            } else {
                speed1CheckBox.setEnabled(true);
            }
        }
        if (!speed2CheckBox.isSelected() && speed1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(speed2CheckBox.getText()).intValue()) {
                speed2CheckBox.setEnabled(false);
            } else {
                speed2CheckBox.setEnabled(true);
            }
        }
        if (!speed3CheckBox.isSelected() && speed2CheckBox.isSelected()) {
            if (upgradePoints < new Integer(speed3CheckBox.getText()).intValue()) {
                speed3CheckBox.setEnabled(false);
            } else {
                speed3CheckBox.setEnabled(true);
            }
        }
        if (!speed4CheckBox.isSelected() && speed3CheckBox.isSelected()) {
            if (upgradePoints < new Integer(speed4CheckBox.getText()).intValue()) {
                speed4CheckBox.setEnabled(false);
            } else {
                speed4CheckBox.setEnabled(true);
            }
        }
        if (!cooldown1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(cooldown1CheckBox.getText()).intValue()) {
                cooldown1CheckBox.setEnabled(false);
            } else {
                cooldown1CheckBox.setEnabled(true);
            }
        }
        if (!cooldown2CheckBox.isSelected() && cooldown1CheckBox.isSelected()) {
            if (upgradePoints < new Integer(cooldown2CheckBox.getText()).intValue()) {
                cooldown2CheckBox.setEnabled(false);
            } else {
                cooldown2CheckBox.setEnabled(true);
            }
        }
        if (!cooldown3CheckBox.isSelected() && cooldown2CheckBox.isSelected()) {
            if (upgradePoints < new Integer(cooldown3CheckBox.getText()).intValue()) {
                cooldown3CheckBox.setEnabled(false);
            } else {
                cooldown3CheckBox.setEnabled(true);
            }
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("BROKEN!");
            //System.exit(1);
        }
        TanksApp straightBars = new TanksApp();
        straightBars.setVisible(true);
        straightBars.start();
    }

    public void dispMessage(TextChat m) {
        chats.add(m);
        m.display();
    }

    private void playAudio(String file) {
        try {
            File soundFile = new File(getClass().getResource(file).getPath());
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            stopPlayback = false;
            new PlayThread(this, audioInputStream, audioFormat, sourceDataLine).start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            stopPlayback = true;
        }
    }

    public void start() {
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        StringTokenizer st;
        printProcess("Game waiting for client to connect...");
        while (!connectionMade) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted:" + ex.getMessage());
            }
            totalUpgradePointsBar.setValue(upgradePoints);
        }
        this.chatInputField.setEnabled(true);
        this.playerNameTextField.setEnabled(true);
        printProcess("Client is connected.");
        client.disperseName(client.getPlayerNum(), "Player " + client.getPlayerNum());
        pinger.start();
        while (imAlive) {
            try {
                chatss.removeAll();
                if (chats.size() > 10) {
                    chats.remove(0);
                }
                for (int i = 0; i < chats.size(); i++) {
                    if (!chats.get(i).how()) {
                        chatss.insert(chats.get(i).getMessage() + "\n", 0);
                        chats.get(i).wow();
                    }
                    if (chats.get(i).isDead()) {
                        chats.remove(i);
                        i--;
                    }
                }
            } catch (Exception e) {
            }
            totalUpgradePointsBar.setValue(upgradePoints);
            numPlayersReadyLabel.setText("Players Ready: " + client.getNumPlayersReady());
            numPlayersConnectedLabel.setText("Players Connected: " + client.getNumPlayersConnected());
            preRun = true;
            while (imAlive && client.isRunning()) {
                //System.out.println("game is isRunning");
                if (preRun) {
                    getContentPane().removeAll();
                    gamePanel = new HandlesEverythingExceptTheGUI();
                    gamePanel.init(client, healthUpgrade, damageUpgrade, airBurstUpgrade, jumpUpgrade, dropUpgrade, speedUpgrade, cooldownUpgrade, shieldUpgrade, easterEggActive, map, gameMode, teams, themes.getSelectedIndex(), names);
                    getContentPane().add(gamePanel);
                    //getContentPane()
                    gamePanel.grabFocus();
                    addKeyListener(gamePanel);
                    gamePanel.setPreferredSize(new Dimension(1200, 750));
                    //gamePanel.setMaximumSize(new Dimension(1200, 750));
                    preRun = false;
                    pack();
                    if (musicCheckBox.isSelected()) {
                        String theMusic = "Sound1.wav";
                        if (easterEggActive) {
                            theMusic = "Sound0.wav";
                        }
                        playAudio(theMusic);
                    }
                    clock.start();
                }
                //while (!preRun) {
                gamePanel.run();
                while (clock.getTime() < 0.01) {
                    //System.out.println("WAITING");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        System.err.println("Interrupted:" + ex.getMessage());
                    }
                }
                clock.reset();
                clock.start();
                //}
            }
            if (!preRun && !client.isRunning()) {
                clock.reset();
                stopPlayback = true;
                getContentPane().removeAll();
                upgradePoints = 10;
                healthUpgrade = 0;
                damageUpgrade = 0;
                airBurstUpgrade = 0;
                jumpUpgrade = 0;
                dropUpgrade = 0;
                speedUpgrade = 0;
                cooldownUpgrade = 0;
                shieldUpgrade = 0;
                playerReady = false;
                easterEggActive = false;
                preRun = true;
                initComponents();
                versionLabel.setText("Version " + version);
                playerReadyButton.setEnabled(true);
                playerNameTextField.setEnabled(true);
                serverNameLabel.setText("Your name is: " + name);
                serverIPLabel.setText("Connecected To:");
                ipTextField.setText(serverIp);
                connectButton.setEnabled(false);
                chatInputField.setEnabled(true);
                ArrayList<DataBox> temp = client.getGameData();
                for (DataBox s : temp) {
                    printResults(s.toString());
                }
                this.playerReadyButton.doClick();
                this.playerReadyButton.doClick();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted:" + ex.getMessage());
            }
        }
        //printProcess("This is now dead");
        System.exit(0);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox airBurst1CheckBox;
    private javax.swing.JCheckBox airBurst2CheckBox;
    private javax.swing.JLabel airBurstUpgradesLabel;
    private javax.swing.JTextField chatInputField;
    private javax.swing.JLabel chatLabel;
    private javax.swing.JTextArea chatss;
    private javax.swing.JButton connectButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JCheckBox cooldown1CheckBox;
    private javax.swing.JCheckBox cooldown2CheckBox;
    private javax.swing.JCheckBox cooldown3CheckBox;
    private javax.swing.JLabel cooldownUpgradesLabel;
    private javax.swing.JCheckBox damage1CheckBox;
    private javax.swing.JCheckBox damage2CheckBox;
    private javax.swing.JCheckBox damage3CheckBox;
    private javax.swing.JLabel damageUpgradesLabel;
    private javax.swing.JCheckBox drop1CheckBox;
    private javax.swing.JLabel dropUpgradesLabel;
    private javax.swing.JLabel gameResultLabel;
    private javax.swing.JCheckBox health1CheckBox;
    private javax.swing.JCheckBox health2CheckBox;
    private javax.swing.JCheckBox health3CheckBox;
    private javax.swing.JLabel healthUpgradesLabel;
    private javax.swing.JTextField ipTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox jump1CheckBox;
    private javax.swing.JCheckBox jump2CheckBox;
    private javax.swing.JLabel jumpUpgradesLabel;
    private javax.swing.JCheckBox musicCheckBox;
    private javax.swing.JLabel numPlayersConnectedLabel;
    private javax.swing.JLabel numPlayersReadyLabel;
    private javax.swing.JLabel playerListLabel;
    private javax.swing.JPanel playerListPanel;
    private javax.swing.JLabel playerNameLabel;
    private javax.swing.JTextField playerNameTextField;
    private javax.swing.JToggleButton playerReadyButton;
    private javax.swing.JLabel readoutLabel;
    private javax.swing.JScrollPane readoutScrollPane;
    private javax.swing.JTextArea readoutTextField;
    private javax.swing.JTextArea resultsTextField;
    private javax.swing.JLabel serverIPLabel;
    private javax.swing.JLabel serverNameLabel;
    private javax.swing.JLabel sheildLabel;
    private javax.swing.JCheckBox shield1CheckBox;
    private javax.swing.JCheckBox shield2CheckBox;
    private javax.swing.JCheckBox speed1CheckBox;
    private javax.swing.JCheckBox speed2CheckBox;
    private javax.swing.JCheckBox speed3CheckBox;
    private javax.swing.JCheckBox speed4CheckBox;
    private javax.swing.JLabel speedUpgradesLabel;
    private javax.swing.JComboBox themes;
    private javax.swing.JProgressBar totalUpgradePointsBar;
    private javax.swing.JLabel upgradesLabel;
    private javax.swing.JPanel upgradesPanel;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

    void conLost() {
        JOptionPane.showMessageDialog(this, "Connection to Server Lost", "Error", 2);
        System.exit(1);
    }
}