package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class SBrick implements Brick {

    private final List<BrickShape> shapes;

    public SBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 5, 5, 0},
                        {5, 5, 0, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {5, 0, 0, 0},
                        {5, 5, 0, 0},
                        {0, 5, 0, 0},
                        {0, 0, 0, 0}
                })
        ));
    }

    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }
}
