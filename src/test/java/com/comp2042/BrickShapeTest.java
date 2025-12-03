package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BrickShapeTest {

    @Test
    void fromMatrixCapturesDimensions() {
        int[][] matrix = {
                {1, 0, 1},
                {0, 0, 0}
        };

        BrickShape shape = BrickShape.fromMatrix(matrix);

        assertEquals(3, shape.getWidth());
        assertEquals(2, shape.getHeight());
    }

    @Test
    void forEachCellVisitsOnlyNonZeroCells() {
        int[][] matrix = {
                {1, 0},
                {0, 2}
        };
        BrickShape shape = BrickShape.fromMatrix(matrix);
        AtomicInteger visitCount = new AtomicInteger();

        shape.forEachCell((x, y, value) -> {
            visitCount.incrementAndGet();
            assertTrue(value != 0, "Only non-zero cells should be visited");
        });

        assertEquals(2, visitCount.get());
    }

    @Test
    void toMatrixRoundTripsOriginalLayout() {
        int[][] matrix = {
                {0, 3},
                {4, 0},
                {0, 5}
        };
        BrickShape shape = BrickShape.fromMatrix(matrix);

        assertArrayEquals(matrix, shape.toMatrix());
    }

    @Test
    void toMatrixProvidesIndependentCopy() {
        int[][] matrix = {
                {1, 2},
                {3, 4}
        };
        BrickShape shape = BrickShape.fromMatrix(matrix);

        int[][] firstCall = shape.toMatrix();
        firstCall[0][0] = 99;

        assertEquals(1, shape.toMatrix()[0][0], "Mutating returned matrix must not change the stored shape");
    }
}
