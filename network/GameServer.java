package seaBattle.network;

import seaBattle.game.Board;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    /** Interface for when a player joins the game */
    public interface PlayerJoinListener {
        void onPlayerJoined(String username, int count);
    }

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private int port;
    private PlayerJoinListener playerJoinListener;

    private Board hostBoard = new Board();
    private Board clientBoard = new Board();

    private boolean hostReady = false;
    private boolean clientReady = false;
    private volatile boolean gameStarted = false;

    private int currentTurn = 0;

    // constructor
    public GameServer(int port) {
        this.port = port;
    }

    // set the player join listener
    public void setPlayerJoinListener(PlayerJoinListener listener) {
        this.playerJoinListener = listener;
    }

    // get the host address
    public String getHostAddress() {
        return NetworkUtils.getLocalIPAddress() + ":" + port;
    }

    // start the server
    public void start() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server gestart op " + getHostAddress());

                while (clients.size() < 2) {
                    Socket socket = serverSocket.accept();
                    int playerId = clients.size();
                    ClientHandler handler = new ClientHandler(socket, playerId);
                    clients.add(handler);
                    handler.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // start the preparation
    public void startPreparation() {
        broadcast("START_PREP");
    }

    // reset the game
    private synchronized void resetGame() {
        hostReady = false;
        clientReady = false;
        gameStarted = false;
        hostBoard = new Board();
        clientBoard = new Board();
        currentTurn = 0;
    }

    // start the turns
    private void startTurns() {
        sendToPlayer(0, "YOUR_TURN");   // host shoots first
        sendToPlayer(1, "WAIT");       // client waits
    }

    // check if both players are ready
    private synchronized void checkBothReady() {
        if (hostReady && clientReady && !gameStarted && clients.size() == 2) {
            gameStarted = true;
            sendToPlayer(0, "ROLE;host");
            sendToPlayer(1, "ROLE;client");
            broadcast("START_TURNS");
            startTurns();
        }
    }

    // host is ready
    public void hostReady() {
        hostReady = true;
        checkBothReady();
    }

    // broadcast a message to all clients
    private void broadcast(String message) {
        for (ClientHandler c : clients)
            c.send(message);
    }

    // send a message to a player
    private void sendToPlayer(int player, String msg) {
        if (player >= 0 && player < clients.size())
            clients.get(player).send(msg);
    }

    // client handler
    private class ClientHandler extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int playerId;

        public ClientHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
        }

        // send a message to the client
        public void send(String msg) {
            out.println(msg);
        }

        // run the client handler
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {

                    if (line.startsWith("JOIN;")) {
                        String username = line.substring(5).trim();
                        if (playerJoinListener != null)
                            playerJoinListener.onPlayerJoined(username, clients.size());
                        continue;
                    }

                    if (line.startsWith("PLACE;")) {
                        String[] p = line.split(";");
                        int x = Integer.parseInt(p[2]);
                        int y = Integer.parseInt(p[3]);
                        if (playerId == 0)
                            hostBoard.place(x, y);
                        else
                            clientBoard.place(x, y);
                    }

                    if (line.equals("READY")) {
                        if (playerId == 0) hostReady = true;
                        else clientReady = true;
                        checkBothReady();
                    }

                    if (line.equals("RESTART") && playerId == 0) {
                        resetGame();
                        broadcast("RESTART");
                        continue;
                    }

                    if (line.startsWith("SHOT;")) {
                        if (playerId != currentTurn) continue;
                        String[] p = line.split(";");
                        int x = Integer.parseInt(p[1]);
                        int y = Integer.parseInt(p[2]);
                        if (x >= 0 && x < 10 && y >= 0 && y < 10)
                            handleShot(playerId, x, y);
                    }
                }

            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        }
    }

    // handle a shot
    private void handleShot(int shooter, int x, int y) {
        Board target = (shooter == 0) ? clientBoard : hostBoard;
        Boolean result = target.shoot(x, y);
        if (result == null) return;

        boolean hit = result;
        int other = 1 - shooter;

        if (hit) {
            sendToPlayer(shooter, "HIT;" + x + ";" + y);
            sendToPlayer(other, "HIT_AT;" + x + ";" + y);
        } else {
            sendToPlayer(shooter, "MISS;" + x + ";" + y);
            sendToPlayer(other, "MISS_AT;" + x + ";" + y);
        }

        if (target.allShipsSunk()) {
            sendToPlayer(shooter, "WIN");
            sendToPlayer(other, "LOSE");
            return;
        }

        currentTurn = other;
        sendToPlayer(currentTurn, "YOUR_TURN");
        sendToPlayer(shooter, "WAIT");
    }
}
