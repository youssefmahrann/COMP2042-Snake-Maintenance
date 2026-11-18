package com.comp2042;

import javafx.scene.image.Image;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class Coin {
    private final Point pos;
    private Image image = new Image("resources/views/res/redcoin.png");

    /** Creates a coin at a position selected by the spawner. */
    Coin(Point position) {
        pos = new Point(position);
    }

    /** Spawns the requested coin type, or returns empty if the board is full. */
    static Optional<Coin> spawn(CoinType type, Game game, Snake snake, Obstacle obstacle) {
        return selectPosition(game.getWidth(), game.getHeight(), snake.getHead(), snake.getBody(),
                obstacle.getObstacles(), Random::new).map(position -> switch (type) {
                    case GOLD -> new GoldCoin(position);
                    case RAINBOW -> new RainbowCoin(position);
                    case BLUE -> new BlueCoin(position);
                    case SILVER -> new SilverCoin(position);
                    case REGULAR -> new Coin(position);
                });
    }

    /** Selects one free position uniformly, or returns empty if there is none. */
    static Optional<Point> selectPosition(int width, int height, Point head,
            Collection<Point> body, Collection<Point> obstacles, Supplier<Random> randomSource) {
        List<Point> occupied = new ArrayList<>(body);
        occupied.add(head);
        occupied.addAll(obstacles);
        List<Point> free = FreeCells.onBoard(width, height, occupied);
        return free.isEmpty()
                ? Optional.empty()
                : Optional.of(free.get(randomSource.get().nextInt(free.size())));
    }

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }
}
