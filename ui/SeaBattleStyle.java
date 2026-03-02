package seaBattle.ui;

import java.awt.*;

public final class SeaBattleStyle {
    
    private SeaBattleStyle() {}

    public static final Color BG = new Color(245, 248, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color WATER = new Color(176, 214, 230);
    public static final Color SHIP = new Color(41, 75, 132);
    public static final Color HIT = new Color(198, 53, 53);
    public static final Color MISS = new Color(140, 140, 140);
    public static final Color GRID_LINE = new Color(80, 100, 120);
    public static final Color ACCENT = new Color(41, 98, 255);
    public static final Color TEXT = new Color(33, 37, 41);
    public static final Color TEXT_MUTED = new Color(108, 117, 125);

    public static final int CELL_SIZE = 34;
    public static final int PADDING = 24;
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
}
