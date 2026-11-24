package com.comp2042;

import java.awt.Point;

public class GoldCoin extends Coin {
    /** Creates this coin at the position selected by the spawner. */
    GoldCoin(Point position) {
        super(position, CoinType.GOLD);
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}
