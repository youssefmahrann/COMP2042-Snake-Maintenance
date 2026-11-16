
package com.comp2042;

import javafx.scene.input.KeyCode;

public class DirectionValidator {

    public static boolean isValid(KeyCode current, KeyCode requested) {
        if (requested == null || !requested.isArrowKey()) {
            return false;
        }

        if (current == null || !current.isArrowKey()) {
            return false;
        }

        if (requested == KeyCode.LEFT) {
            return current != KeyCode.RIGHT;
        }

        if (requested == KeyCode.RIGHT) {
            return current != KeyCode.LEFT;
        }

        if (requested == KeyCode.DOWN) {
            return current != KeyCode.UP;
        }

        if (requested == KeyCode.UP) {
            return current != KeyCode.DOWN;
        }

        return false;
    }
}
