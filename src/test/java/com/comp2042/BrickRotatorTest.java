package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    @Test
    void getNextShapeCyclesAndWraps() {
        BrickShape shapeA = BrickShape.fromMatrix(new int[][]{{1}});
        BrickShape shapeB = BrickShape.fromMatrix(new int[][]{{2}});
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(new TestBrick(shapeA, shapeB));

        NextShapeInfo next = rotator.getNextShape();
        assertSame(shapeB, next.getShape());
        assertEquals(1, next.getPosition());

        rotator.setCurrentShape(next.getPosition());
        NextShapeInfo wrapped = rotator.getNextShape();
        assertSame(shapeA, wrapped.getShape(), "After last rotation the preview should wrap to the first shape");
        assertEquals(0, wrapped.getPosition());
    }

    @Test
    void setBrickResetsCurrentShapeIndex() {
        BrickShape shapeA = BrickShape.fromMatrix(new int[][]{{1}});
        BrickShape shapeB = BrickShape.fromMatrix(new int[][]{{2}});
        BrickShape shapeC = BrickShape.fromMatrix(new int[][]{{3}});
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(new TestBrick(shapeA, shapeB));
        rotator.setCurrentShape(1);

        rotator.setBrick(new TestBrick(shapeC));

        assertSame(shapeC, rotator.getCurrentShape());
    }

    @Test
    void getCurrentShapeReturnsActiveRotation() {
        BrickShape shapeA = BrickShape.fromMatrix(new int[][]{{4}});
        BrickShape shapeB = BrickShape.fromMatrix(new int[][]{{5}});
        BrickRotator rotator = new BrickRotator();
        rotator.setBrick(new TestBrick(shapeA, shapeB));
        rotator.setCurrentShape(1);

        assertSame(shapeB, rotator.getCurrentShape());
    }

    private static final class TestBrick implements Brick {
        private final List<BrickShape> shapes;

        private TestBrick(BrickShape... shapes) {
            this.shapes = List.copyOf(Arrays.asList(shapes));
        }

        @Override
        public List<BrickShape> getShapes() {
            return shapes;
        }
    }
}
