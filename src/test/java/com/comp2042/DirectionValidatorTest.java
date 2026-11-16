
package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionValidatorTest {

    @Test
    void shouldRejectDownWhenMovingUp() {
        assertFalse(DirectionValidator.isValid(
            KeyCode.UP, KeyCode.DOWN));
    }

    @Test
    void shouldRejectUpWhenMovingDown() {
        assertFalse(DirectionValidator.isValid(
            KeyCode.DOWN, KeyCode.UP));
    }

    @Test
    void shouldRejectLeftWhenMovingRight() {
        assertFalse(DirectionValidator.isValid(
            KeyCode.RIGHT, KeyCode.LEFT));
    }

    @Test
    void shouldRejectRightWhenMovingLeft() {
        assertFalse(DirectionValidator.isValid(
            KeyCode.LEFT, KeyCode.RIGHT));
    }

    @Test
    void shouldAllowDownWhenMovingRight() {
        assertTrue(DirectionValidator.isValid(
            KeyCode.RIGHT, KeyCode.DOWN));
    }

    @Test
    void shouldRejectNonArrowKeys() {
        assertFalse(DirectionValidator.isValid(
            KeyCode.LEFT, KeyCode.SPACE));
    }

    @Test
    void shouldRejectNullDirection() {
        assertFalse(DirectionValidator.isValid(
            null, KeyCode.LEFT));
    }
}
