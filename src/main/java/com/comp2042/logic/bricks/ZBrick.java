package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ZBrick implements Brick {

    private final List<BrickShape> shapes;

    public ZBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {7, 7, 0, 0},
                        {0, 7, 7, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 7, 0, 0},
                        {7, 7, 0, 0},
                        {7, 0, 0, 0},
                        {0, 0, 0, 0}
                })
        ));
    }

    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }
}
