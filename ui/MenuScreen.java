package seaBattle.ui;

import seaBattle.game.SceneManager;

import javax.swing.*;
import java.awt.*;

public class MenuScreen {
    
    public static JPanel create() {
        JPanel root = new JPanel();
        root.setBackground(SeaBattleStyle.BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2));

        root.add(Box.createVerticalGlue());
        JLabel title = new JLabel("Sea Battle");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(SeaBattleStyle.PADDING));

        JButton host = new JButton("Host Game");
        host.setAlignmentX(Component.CENTER_ALIGNMENT);
        host.addActionListener(e -> SceneManager.showHostLobby());
        root.add(host);
        root.add(Box.createVerticalStrut(16));
        JButton join = new JButton("Join Game");
        join.setAlignmentX(Component.CENTER_ALIGNMENT);
        join.addActionListener(e -> SceneManager.showJoinScreen());
        root.add(join);
        root.add(Box.createVerticalGlue());

        return root;
    }
}
