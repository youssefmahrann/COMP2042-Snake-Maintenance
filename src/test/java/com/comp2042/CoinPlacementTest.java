package com.comp2042;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/** Checks coin placement without starting JavaFX. */
class CoinPlacementTest {
    /** A full board should return no position without retrying. */
    @Test
    void returnsEmptyOnAFullBoard() {
        assertEquals(Optional.empty(), Coin.selectPosition(3, 2, new Point(0, 0),
                List.of(new Point(1, 0), new Point(2, 0), new Point(0, 1)),
                List.of(new Point(1, 1), new Point(2, 1)), boundedRandom(42)));
    }

    /** The only available square can be on the edge of a rectangular board. */
    @Test
    void selectsTheOnlyFreeCell() {
        assertEquals(Optional.of(new Point(1, 2)), Coin.selectPosition(2, 3, new Point(0, 0),
                List.of(new Point(1, 0), new Point(0, 1)),
                List.of(new Point(1, 1), new Point(0, 2)), boundedRandom(42)));
    }

    /** Caps retries so the original full-board bug cannot hang Maven. */
    private static Supplier<Random> boundedRandom(long seed) {
        Random random = new Random(seed);
        int[] remaining = {64};
        return () -> {
            assertTrue(remaining[0]-- > 0, "Coin placement exhausted its retry budget");
            return random;
        };
    }
}
