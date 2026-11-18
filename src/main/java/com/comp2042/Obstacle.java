package com.comp2042;

import javafx.scene.paint.Color;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Obstacle {
    private Color color = Color.BLACK;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;

    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

    /**
     * Places obstacles on free cells and returns the number placed.
     *
     * @param snek the snake whose cells must stay clear
     * @param coin the coin whose cell must stay clear
     * @param amount the requested number of obstacles
     * @return the number of obstacles added to the board
     */
    public int newObstacles(Snake snek, Coin coin, int amount) {
        return newObstacles(snek.getHead(), snek.getBody(), coin.getPos(), amount, new Random());
    }

    /** Places up to the requested number of obstacles, reserving a cell for the next coin. */
    int newObstacles(Point head, Collection<Point> body, Point coin, int amount, Random random) {
        if (obstacles.size() > 50 || amount <= 0) {
            return 0;
        }

        Set<Point> occupied = new HashSet<>(body);
        occupied.add(head);
        occupied.add(coin);
        occupied.addAll(obstacles);

        ArrayList<Point> freeCells = new ArrayList<>();
        for (int row = 0; row < y; row++) {
            for (int column = 0; column < x; column++) {
                Point cell = new Point(column, row);
                if (!occupied.contains(cell)) {
                    freeCells.add(cell);
                }
            }
        }

        int count = Math.min(amount, freeCells.size() - 1);
        if (count <= 0) {
            return 0;
        }

        Collections.shuffle(freeCells, random);
        obstacles.addAll(freeCells.subList(0, count));
        return count;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }
}
