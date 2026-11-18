package com.comp2042;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** Checks coin placement, final scoring, and restart state without starting JavaFX. */
class FullBoardTest {
    /** Normal play records the points before requesting a replacement. */
    @Test
    void continuesWhileAReplacementCanSpawn() {
        Game game = new Game(5, 3, true, 'N');

        game.recordCollection(5, () -> {
            assertEquals(5, game.getScorePoints());
            assertFalse(game.gameWon());
            return true;
        });

        assertFalse(game.gameWon());
        assertEquals(5, game.getScorePoints());
    }

    /** A full board records the final points once and never requests another coin. */
    @Test
    void stopsRecordingAfterFullBoardWin() {
        Game game = new Game(3, 2, true, 'N');

        game.recordCollection(10, () -> Coin.selectPosition(3, 2, new Point(0, 0),
                List.of(new Point(1, 0), new Point(2, 0), new Point(0, 1)),
                List.of(new Point(1, 1), new Point(2, 1)),
                () -> fail("A full board must not draw a random value")).isPresent());
        game.recordCollection(10, () -> fail("A won game must not request another coin"));

        assertTrue(game.gameWon());
        assertEquals(10, game.getScorePoints());
    }

    /** The original win target also stops replacement spawning. */
    @Test
    void skipsReplacementAfterTheWinningCollection() {
        Game game = new Game(2, 2, false, 'N');
        game.recordCollection(5, () -> true);
        game.recordCollection(5, () -> fail("The winning collection must not spawn another coin"));

        assertTrue(game.gameWon());
        assertEquals(10, game.getScorePoints());
    }

    /** Restart clears a capacity-based win and restores the target before the next round. */
    @Test
    void restartClearsTheWinAndRestoresTheOriginalTarget() {
        Game game = new Game(3, 2, true, 'N');
        game.decMaxScore(2);
        game.recordCollection(5, () -> false);
        assertTrue(game.gameWon());

        game.resetScore();
        assertFalse(game.gameWon());
        assertEquals(0, game.getScorePoints());
        for (int count = 0; count < 3; count++) {
            game.recordCollection(5, () -> true);
            assertFalse(game.gameWon());
        }
        game.recordCollection(5, () -> fail("The restored target has been reached"));
        assertTrue(game.gameWon());
        assertEquals(20, game.getScorePoints());
    }

    /** Every random choice maps to a distinct legal cell on a rectangular board. */
    @Test
    void offersEachFreeCellExactlyOnce() {
        Point head = new Point(1, 0);
        List<Point> body = List.of(new Point(2, 0));
        List<Point> obstacles = List.of(new Point(0, 1));
        List<Point> free = List.of(new Point(0, 0), new Point(3, 0),
                new Point(1, 1), new Point(2, 1), new Point(3, 1));

        for (int index = 0; index < free.size(); index++) {
            int choice = index;
            Random random = new Random() {
                @Override
                public int nextInt(int bound) {
                    assertEquals(free.size(), bound);
                    return choice;
                }
            };
            assertEquals(Optional.of(free.get(index)),
                    Coin.selectPosition(4, 2, head, body, obstacles, () -> random));
        }
    }
}
