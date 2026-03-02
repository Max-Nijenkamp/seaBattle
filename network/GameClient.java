package seaBattle.network;

import seaBattle.game.SceneManager;
import seaBattle.ui.GameScreen;
import seaBattle.ui.PreparationScreen;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class GameClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String role = "Player";
    private boolean[][] ownShips = new boolean[10][10];

    // connect to the server
    public boolean connect(String ip, int port, String username) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("JOIN;" + username);
            listen();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // send a message to the server
    public void send(String message) {
        if (out != null)
            out.println(message);
    }

    // get the role of the client
    public String getRole() {
        return role != null ? role : "Player";
    }

    // register a ship cell
    public void registerOwnShipCell(int x, int y) {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            ownShips[x][y] = true;
        }
    }

    // get the own ships
    public boolean[][] getOwnShips() {
        return ownShips;
    }

    // clear the own ships
    public void clearOwnShips() {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                ownShips[i][j] = false;
    }

    // listen for messages from the server
    private void listen() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("SERVER: " + msg);
                    if (msg.startsWith("ROLE;")) {
                        String r = msg.substring(5).trim().toLowerCase();
                        role = r.equals("host") ? "Host" : r.equals("client") ? "Client" : r;
                    } else if (msg.equals("START_PREP")) {
                        SwingUtilities.invokeLater(() ->
                                SceneManager.setPanel(PreparationScreen.create(GameClient.this, false), 620, 780));
                    } else if (msg.equals("RESTART")) {
                        clearOwnShips();
                        boolean isHost = "Host".equals(role);
                        SwingUtilities.invokeLater(() ->
                                SceneManager.setPanel(PreparationScreen.create(GameClient.this, isHost), 620, 780));
                    } else if (msg.equals("START_TURNS")) {
                        SwingUtilities.invokeLater(() -> SceneManager.showGameScreen(this));
                    } else if (msg.equals("YOUR_TURN")) {
                        SwingUtilities.invokeLater(() -> GameScreen.setMyTurn(true));
                    } else if (msg.equals("WAIT")) {
                        SwingUtilities.invokeLater(() -> GameScreen.setMyTurn(false));
                    } else if (msg.startsWith("HIT;")) {
                        String[] p = msg.split(";");
                        int gx = Integer.parseInt(p[1]), gy = Integer.parseInt(p[2]);
                        SwingUtilities.invokeLater(() -> GameScreen.markEnemyHit(gx, gy));
                    } else if (msg.startsWith("MISS;")) {
                        String[] p = msg.split(";");
                        int gx = Integer.parseInt(p[1]), gy = Integer.parseInt(p[2]);
                        SwingUtilities.invokeLater(() -> GameScreen.markEnemyMiss(gx, gy));
                    } else if (msg.startsWith("HIT_AT;")) {
                        String[] p = msg.split(";");
                        int gx = Integer.parseInt(p[1]), gy = Integer.parseInt(p[2]);
                        SwingUtilities.invokeLater(() -> GameScreen.markOwnHit(gx, gy));
                    } else if (msg.startsWith("MISS_AT;")) {
                        String[] p = msg.split(";");
                        int gx = Integer.parseInt(p[1]), gy = Integer.parseInt(p[2]);
                        SwingUtilities.invokeLater(() -> GameScreen.markOwnMiss(gx, gy));
                    } else if (msg.equals("WIN")) {
                        SwingUtilities.invokeLater(() -> GameScreen.showGameOver(GameClient.this, "You win!"));
                    } else if (msg.equals("LOSE")) {
                        SwingUtilities.invokeLater(() -> GameScreen.showGameOver(GameClient.this, "You lose!"));
                    }
                }
            } catch (IOException ignored) {}
        }).start();
    }
}
