package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finds playable positions that are not occupied. */
final class FreeCells {
    private FreeCells() {
    }

    /** Lists free cells in row order using zero-based board coordinates. */
    static List<Point> onBoard(int width, int height, Collection<Point> occupied) {
        Set<Point> blocked = new HashSet<>(occupied);
        List<Point> free = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                Point cell = new Point(column, row);
                if (!blocked.contains(cell)) {
                    free.add(cell);
                }
            }
        }
        return free;
    }
}
