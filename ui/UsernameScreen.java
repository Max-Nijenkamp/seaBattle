package seaBattle.ui;

import seaBattle.game.SceneManager;

import javax.swing.*;
import java.awt.*;

public class UsernameScreen {
    
    public static JPanel create() {
        JPanel root = new JPanel();
        root.setBackground(SeaBattleStyle.BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2, SeaBattleStyle.PADDING * 2));

        root.add(Box.createVerticalGlue());
        JLabel title = new JLabel("Welcome to Sea Battle");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(8));
        JLabel sub = new JLabel("Enter your name to continue");
        sub.setFont(SeaBattleStyle.BODY_FONT);
        sub.setForeground(SeaBattleStyle.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(sub);
        root.add(Box.createVerticalStrut(SeaBattleStyle.PADDING));

        HintTextField nameField = new HintTextField(18, "Enter your username");
        nameField.setMaximumSize(new Dimension(260, 36));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SeaBattleStyle.GRID_LINE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        root.add(nameField);
        root.add(Box.createVerticalStrut(SeaBattleStyle.PADDING));

        JButton next = new JButton("Continue");
        next.setAlignmentX(Component.CENTER_ALIGNMENT);
        next.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty()) {
                SceneManager.username = nameField.getText().trim();
                SceneManager.showMenuScreen();
            }
        });
        root.add(next);
        root.add(Box.createVerticalGlue());

        return root;
    }
}
