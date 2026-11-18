package com.comp2042;

import javafx.scene.image.Image;

import java.awt.Point;

public class GoldCoin extends Coin {
    private Image image = new Image("resources/views/res/goldcoin.png");

    /** Creates this coin at the position selected by the spawner. */
    GoldCoin(Point position) {
        super(position);
    }

    public Image getImage() {
        return image;
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}
