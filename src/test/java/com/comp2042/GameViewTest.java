package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Checks presentation calculations without starting the JavaFX toolkit. */
class GameViewTest {
    @ParameterizedTest
    @CsvSource({"800, 400, 1280, 960, 1.6", "400, 800, 1280, 960, 1.2",
            "1000, 800, 500, 600, 0.5", "400, 600, 400, 600, 1"})
    void fitsBothDimensionsWithTheSameScale(
            double width, double height, double viewportWidth, double viewportHeight, double expected) {
        assertEquals(expected, GameView.fitScale(width, height, viewportWidth, viewportHeight), 1e-9);
    }

    @Test
    void retainsWindowedCellSizes() {
        assertEquals(20, GameView.baseCellSize(20));
        assertEquals(20, GameView.baseCellSize(50));
        assertEquals(10, GameView.baseCellSize(51));
    }

    @ParameterizedTest
    @CsvSource({"REGULAR, redcoin.png", "GOLD, goldcoin.png", "RAINBOW, rainbowcoin.png",
            "BLUE, bluecoin.png", "SILVER, silvercoin.png"})
    void mapsCoinTypesToPackagedSprites(CoinType type, String asset) {
        assertEquals(asset, GameView.coinSpriteName(type));
        assertNotNull(GameView.class.getResource("/resources/views/res/" + asset));
    }

    @ParameterizedTest
    @CsvSource({"LEFT, UP, 2", "LEFT, DOWN, 3", "RIGHT, UP, 1", "RIGHT, DOWN, 4",
            "UP, LEFT, 4", "UP, RIGHT, 3", "DOWN, LEFT, 1", "DOWN, RIGHT, 2"})
    void preservesEveryTurnSpriteRotation(KeyCode previous, KeyCode next, int quarterTurns) {
        assertEquals(quarterTurns, GameView.turnQuarterTurns(previous, next));
    }

    @Test
    void straightMovementDoesNotLeaveATurnMarker() {
        assertEquals(0, GameView.turnQuarterTurns(KeyCode.LEFT, KeyCode.LEFT));
    }

    @ParameterizedTest
    @CsvSource({"2, 1, 0", "3, 2, 90", "2, 3, 180", "1, 2, 270"})
    void preservesTailOrientation(int x, int y, int angle) {
        assertEquals(angle, GameView.tailAngle(new Point(2, 2), new Point(x, y)));
    }

    @Test
    void packsTheSnakeSpritesStylesheetAndMenu() {
        for (String asset : new String[]{"snakehead.png", "snakebody.png", "snaketail.png", "snaketurnleft.png"}) {
            assertNotNull(GameView.class.getResource("/resources/views/res/" + asset));
        }
        assertNotNull(GameView.class.getResource("/resources/views/styling.css"));
        assertNotNull(MainMenuController.class.getResource("/resources/views/MainMenu.fxml"));
    }
}
