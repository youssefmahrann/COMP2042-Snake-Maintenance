package com.comp2042;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** Verifies obstacle placement with repeatable random choices. */
class ObstacleTest {
    /** Checks the coordinate range on an empty rectangular board. */
    @Test
    void usesBothBoardEdgesWithoutLeavingTheBoard() {
        Obstacle obstacle = new Obstacle(6, 4);
        obstacle.newObstacles(null, List.of(), null, 18, new Random(42));

        List<Point> positions = obstacle.getObstacles();
        assertTrue(positions.stream().allMatch(p -> p.x >= 0 && p.x < 6 && p.y >= 0 && p.y < 4),
                () -> "Out-of-bounds obstacle in " + positions);
        assertTrue(positions.stream().anyMatch(p -> p.x == 0));
        assertTrue(positions.stream().anyMatch(p -> p.x == 5));
        assertTrue(positions.stream().anyMatch(p -> p.y == 0));
        assertTrue(positions.stream().anyMatch(p -> p.y == 3));
    }

    /** Checks that candidates stay unique across repeated calls. */
    @Test
    void avoidsSnakeCoinAndExistingObstacles() {
        Obstacle obstacle = new Obstacle(5, 4);
        Point head = new Point(2, 1);
        Point tail = new Point(3, 1);
        Point coin = new Point(4, 1);
        obstacle.newObstacles(head, List.of(tail), coin, 7, new Random(42));
        List<Point> firstBatch = List.copyOf(obstacle.getObstacles());
        obstacle.newObstacles(head, List.of(tail), coin, 7, new Random(42));

        List<Point> positions = obstacle.getObstacles();
        assertEquals(14, positions.size());
        assertEquals(14, new HashSet<>(positions).size());
        assertTrue(positions.containsAll(firstBatch));
        assertFalse(positions.contains(head));
        assertFalse(positions.contains(tail));
        assertFalse(positions.contains(coin));
    }

    /** Checks that a partially fulfilled Silver batch leaves a replacement coin cell. */
    @Test
    void silverCoinLeavesOneFreeCellAndUsesTheActualPlacementCount() {
        Obstacle obstacle = new Obstacle(3, 2);
        Game game = new Game(3, 2, true, 'N');
        Point collectedCoin = new Point(0, 0);
        List<Point> body = List.of(new Point(1, 0), new Point(2, 0), new Point(0, 1));
        game.incScore();

        int placed = assertTimeoutPreemptively(Duration.ofSeconds(2),
                () -> obstacle.newObstacles(collectedCoin, body, collectedCoin, 10, new Random(42)));
        assertEquals(1, placed);
        assertEquals(1, obstacle.getObstacles().size());
        assertTrue(List.of(new Point(1, 1), new Point(2, 1)).containsAll(obstacle.getObstacles()));
        assertEquals(0, obstacle.newObstacles(collectedCoin, body, collectedCoin, 10, new Random(42)));

        game.decMaxScore(placed);
        game.incScore(); // The Silver coin.
        assertFalse(game.gameWon());
        game.incScore(); // The replacement coin in the last free cell.
        assertTrue(game.gameWon());
    }

    /** Checks that a Silver coin on the final cell wins even when no obstacle fits. */
    @Test
    void fullBoardPlacesNothingAndKeepsTheWinTarget() {
        Obstacle obstacle = new Obstacle(2, 2);
        Game game = new Game(2, 2, true, 'N');
        Point collectedCoin = new Point(0, 0);
        List<Point> body = List.of(new Point(1, 0), new Point(0, 1), new Point(1, 1));

        int placed = assertTimeoutPreemptively(Duration.ofSeconds(2),
                () -> obstacle.newObstacles(collectedCoin, body, collectedCoin, 10, new Random(42)));
        assertEquals(0, placed);
        assertTrue(obstacle.getObstacles().isEmpty());
        game.incScore();
        game.decMaxScore(placed);
        assertFalse(game.gameWon());
        game.incScore();
        assertTrue(game.gameWon());
    }

    /** Checks that a zero-sized or negative request leaves an empty board alone. */
    @Test
    void ignoresNonpositiveRequests() {
        Obstacle obstacle = new Obstacle(4, 4);
        assertEquals(0, obstacle.newObstacles(null, List.of(), null, 0, new Random(42)));
        assertEquals(0, obstacle.newObstacles(null, List.of(), null, -1, new Random(42)));
        assertTrue(obstacle.getObstacles().isEmpty());
    }

    /** Checks that the legacy guard applies before each batch. */
    @Test
    void preservesTheExistingBatchGuard() {
        Obstacle obstacle = new Obstacle(10, 10);
        assertEquals(50, obstacle.newObstacles(null, List.of(), null, 50, new Random(42)));
        assertEquals(2, obstacle.newObstacles(null, List.of(), null, 2, new Random(43)));
        assertEquals(0, obstacle.newObstacles(null, List.of(), null, 4, new Random(44)));
        assertEquals(52, obstacle.getObstacles().size());
    }
}
