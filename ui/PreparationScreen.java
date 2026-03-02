package seaBattle.ui;

import seaBattle.network.GameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

public class PreparationScreen extends JPanel {
    
    private static final int SIZE = 10;
    private static final String SHIP_FLAVOR = "ship-index";

    private static class Ship {
        final String name;
        final int size;
        int startX = -1, startY = -1;
        boolean horizontal = true;
        boolean placed = false;

        Ship(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    private static final Ship[] FLEET = {
            new Ship("Carrier", 5),
            new Ship("Battleship", 4),
            new Ship("Cruiser", 3),
            new Ship("Submarine", 3),
            new Ship("Destroyer", 2)
    };

    private static Cell[][] cells = new Cell[SIZE][SIZE];
    private static boolean[][] occupied = new boolean[SIZE][SIZE];
    private static JToggleButton orientationToggle;
    private static JLabel[] shipLabels;
    private static JButton readyButton;
    private static JPanel gridWrapper;

    public static JPanel create(GameClient client, boolean isHost) {
        for (Ship s : FLEET) {
            s.startX = s.startY = -1;
            s.horizontal = true;
            s.placed = false;
        }
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                occupied[i][j] = false;

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(SeaBattleStyle.BG);
        root.setBorder(BorderFactory.createEmptyBorder(SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING, SeaBattleStyle.PADDING));

        // Title
        JLabel title = new JLabel("Place your fleet");
        title.setFont(SeaBattleStyle.TITLE_FONT);
        title.setForeground(SeaBattleStyle.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(title, BorderLayout.NORTH);

        // Grid on top (with optional row/col labels)
        JPanel gridSection = new JPanel(new BorderLayout(0, 0));
        gridSection.setBackground(SeaBattleStyle.BG);
        JPanel gridWithLabels = new JPanel(new BorderLayout(0, 0));
        gridWithLabels.setBackground(SeaBattleStyle.BG);

        // Column labels A–J
        JPanel colLabels = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        colLabels.setBackground(SeaBattleStyle.BG);
        colLabels.add(Box.createHorizontalStrut(SeaBattleStyle.CELL_SIZE));
        for (int i = 0; i < SIZE; i++) {
            JLabel l = new JLabel(String.valueOf((char) ('A' + i)));
            l.setFont(SeaBattleStyle.BODY_FONT);
            l.setPreferredSize(new Dimension(SeaBattleStyle.CELL_SIZE, 22));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            colLabels.add(l);
        }
        gridWithLabels.add(colLabels, BorderLayout.NORTH);

        JPanel rowAndGrid = new JPanel(new BorderLayout(0, 0));
        rowAndGrid.setBackground(SeaBattleStyle.BG);
        JPanel rowLabels = new JPanel(new GridLayout(SIZE, 1, 0, 0));
        rowLabels.setBackground(SeaBattleStyle.BG);
        for (int i = 1; i <= SIZE; i++) {
            JLabel l = new JLabel(String.valueOf(i));
            l.setFont(SeaBattleStyle.BODY_FONT);
            l.setPreferredSize(new Dimension(22, SeaBattleStyle.CELL_SIZE));
            l.setHorizontalAlignment(SwingConstants.CENTER);
            rowLabels.add(l);
        }
        rowAndGrid.add(rowLabels, BorderLayout.WEST);

        gridWrapper = new JPanel(new GridLayout(SIZE, SIZE, 1, 1));
        gridWrapper.setBackground(SeaBattleStyle.GRID_LINE);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Cell cell = new Cell(x, y, false, null);
                cells[x][y] = cell;
                gridWrapper.add(cell);
            }
        }
        rowAndGrid.add(gridWrapper, BorderLayout.CENTER);
        gridWithLabels.add(rowAndGrid, BorderLayout.CENTER);
        gridSection.add(gridWithLabels, BorderLayout.CENTER);
        root.add(gridSection, BorderLayout.CENTER);

        // Drop target on grid using AWT DnD (reliable across platforms)
        gridWrapper.setDropTarget(new DropTarget(gridWrapper, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = dtde.getTransferable();
                    if (!t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        dtde.dropComplete(false);
                        return;
                    }
                    String data = (String) t.getTransferData(DataFlavor.stringFlavor);
                    if (data == null || !data.startsWith("SHIP_")) {
                        dtde.dropComplete(false);
                        return;
                    }
                    int shipIndex = Integer.parseInt(data.substring(5));
                    Point p = dtde.getLocation();
                    Component c = gridWrapper.getComponentAt(p);
                    if (!(c instanceof Cell)) {
                        dtde.dropComplete(false);
                        return;
                    }
                    Cell cell = (Cell) c;
                    int cx = cell.getGridX();
                    int cy = cell.getGridY();
                    boolean horizontal = orientationToggle != null && orientationToggle.isSelected();
                    placeShip(shipIndex, cx, cy, horizontal);
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                }
            }
        }));

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(SeaBattleStyle.BG);

        orientationToggle = new JToggleButton("Horizontal");
        orientationToggle.setSelected(true);
        orientationToggle.addActionListener(e -> orientationToggle.setText(orientationToggle.isSelected() ? "Horizontal" : "Vertical"));
        JPanel orientRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        orientRow.setBackground(SeaBattleStyle.BG);
        orientRow.add(new JLabel("Orientation: "));
        orientRow.add(orientationToggle);
        bottom.add(orientRow);
        bottom.add(Box.createVerticalStrut(8));

        JLabel dragHint = new JLabel("Drag ships onto the grid (press on a ship, then drag to a cell):");
        dragHint.setFont(SeaBattleStyle.BODY_FONT);
        dragHint.setForeground(SeaBattleStyle.TEXT_MUTED);
        dragHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(dragHint);
        bottom.add(Box.createVerticalStrut(6));

        JPanel shipStrip = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        shipStrip.setBackground(SeaBattleStyle.BG);
        shipLabels = new JLabel[FLEET.length];
        for (int i = 0; i < FLEET.length; i++) {
            Ship s = FLEET[i];
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            box.setBackground(SeaBattleStyle.BG);
            JPanel token = createShipToken(i, s);
            box.add(token);
            shipLabels[i] = new JLabel(s.name + " (" + s.size + ")");
            shipLabels[i].setFont(SeaBattleStyle.BODY_FONT);
            shipLabels[i].setForeground(SeaBattleStyle.TEXT_MUTED);
            shipLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(shipLabels[i]);
            shipStrip.add(box);
        }
        bottom.add(shipStrip);
        bottom.add(Box.createVerticalStrut(12));

        readyButton = new JButton("Ready");
        readyButton.setEnabled(false);
        readyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        readyButton.addActionListener(e -> {
            if (!allShipsPlaced()) {
                JOptionPane.showMessageDialog(root, "Place all ships first.", "Fleet incomplete", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (client != null) {
                for (int y = 0; y < SIZE; y++)
                    for (int x = 0; x < SIZE; x++)
                        if (occupied[x][y]) {
                            client.registerOwnShipCell(x, y);
                            client.send("PLACE;CELL;" + x + ";" + y + ";true");
                        }
                client.send("READY");
            }
            readyButton.setEnabled(false);
        });
        bottom.add(readyButton);

        root.add(bottom, BorderLayout.SOUTH);
        refreshShipLabels();
        return root;
    }

    private static JPanel createShipToken(int shipIndex, Ship s) {
        boolean horizontal = true;
        int w = horizontal ? s.size * (SeaBattleStyle.CELL_SIZE / 2 + 2) : SeaBattleStyle.CELL_SIZE;
        int h = horizontal ? SeaBattleStyle.CELL_SIZE : s.size * (SeaBattleStyle.CELL_SIZE / 2 + 2);
        JPanel token = new JPanel(new GridLayout(horizontal ? 1 : s.size, horizontal ? s.size : 1, 1, 1));
        token.setPreferredSize(new Dimension(w, h));
        token.setBackground(SeaBattleStyle.SHIP);
        token.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SeaBattleStyle.GRID_LINE, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        for (int i = 0; i < s.size; i++) {
            JPanel block = new JPanel();
            block.setBackground(SeaBattleStyle.SHIP);
            token.add(block);
        }
        // Use AWT DragSource so drag actually starts when user drags (works reliably)
        final int idx = shipIndex;
        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.stringFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.stringFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!DataFlavor.stringFlavor.equals(flavor)) throw new UnsupportedFlavorException(flavor);
                return "SHIP_" + idx;
            }
        };
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                token,
                DnDConstants.ACTION_COPY,
                dge -> dge.startDrag(DragSource.DefaultCopyDrop, transferable, null)
        );
        return token;
    }

    private static void placeShip(int shipIndex, int startX, int startY, boolean horizontal) {
        if (shipIndex < 0 || shipIndex >= FLEET.length) return;
        Ship s = FLEET[shipIndex];
        int dx = horizontal ? 1 : 0, dy = horizontal ? 0 : 1;

        if (s.placed) {
            int odx = s.horizontal ? 1 : 0, ody = s.horizontal ? 0 : 1;
            for (int i = 0; i < s.size; i++) {
                int x = s.startX + i * odx, y = s.startY + i * ody;
                if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
                    occupied[x][y] = false;
                    cells[x][y].setShip(false);
                }
            }
        }

        for (int i = 0; i < s.size; i++) {
            int x = startX + i * dx, y = startY + i * dy;
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || occupied[x][y]) {
                if (s.placed) {
                    int odx = s.horizontal ? 1 : 0, ody = s.horizontal ? 0 : 1;
                    for (int j = 0; j < s.size; j++) {
                        int ox = s.startX + j * odx, oy = s.startY + j * ody;
                        if (ox >= 0 && ox < SIZE && oy >= 0 && oy < SIZE) {
                            occupied[ox][oy] = true;
                            cells[ox][oy].setShip(true);
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "Ship doesn't fit here or overlaps another.", "Invalid drop", JOptionPane.WARNING_MESSAGE);
                refreshShipLabels();
                return;
            }
        }

        s.startX = startX;
        s.startY = startY;
        s.horizontal = horizontal;
        s.placed = true;
        for (int i = 0; i < s.size; i++) {
            int x = startX + i * dx, y = startY + i * dy;
            occupied[x][y] = true;
            cells[x][y].setShip(true);
        }
        refreshShipLabels();
    }

    private static boolean allShipsPlaced() {
        for (Ship s : FLEET) if (!s.placed) return false;
        return true;
    }

    private static void refreshShipLabels() {
        if (shipLabels == null) return;
        for (int i = 0; i < FLEET.length; i++)
            shipLabels[i].setText(FLEET[i].name + " (" + FLEET[i].size + ")" + (FLEET[i].placed ? " ✓" : ""));
        if (readyButton != null) readyButton.setEnabled(allShipsPlaced());
    }
}
