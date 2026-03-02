package seaBattle.ui;

import javax.swing.*;
import java.awt.*;

public class Cell extends JPanel {

    private final int x;
    private final int y;
    private boolean hasShip = false;
    private boolean revealed = false;
    private Runnable onToggle;

    public Cell(int x, int y) {
        this(x, y, false, null);
    }

    public Cell(int x, int y, boolean ignoredPlacementFlag) {
        this(x, y, ignoredPlacementFlag, null);
    }

    public Cell(int x, int y, boolean ignoredPlacementFlag, Runnable onToggle) {
        this.x = x;
        this.y = y;
        this.onToggle = onToggle;
        int size = SeaBattleStyle.CELL_SIZE;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setBackground(SeaBattleStyle.WATER);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SeaBattleStyle.GRID_LINE, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        setOpaque(true);
    }

    // set the ship
    public void setShip(boolean ship) {
        if (this.hasShip == ship) return;
        this.hasShip = ship;
        setBackground(ship ? SeaBattleStyle.SHIP : SeaBattleStyle.WATER);
        if (onToggle != null) onToggle.run();
        repaint();
    }

    // check if the cell has a ship
    public boolean hasShip() {
        return hasShip;
    }

    // set the hit
    public void setHit() {
        revealed = true;
        setBackground(SeaBattleStyle.HIT);
        repaint();
    }

    // set the miss
    public void setMiss() {
        revealed = true;
        setBackground(SeaBattleStyle.MISS);
        repaint();
    }

    // check if the cell is revealed
    public boolean isRevealed() {
        return revealed;
    }

    // get the grid X coordinate
    public int getGridX() { return x; }

    // get the grid Y coordinate
    public int getGridY() { return y; }
}
