package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;

/**
 * Owns the BrickGenerator and provides a stable preview API for the UI and ActiveBrick.
 * Renamed from BrickPreview to BrickProvider.
 */
public class BrickProvider {

    private final BrickGenerator generator;
    private Brick next;

    public BrickProvider(BrickGenerator generator) {
        this.generator = generator;
        this.next = this.generator.getBrick();
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
    public BrickShape peekNextPreview() {
        return next.getShapes().get(0);
    }
}
