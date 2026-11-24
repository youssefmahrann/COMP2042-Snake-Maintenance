package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;

public class Snake {
    private boolean dead = false;
    private final Point head = new Point();
    private final ArrayList<Point> body = new ArrayList<>();
    private final int gx;
    private final int gy;
    private boolean ignoreBorders = false;
    private boolean noBorder;
    private Obstacle obstacle;
    private int snakeDir = 270;

    public Snake(int x, int y, boolean noBorder, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.noBorder = noBorder;
        this.head.setLocation(x / 2, y / 2);
        this.body.add(new Point(x / 2 + 1, y / 2));
        this.gx = x;
        this.gy = y;
    }

    /** Moves one cell on a turn with no coin at the destination. */
    public void move(char dir) {
        move(dir, null);
    }

    /**
     * Moves toward the requested direction and checks the landing cell.
     * The tail remains occupied if the landing cell contains a coin.
     *
     * @param dir the movement direction
     * @param coinPosition the active coin position, or null for a move without a coin
     */
    void move(char dir, Point coinPosition) {
        if (dead) {
            return;
        }

        Point destination = new Point(head);
        switch (dir) {
            case 'U' -> {
                snakeDir = 0;
                destination.translate(0, -1);
            }
            case 'D' -> {
                snakeDir = 180;
                destination.translate(0, 1);
            }
            case 'L' -> {
                snakeDir = 270;
                destination.translate(-1, 0);
            }
            case 'R' -> {
                snakeDir = 90;
                destination.translate(1, 0);
            }
            default -> {
                return;
            }
        }

        if (isOutsideBoard(destination)) {
            if (!ignoreBorders && !noBorder) {
                dead = true;
                return;
            }
            destination.setLocation(Math.floorMod(destination.x, gx), Math.floorMod(destination.y, gy));
        }

        if (obstacle.getObstacles().contains(destination)) {
            dead = true;
            return;
        }

        body.add(new Point(head));
        head.setLocation(destination);
        int firstOccupiedSegment = destination.equals(coinPosition) ? 0 : 1;
        dead = body.subList(firstOccupiedSegment, body.size()).contains(destination);
    }

    public boolean ateCoin(Coin coin) {
        return ateCoinAt(coin.getPos());
    }

    /** Updates growth after moving, based on the coin position. */
    boolean ateCoinAt(Point coinPosition) {
        if (!head.equals(coinPosition)) {
            body.remove(0);
        }
        return head.equals(coinPosition);
    }

    /** Checks whether a proposed move leaves the playable grid. */
    private boolean isOutsideBoard(Point position) {
        return position.x < 0 || position.y < 0 || position.x >= gx || position.y >= gy;
    }

    public boolean hitSelf() {
        return body.contains(head);
    }

    public ArrayList<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Point getHead() {
        return new Point(head);
    }

    public boolean getDead() {
        return dead;
    }

    public void ignoreBorders(boolean ign) {
        ignoreBorders = ign;
    }

    public int getSnakeDir() {
        return snakeDir;
    }

}
