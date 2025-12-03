package com.comp2042.logic.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite that groups the occupied cells of a brick rotation. Keeps width and
 * height metadata so renderers can build overlays while callers iterate only over
 * meaningful cells.
 */
public final class BrickShape implements BrickComponent {

    private final List<BrickComponent> children;
    private final int width;
    private final int height;

    private BrickShape(List<BrickComponent> children, int width, int height) {
        this.children = Collections.unmodifiableList(children);
        this.width = width;
        this.height = height;
    }

    public static BrickShape fromMatrix(int[][] matrix) {
        List<BrickComponent> children = new ArrayList<>();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] != 0) {
                    children.add(new BrickCell(x, y, matrix[y][x]));
                }
            }
        }
        int width = matrix.length == 0 ? 0 : matrix[0].length;
        int height = matrix.length;
        return new BrickShape(children, width, height);
    }

    @Override
    public void forEachCell(CellConsumer consumer) {
        for (BrickComponent child : children) {
            child.forEachCell(consumer);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Convenience helper for views that still expect a dense matrix. Keeps
     * responsibility localized to the view layer.
     */
    public int[][] toMatrix() {
        int[][] matrix = new int[height][width];
        forEachCell((x, y, value) -> matrix[y][x] = value);
        return matrix;
    }
}
