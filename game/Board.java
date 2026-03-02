package seaBattle.game;

public class Board {
    
    private final int SIZE = 10;
    private boolean[][] ships = new boolean[SIZE][SIZE];
    private boolean[][] shots = new boolean[SIZE][SIZE];

    /** @param x, y grid coordinates */
    public void place(int x, int y) {
        ships[x][y] = true;
    }

    /** @return true = hit, false = miss, null = already shot at this cell (invalid) */
    public Boolean shoot(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || shots[x][y]) return null;

        shots[x][y] = true;

        return ships[x][y];
    }

    /** @return true if all ships are sunk */
    public boolean allShipsSunk() {
        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                if (ships[x][y] && !shots[x][y])
                    return false;

        return true;
    }

}