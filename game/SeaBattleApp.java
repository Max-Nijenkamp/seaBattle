package seaBattle.game;

import javax.swing.*;

public class SeaBattleApp {

    private static JFrame frame;

    /** Main method */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Sea Battle LAN");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            SceneManager.showUsernameScreen();
            frame.setVisible(true);
        });
    }

    /** @return the frame */
    public static JFrame getFrame() {
        return frame;
    }
}
