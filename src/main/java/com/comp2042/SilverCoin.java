package com.comp2042;

import javafx.scene.image.Image;

import java.awt.Point;

public class SilverCoin extends Coin {
    private Image image = new Image("resources/views/res/silvercoin.png");

    /** Creates this coin at the position selected by the spawner. */
    SilverCoin(Point position) {
        super(position);
    }

    public Image getImage() {
        return image;
    }

    public int getPoints() {
        int points = 5;
        return points;
    }
}
