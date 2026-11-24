package com.comp2042;


import java.awt.Point;

public class SilverCoin extends Coin {
    /** Creates this coin at the position selected by the spawner. */
    SilverCoin(Point position) {
        super(position, CoinType.SILVER);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }
}
