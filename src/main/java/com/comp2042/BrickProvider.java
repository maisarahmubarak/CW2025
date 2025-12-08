package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Manages the generation and preview of bricks in the game.
 * <p>
 * This class owns a {@link BrickGenerator} and maintains a queue of upcoming bricks,
 * providing a stable API for the UI to display previews and for the game logic to consume new bricks.
 * </p>
 */
public class BrickProvider {

    private final BrickGenerator generator;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final int PREVIEW_COUNT = 3;

    /**
     * Constructs a new BrickProvider with the specified generator.
     * <p>
     * Initializes the preview queue with a set number of bricks.
     * </p>
     *
     * @param generator the {@link BrickGenerator} used to create new bricks.
     */
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
     * Retrieves and removes the next brick from the queue.
     * <p>
     * This method also refills the queue to ensure a constant number of preview bricks are available.
     * </p>
     *
     * @return the next {@link Brick} to be played.
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
     * Peeks at the shape of the next brick in the queue without removing it.
     * <p>
     * This is typically used for displaying the "Next Brick" preview in the UI.
     * </p>
     *
     * @return the {@link BrickShape} of the next brick (first rotation state).
     */
    public BrickShape peekNextPreview() {
        Brick next = nextBricks.peek();
        return next == null ? null : next.getShapes().get(0);
    }

    /**
     * Peeks at the shapes of the next few bricks in the queue.
     *
     * @param count the number of upcoming bricks to preview
     * @return a list of {@link BrickShape} objects for the next bricks
     */
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
