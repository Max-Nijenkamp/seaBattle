package seaBattle.game;

public class ShipPlacement {

    public String shipName;
    public int x;
    public int y;
    public boolean horizontal;

    // constructor
    public ShipPlacement(String shipName, int x, int y, boolean horizontal) {
        this.shipName = shipName;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

}
