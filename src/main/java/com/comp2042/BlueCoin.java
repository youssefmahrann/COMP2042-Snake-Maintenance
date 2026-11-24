package com.comp2042;


import java.awt.Point;

public class BlueCoin extends Coin {
    /** Creates this coin at the position selected by the spawner. */
    BlueCoin(Point position) {
        super(position, CoinType.BLUE);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

}
