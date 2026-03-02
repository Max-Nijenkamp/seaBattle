package seaBattle.ui;

import seaBattle.game.SceneManager;
import seaBattle.network.GameClient;

import javax.swing.*;
import java.awt.*;

public class GameScreen {

    private static final int SIZE = 10;
    private static boolean myTurn = false;
    private static Cell[][] ownCells = new Cell[SIZE][SIZE];
    private static Cell[][] enemyCells = new Cell[SIZE][SIZE];
    private static JLabel statusLabel;

    private static JPanel makeGridWithLabels(String title, Cell[][] cellGrid, boolean clickable, GameClient client) {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(SeaBattleStyle.BG);

        JLabel heading = new JLabel(title);
        heading.setFont(SeaBattleStyle.HEADING_FONT);
        heading.setForeground(SeaBattleStyle.TEXT);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        outer.add(heading, BorderLayout.NORTH);

        JPanel colLabels = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        colLabels.setBackground(SeaBattleStyle.BG);
        colLabels.add(Box.createHorizontalStrut(SeaBattleStyle.CELL_SIZE));
        for (int i = 0; i < SIZE; i++) {
            JLabel l = new JLabel(String.valueOf((char) ('A' + i)));
            l.setFont(SeaBattleStyle.BODY_FONT);
            l.setPreferredSize(new Dimension(SeaBattleStyle.CELL_SIZE, 20));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            colLabels.add(l);
        }
        outer.add(colLabels, BorderLayout.PAGE_START);

        JPanel rowAndGrid = new JPanel(new BorderLayout(0, 0));
        rowAndGrid.setBackground(SeaBattleStyle.BG);
        JPanel rowLabels = new JPanel(new GridLayout(SIZE, 1, 0, 0));
        rowLabels.setBackground(SeaBattleStyle.BG);
        for (int i = 1; i <= SIZE; i++) {
            JLabel l = new JLabel(String.valueOf(i));
            l.setFont(SeaBattleStyle.BODY_FONT);
            l.setPreferredSize(new Dimension(20, SeaBattleStyle.CELL_SIZE));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            rowLabels.add(l);
        }
        rowAndGrid.add(rowLabels, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(SIZE, SIZE, 1, 1));
        grid.setBackground(SeaBattleStyle.GRID_LINE);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Cell cell = new Cell(x, y, false);
                cellGrid[x][y] = cell;
                if (clickable && client != null) {
                    final int fx = x, fy = y;
                    cell.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            if (!myTurn) return;
                            if (enemyCells[fx][fy].isRevealed()) return;
                            client.send("SHOT;" + fx + ";" + fy);
                            myTurn = false;
                            if (statusLabel != null) statusLabel.setText("Waiting for opponent...");
                        }
                    });
                }
                grid.add(cell);
            }
        }
        rowAndGrid.add(grid, BorderLayout.CENTER);
        outer.add(rowAndGrid, BorderLayout.CENTER);
        return outer;
    }

    public static JPanel create(GameClient client) {
        myTurn = false;
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(SeaBattleStyle.BG);
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING));

        JPanel topInfo = new JPanel(new BorderLayout(4, 4));
        topInfo.setBackground(SeaBattleStyle.BG);
        String role = client != null ? client.getRole() : "Player";
        JLabel roleLabel = new JLabel("You are: " + role);
        roleLabel.setFont(SeaBattleStyle.TITLE_FONT);
        roleLabel.setForeground(SeaBattleStyle.TEXT);
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topInfo.add(roleLabel, BorderLayout.NORTH);
        statusLabel = new JLabel("Waiting for your turn...");
        statusLabel.setFont(SeaBattleStyle.BODY_FONT);
        statusLabel.setForeground(SeaBattleStyle.TEXT_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topInfo.add(statusLabel, BorderLayout.CENTER);
        root.add(topInfo, BorderLayout.NORTH);

        JPanel boards = new JPanel(new GridLayout(2, 1, 0, 20));
        boards.setBackground(SeaBattleStyle.BG);

        boolean[][] ownShips = client != null ? client.getOwnShips() : new boolean[SIZE][SIZE];
        JPanel ownSection = makeGridWithLabels("Your board", ownCells, false, null);
        boards.add(ownSection);
        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                if (x < ownShips.length && y < ownShips[0].length && ownShips[x][y] && ownCells[x][y] != null)
                    ownCells[x][y].setShip(true);

        JPanel enemySection = makeGridWithLabels("Enemy board — click a cell to shoot", enemyCells, true, client);
        boards.add(enemySection);

        root.add(boards, BorderLayout.CENTER);
        return root;
    }

    // set the my turn
    public static void setMyTurn(boolean turn) {
        myTurn = turn;
        if (statusLabel != null)
            statusLabel.setText(turn ? "Your turn — click a cell to shoot" : "Waiting for opponent...");
    }

    // mark the enemy hit
    public static void markEnemyHit(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && enemyCells[x][y] != null)
            enemyCells[x][y].setHit();
    }

    // mark the enemy miss
    public static void markEnemyMiss(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && enemyCells[x][y] != null)
            enemyCells[x][y].setMiss();
    }

    // mark the own hit
    public static void markOwnHit(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && ownCells[x][y] != null)
            ownCells[x][y].setHit();
    }

    // mark the own miss
    public static void markOwnMiss(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && ownCells[x][y] != null)
            ownCells[x][y].setMiss();
    }

    // show the result
    public static void showResult(String message) {
        JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // show the game over
    public static void showGameOver(GameClient client, String message) {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JLabel result = new JLabel(message, SwingConstants.CENTER);
        result.setFont(SeaBattleStyle.TITLE_FONT);
        root.add(result, BorderLayout.CENTER);
        if (client != null && "Host".equals(client.getRole())) {
            JButton restart = new JButton("Restart");
            restart.addActionListener(e -> client.send("RESTART"));
            root.add(restart, BorderLayout.SOUTH);
        } else {
            JLabel wait = new JLabel("Waiting for host to restart...", SwingConstants.CENTER);
            root.add(wait, BorderLayout.SOUTH);
        }
        SceneManager.setPanel(root, 400, 200);
    }
}
