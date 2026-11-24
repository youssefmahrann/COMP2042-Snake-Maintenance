package com.comp2042;

import java.util.function.BooleanSupplier;

public class Game {
    private int width;
    private int height;
    private int score;
    private int maxScore;
    private boolean boardFull;
    private int scorePoints;
    private double speed;
    private boolean powerUp;
    private boolean noBorder;

    public Game(int x, int y, boolean powerUp, char difficulty) {
        if (difficulty == 'E') {
            speed = 8.5;
            noBorder = true;
        } else if (difficulty == 'N') {
            speed = 8.5;
            noBorder = false;
        } else if (difficulty == 'H') {
            speed = 13;
            noBorder = false;
        }
        this.powerUp = powerUp;
        width = x;
        height = y;
        maxScore = x * y - 2;
        this.powerUp = powerUp;
    }

    public boolean gameWon() {
        return boardFull || score == maxScore;
    }

    /** Records the points once and requests a replacement only while play can continue. */
    void recordCollection(int points, BooleanSupplier spawnNextCoin) {
        if (gameWon()) {
            return;
        }
        addPoints(points);
        incScore();
        if (!gameWon() && !spawnNextCoin.getAsBoolean()) {
            markBoardFull();
        }
    }

    /** Marks the game won when there is no free cell for a coin. */
    void markBoardFull() {
        boardFull = true;
    }

    public void incScore() {
        score++;
    }

    public void decMaxScore(int num) {
        this.maxScore -= num;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        boardFull = false;
        maxScore = width * height - 2;
        score = 0;
        scorePoints = 0;
    }

    public void addPoints(int n) {
        scorePoints += n;
    }

    public int getScorePoints() {
        return scorePoints;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }
}
