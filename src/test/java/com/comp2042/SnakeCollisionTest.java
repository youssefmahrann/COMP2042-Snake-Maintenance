package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

/** Exercises movement and collision rules without opening a JavaFX window. */
class SnakeCollisionTest {
    /** Every wrapped destination must be checked for obstacles. */
    @ParameterizedTest
    @CsvSource({"L, 0, 2, 5, 2", "R, 5, 2, 0, 2", "U, 3, 0, 3, 4", "D, 3, 4, 3, 0"})
    void detectsObstaclesAfterWrapping(char direction, int edgeX, int edgeY, int landingX, int landingY) {
        Obstacle obstacles = new Obstacle(6, 5);
        Snake snake = new Snake(6, 5, true, obstacles);
        moveToEdge(snake, direction, new Point(edgeX, edgeY));
        obstacles.getObstacles().add(new Point(landingX, landingY));

        snake.move(direction);

        assertTrue(snake.getDead(), "An obstacle at the wrapped destination must be lethal");
    }

    /** A short snake can collide with its middle segment on a narrow wrapped board. */
    @Test
    void detectsSelfCollisionBeforeTheSnakeHasFiveBodySegments() {
        Snake snake = new Snake(3, 3, true, new Obstacle(3, 3));
        snake.move('U');
        assertFalse(snake.getDead());
        assertTrue(snake.ateCoinAt(new Point(1, 0)));
        snake.move('L');
        assertFalse(snake.getDead());
        assertTrue(snake.ateCoinAt(new Point(0, 0)));
        snake.move('L');
        assertFalse(snake.getDead());
        assertFalse(snake.ateCoinAt(null));
        assertEquals(new Point(2, 0), snake.getHead());
        assertEquals(new Point(1, 0), snake.getBody().get(1));

        snake.move('L');

        assertTrue(snake.getDead(), "The middle segment remains occupied after the move");
    }

    /** A clear opposite edge receives the snake when wrapping is enabled. */
    @ParameterizedTest
    @CsvSource({"L, 0, 2, 5, 2", "R, 5, 2, 0, 2", "U, 3, 0, 3, 4", "D, 3, 4, 3, 0"})
    void wrapsToAnUnoccupiedCell(char direction, int edgeX, int edgeY, int landingX, int landingY) {
        Snake snake = new Snake(6, 5, true, new Obstacle(6, 5));
        moveToEdge(snake, direction, new Point(edgeX, edgeY));

        snake.move(direction);

        assertFalse(snake.getDead());
        assertEquals(new Point(landingX, landingY), snake.getHead());
    }

    /** A solid border is lethal on each side of the board. */
    @ParameterizedTest
    @CsvSource({"L, 0, 2", "R, 5, 2", "U, 3, 0", "D, 3, 4"})
    void hitsSolidWalls(char direction, int edgeX, int edgeY) {
        Snake snake = new Snake(6, 5, false, new Obstacle(6, 5));
        Point edge = new Point(edgeX, edgeY);
        moveToEdge(snake, direction, edge);

        snake.move(direction);

        assertTrue(snake.getDead());
        assertEquals(edge, snake.getHead());
    }

    /** A normal move can land on the tail cell as the tail leaves it. */
    @Test
    void entersTheVacatingTailCell() {
        Snake snake = new Snake(3, 3, true, new Obstacle(3, 3));
        snake.move('L');
        assertTrue(snake.ateCoinAt(new Point(0, 1)));
        Point tail = snake.getBody().get(0);

        snake.move('L', new Point(0, 0));

        assertFalse(snake.getDead());
        assertEquals(tail, snake.getHead());
        assertFalse(snake.ateCoinAt(new Point(0, 0)));
        assertFalse(snake.hitSelf());
        assertEquals(2, snake.getBody().size());
    }

    /** A tail cell stays occupied on a growing move, even for a short snake. */
    @Test
    void hitsTheTailIfGrowthWouldKeepItInPlace() {
        Snake snake = new Snake(3, 3, true, new Obstacle(3, 3));
        snake.move('L');
        assertTrue(snake.ateCoinAt(new Point(0, 1)));
        Point tail = snake.getBody().get(0);

        snake.move('L', tail);

        assertTrue(snake.getDead());
        assertTrue(snake.hitSelf());
    }

    /** A coin at a clear wrapped destination is collected and the tail stays in place. */
    @Test
    void growsAfterCollectingACoinAcrossAnEdge() {
        Snake snake = new Snake(6, 5, true, new Obstacle(6, 5));
        moveToEdge(snake, 'L', new Point(0, 2));
        Point oldTail = snake.getBody().get(0);
        Point coin = new Point(5, 2);

        snake.move('L', coin);

        assertFalse(snake.getDead());
        assertTrue(snake.ateCoinAt(coin));
        assertEquals(2, snake.getBody().size());
        assertEquals(oldTail, snake.getBody().get(0));
    }

    /** Blue allows a wrapped move but cannot cancel an obstacle collision. */
    @Test
    void blueWrapsButCannotCrossAnObstacle() {
        Obstacle obstacles = new Obstacle(6, 5);
        Snake snake = new Snake(6, 5, false, obstacles);
        snake.ignoreBorders(true);
        moveToEdge(snake, 'L', new Point(0, 2));
        snake.move('L');
        assertFalse(snake.getDead());
        assertFalse(snake.ateCoinAt(null));
        assertEquals(new Point(5, 2), snake.getHead());
        obstacles.getObstacles().add(new Point(0, 2));

        snake.move('R');

        assertTrue(snake.getDead());
    }

    /** Temporary wrapping still checks a body segment after reaching the opposite edge. */
    @Test
    void blueDoesNotPreventSelfCollision() {
        Snake snake = new Snake(3, 3, false, new Obstacle(3, 3));
        snake.ignoreBorders(true);
        snake.move('L', new Point(0, 1));
        assertTrue(snake.ateCoinAt(new Point(0, 1)));
        snake.move('U', new Point(0, 0));
        assertTrue(snake.ateCoinAt(new Point(0, 0)));
        snake.move('U', new Point(0, 2));
        assertTrue(snake.ateCoinAt(new Point(0, 2)));

        snake.move('U');

        assertTrue(snake.getDead());
    }

    /** Rebuilding the snake and clearing obstacles restores normal border rules. */
    @Test
    void restartRestoresSolidWallsAndClearsObstacles() {
        Game game = new Game(6, 5, true, 'N');
        Obstacle obstacles = new Obstacle(6, 5);
        Snake previous = new Snake(6, 5, game.isNoBorder(), obstacles);
        previous.ignoreBorders(true);
        obstacles.getObstacles().add(new Point(2, 2));
        previous.move('L');
        assertTrue(previous.getDead());

        Snake restarted = new Snake(6, 5, game.isNoBorder(), obstacles);
        obstacles.reset();
        assertFalse(restarted.getDead());
        moveToEdge(restarted, 'L', new Point(0, 2));
        restarted.move('L');
        assertTrue(restarted.getDead());
    }

    /** Advances without growth until the snake reaches the specified edge cell. */
    private static void moveToEdge(Snake snake, char direction, Point edge) {
        for (int step = 0; step < 6 && !snake.getHead().equals(edge); step++) {
            snake.move(direction);
            assertFalse(snake.getDead(), "The setup move must be safe");
            assertFalse(snake.ateCoinAt(null));
        }
        assertEquals(edge, snake.getHead(), "The setup must reach the expected edge");
    }
}
