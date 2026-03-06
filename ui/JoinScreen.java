package seaBattle.ui;

import seaBattle.game.SceneManager;
import seaBattle.network.GameClient;
import seaBattle.network.LANListener;

import javax.swing.*;
import java.awt.*;

public class JoinScreen {

    private static final GameClient client = new GameClient();
    private static final DefaultListModel<String> serverListModel = new DefaultListModel<>();
    private static final JTextField manualAddressField = new JTextField();

    public static JPanel createWaitingPanel() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(SeaBattleStyle.BG);
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2));

        JLabel title = new JLabel("Connected");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(title, BorderLayout.NORTH);

        JLabel msg = new JLabel("<html><div style='text-align:center'>Waiting for the host to start the game.<br>You will enter ship placement when they begin.</div></html>");
        msg.setFont(SeaBattleStyle.BODY_FONT);
        msg.setForeground(SeaBattleStyle.TEXT_MUTED);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(msg, BorderLayout.CENTER);

        return root;
    }

    public static JPanel create() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(SeaBattleStyle.BG);
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING));

        JLabel title = new JLabel("Join a game");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(title, BorderLayout.NORTH);

        JLabel hint = new JLabel("Select a game from the list, or enter an address, then click Join.");
        hint.setFont(SeaBattleStyle.BODY_FONT);
        hint.setForeground(SeaBattleStyle.TEXT_MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(hint, BorderLayout.PAGE_START);

        JList<String> serverList = new JList<>(serverListModel);
        serverList.setFont(SeaBattleStyle.BODY_FONT);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serverList.setBackground(SeaBattleStyle.CARD_BG);
        JScrollPane scroll = new JScrollPane(serverList);
        scroll.setBorder(BorderFactory.createLineBorder(SeaBattleStyle.GRID_LINE, 1));

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(SeaBattleStyle.BG);
        center.add(scroll, BorderLayout.CENTER);

        JPanel manualPanel = new JPanel(new BorderLayout(4, 4));
        manualPanel.setBackground(SeaBattleStyle.BG);
        JLabel manualLabel = new JLabel("Or connect directly (ip:port):");
        manualLabel.setFont(SeaBattleStyle.BODY_FONT);
        manualLabel.setForeground(SeaBattleStyle.TEXT_MUTED);
        manualPanel.add(manualLabel, BorderLayout.NORTH);
        manualAddressField.setColumns(18);
        manualPanel.add(manualAddressField, BorderLayout.CENTER);
        center.add(manualPanel, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);

        LANListener listener = new LANListener();
        listener.listen((ip, name, port) -> {
            SwingUtilities.invokeLater(() -> {
                String entry = name + " — " + ip + ":" + port;
                if (!serverListModel.contains(entry))
                    serverListModel.addElement(entry);
            });
        });

        JButton join = new JButton("Join");
        join.addActionListener(e -> {
            String ip;
            int port;

            String selected = serverList.getSelectedValue();
            if (selected != null) {
                int sep = selected.indexOf(" — ");
                int colon = selected.indexOf(":");
                if (sep < 0 || colon < 0 || colon <= sep + 3) {
                    JOptionPane.showMessageDialog(root, "The selected entry has an invalid address.", "Invalid address", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ip = selected.substring(sep + 3, colon).trim();
                port = Integer.parseInt(selected.substring(colon + 1).trim());
            } else {
                String manual = manualAddressField.getText().trim();
                if (manual.isEmpty()) {
                    JOptionPane.showMessageDialog(root, "Select a game from the list or enter an address.", "No game selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    int colon = manual.lastIndexOf(':');
                    if (colon >= 0) {
                        ip = manual.substring(0, colon).trim();
                        port = Integer.parseInt(manual.substring(colon + 1).trim());
                    } else {
                        ip = manual;
                        port = 5000;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(root, "Could not parse the port from the address. Use format ip:port.", "Invalid address", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (client.connect(ip, port, SceneManager.username)) {
                SceneManager.setPanel(createWaitingPanel(), 480, 280);
            } else {
                JOptionPane.showMessageDialog(root, "Could not connect. Check the address and try again.", "Connection failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        root.add(join, BorderLayout.SOUTH);

        return root;
    }
}
