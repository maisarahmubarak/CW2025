package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class LBrick implements Brick {

    private final List<BrickShape> shapes;

    public LBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 3},
                        {0, 3, 0, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 0},
                        {0, 0, 3, 0},
                        {0, 0, 3, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 0, 3, 0},
                        {3, 3, 3, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 3, 0, 0},
                        {0, 3, 0, 0},
                        {0, 3, 3, 0},
                        {0, 0, 0, 0}
                })
        ));
    }

    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }
}
