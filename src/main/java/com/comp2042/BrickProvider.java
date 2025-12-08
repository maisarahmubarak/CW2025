package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Owns the BrickGenerator and provides a stable preview API for the UI and ActiveBrick.
 * Renamed from BrickPreview to BrickProvider.
 */
public class BrickProvider {

    private final BrickGenerator generator;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final int PREVIEW_COUNT = 3;

    public BrickProvider(BrickGenerator generator) {
        this.generator = generator;
        // fill preview queue
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            try {
                nextBricks.add(generator.getBrick());
            } catch (RuntimeException ignored) {
                // Some tests supply a finite queue for the generator; ignore exhaustion.
            }
        }
    }

    /**
     * Return and consume the next Brick.
     */
    public Brick consumeNext() {
        Brick current = nextBricks.poll();
        // refill
        try {
            nextBricks.add(generator.getBrick());
        } catch (RuntimeException ignored) {
            // Ignore generator exhaustion in tests
        }
        return current;
    }

    /**
     * Peek the preview shape for UI (first rotation matrix of the next brick).
     */
    public BrickShape peekNextPreview() {
        Brick next = nextBricks.peek();
        return next == null ? null : next.getShapes().get(0);
    }

    public List<BrickShape> peekNextPreviews(int count) {
        List<BrickShape> shapes = new ArrayList<>();
        int i = 0;
        for (Brick b : nextBricks) {
            if (i >= count) break;
            shapes.add(b.getShapes().get(0));
            i++;
        }
        // If not enough in queue, attempt to fill shape list with generators (non-destructive)
        while (shapes.size() < count) {
            Brick b = generator.getNextBrick();
            if (b != null) {
                shapes.add(b.getShapes().get(0));
            } else break;
        }
        return shapes;
    }
}
