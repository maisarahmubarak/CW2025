package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

/**
 * Owns the BrickGenerator and provides a stable preview API for the UI and ActiveBrick.
 * Renamed from BrickPreview to BrickProvider.
 */
public class BrickProvider {

    private final BrickGenerator generator;
    private Brick next;

    public BrickProvider() {
        this.generator = new RandomBrickGenerator();
        this.next = generator.getBrick();
    }

    /**
     * Return and consume the next Brick.
     */
    public Brick consumeNext() {
        Brick current = next;
        next = generator.getBrick();
        return current;
    }

    /**
     * Peek the preview shape for UI (first rotation matrix of the next brick).
     */
    public int[][] peekNextPreview() {
        return next.getShapeMatrix().get(0);
    }
}
