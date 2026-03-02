package seaBattle.ui;

import seaBattle.game.SceneManager;
import seaBattle.network.GameClient;
import seaBattle.network.GameServer;
import seaBattle.network.LANBroadcaster;
import seaBattle.network.NetworkUtils;

import javax.swing.*;
import java.awt.*;

public class HostLobbyScreen {
    
    private static GameServer server;
    private static GameClient hostClient;
    private static JPanel playersBox;
    private static JButton startGame;

    public static JPanel create() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(SeaBattleStyle.BG);
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING));

        int port = NetworkUtils.findAvailablePort(5000);
        if (port < 0) {
            JOptionPane.showMessageDialog(null, "No available port found (5000–5019 in use).", "Cannot host", JOptionPane.ERROR_MESSAGE);
            SceneManager.showMenuScreen();
            return root;
        }

        server = new GameServer(port);
        server.start();

        hostClient = new GameClient();
        final int hostPort = port;
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            hostClient.connect("127.0.0.1", hostPort, SceneManager.username);
        }).start();

        new LANBroadcaster().startBroadcast(SceneManager.username, port);

        JLabel title = new JLabel("Lobby (Host)");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setBackground(SeaBattleStyle.BG);
        JLabel ipLabel = new JLabel("Share this address: " + server.getHostAddress());
        ipLabel.setFont(SeaBattleStyle.BODY_FONT);
        ipLabel.setForeground(SeaBattleStyle.TEXT_MUTED);
        top.add(ipLabel, BorderLayout.NORTH);
        playersBox = new JPanel(new GridLayout(0, 1, 0, 4));
        playersBox.setBackground(SeaBattleStyle.BG);
        JLabel hostLabel = new JLabel("• " + SceneManager.username + " (you)");
        hostLabel.setFont(SeaBattleStyle.BODY_FONT);
        playersBox.add(hostLabel);
        top.add(playersBox, BorderLayout.CENTER);
        root.add(top, BorderLayout.CENTER);

        startGame = new JButton("Start Game");
        startGame.setEnabled(false);
        server.setPlayerJoinListener((username, count) -> {
            SwingUtilities.invokeLater(() -> {
                if (count > 1) {
                    JLabel l = new JLabel("• " + username);
                    l.setFont(SeaBattleStyle.BODY_FONT);
                    playersBox.add(l);
                    playersBox.revalidate();
                }
                if (count >= 2) startGame.setEnabled(true);
            });
        });
        startGame.addActionListener(e -> {
            server.startPreparation();
            SceneManager.setPanel(PreparationScreen.create(hostClient, true), 620, 780);
        });
        root.add(startGame, BorderLayout.SOUTH);

        return root;
    }
}
