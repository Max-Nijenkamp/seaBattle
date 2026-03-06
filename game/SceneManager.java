package seaBattle.game;

import seaBattle.network.GameClient;
import seaBattle.ui.GameScreen;
import seaBattle.ui.HostLobbyScreen;
import seaBattle.ui.JoinScreen;
import seaBattle.ui.MenuScreen;
import seaBattle.ui.UsernameScreen;

import javax.swing.*;

public class SceneManager {

    public static String username;

    // set the panel
    public static void setPanel(JPanel panel, int width, int height) {
        JFrame f = SeaBattleApp.getFrame();

        if (f == null) return;

        f.setContentPane(panel);
        f.setSize(width, height);
        f.validate();
        f.repaint();
    }

    /** Show the username screen */
    public static void showUsernameScreen() {
        setPanel(UsernameScreen.create(), 400, 300);
    }

    /** Show the menu screen */
    public static void showMenuScreen() {
        setPanel(MenuScreen.create(), 400, 300);
    }

    /** Show the host lobby screen */
    public static void showHostLobby() {
        setPanel(HostLobbyScreen.create(), 500, 450);
    }

    /** Show the join screen */
    public static void showJoinScreen() {
        setPanel(JoinScreen.create(), 500, 400);
    }

    /** Show the game screen */
    public static void showGameScreen(GameClient client) {
        setPanel(GameScreen.create(client), 600, 700);
    }
}
