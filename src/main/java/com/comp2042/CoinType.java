package com.comp2042;

/** The types of coin that can spawn after a coin is collected. */
enum CoinType {
    REGULAR, GOLD, RAINBOW, BLUE, SILVER;

    static final int ROLL_COUNT = 12;

    /**
     * Selects the next coin type using the current game settings.
     *
     * @param roll the random value from 0 through 11
     * @param powerUpsEnabled whether special coins may spawn
     * @param obstaclesPresent whether the board already contains obstacles
     * @return the type of coin to spawn
     */
    static CoinType select(int roll, boolean powerUpsEnabled, boolean obstaclesPresent) {
        if (!powerUpsEnabled) {
            return REGULAR;
        }
        if (roll == 1) {
            return GOLD;
        }
        if (roll == 2) {
            return RAINBOW;
        }
        if (roll == 3) {
            return BLUE;
        }
        if (roll == 4 && !obstaclesPresent) {
            return SILVER;
        }
        return REGULAR;
    }
}
