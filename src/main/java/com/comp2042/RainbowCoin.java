package com.comp2042;


import java.awt.Point;

public class RainbowCoin extends Coin {
    /** Creates this coin at the position selected by the spawner. */
    RainbowCoin(Point position) {
        super(position, CoinType.RAINBOW);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public double getSpeedBonus() {
        double speedBonus = 5;
        return speedBonus;
    }

}
