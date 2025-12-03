package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class IBrick implements Brick {

    private final List<BrickShape> shapes;

    public IBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }),
                BrickShape.fromMatrix(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        })
        ));
    }

    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }

}
