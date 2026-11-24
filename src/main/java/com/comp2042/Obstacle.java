package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Obstacle {
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

        List<Point> occupied = new ArrayList<>(body);
        occupied.add(head);
        occupied.add(coin);
        occupied.addAll(obstacles);
        List<Point> freeCells = FreeCells.onBoard(x, y, occupied);

        int count = Math.min(amount, freeCells.size() - 1);
        if (count <= 0) {
            return 0;
        }

        Collections.shuffle(freeCells, random);
        obstacles.addAll(freeCells.subList(0, count));
        return count;
    }

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }
}
