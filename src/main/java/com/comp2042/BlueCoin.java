package com.comp2042;

import javafx.scene.image.Image;

import java.awt.Point;

public class BlueCoin extends Coin {
    private Image image = new Image("resources/views/res/bluecoin.png");

    /** Creates this coin at the position selected by the spawner. */
    BlueCoin(Point position) {
        super(position);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }
}
