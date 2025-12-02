package com.comp2042.logic.bricks;

import java.util.Collections;
import java.util.List;

final class OBrick implements Brick {

    private final List<BrickShape> shapes;

    public OBrick() {
        shapes = Collections.singletonList(BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        }));
    }

    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }

}
