package com.comp2042;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Checks every coin roll with and without power-ups and obstacles. */
class CoinTypeTest {
    /** Checks the coin selected for each roll with power-ups enabled. */
    @ParameterizedTest
    @CsvSource({
        "0, REGULAR, REGULAR",
        "1, GOLD, GOLD",
        "2, RAINBOW, RAINBOW",
        "3, BLUE, BLUE",
        "4, SILVER, REGULAR",
        "5, REGULAR, REGULAR",
        "6, REGULAR, REGULAR",
        "7, REGULAR, REGULAR",
        "8, REGULAR, REGULAR",
        "9, REGULAR, REGULAR",
        "10, REGULAR, REGULAR",
        "11, REGULAR, REGULAR"
    })
    void selectsCoinWithPowerUps(int roll, CoinType withoutObstacles, CoinType withObstacles) {
        assertEquals(withoutObstacles, CoinType.select(roll, true, false), "Without obstacles");
        assertEquals(withObstacles, CoinType.select(roll, true, true), "With obstacles");
    }

    /** Checks that both board states return a regular coin when power-ups are disabled. */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void selectsRegularCoinWithoutPowerUps(int roll) {
        assertEquals(CoinType.REGULAR, CoinType.select(roll, false, false), "Without obstacles");
        assertEquals(CoinType.REGULAR, CoinType.select(roll, false, true), "With obstacles");
    }
}
