package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrickProviderTest {

    @Test
    void consumeNextAdvancesPreviewQueue() {
        Brick first = TestBrick.ofValue(1);
        Brick second = TestBrick.ofValue(2);
        BrickGenerator generator = new QueueBrickGenerator(first, second);
        BrickProvider provider = new BrickProvider(generator);

        assertEquals(1, provider.peekNextPreview().toMatrix()[0][0]);

        Brick consumed = provider.consumeNext();

        assertSame(first, consumed);
        assertEquals(2, provider.peekNextPreview().toMatrix()[0][0]);
    }

    @Test
    void peekDoesNotConsumeNextBrick() {
        Brick first = TestBrick.ofValue(7);
        Brick second = TestBrick.ofValue(8);
        BrickGenerator generator = new QueueBrickGenerator(first, second);
        BrickProvider provider = new BrickProvider(generator);

        BrickShape peek1 = provider.peekNextPreview();
        BrickShape peek2 = provider.peekNextPreview();

        assertSame(peek1, peek2, "Repeated peeks must be stable until consumeNext() is called");
    }

    private static final class QueueBrickGenerator implements BrickGenerator {
        private final Deque<Brick> queue;

        private QueueBrickGenerator(Brick... bricks) {
            this.queue = new ArrayDeque<>(Arrays.asList(bricks));
        }

        @Override
        public Brick getBrick() {
            Brick brick = queue.pollFirst();
            if (brick == null) {
                throw new IllegalStateException("Exhausted brick generator queue");
            }
            return brick;
        }

        @Override
        public Brick getNextBrick() {
            return queue.peekFirst();
        }
    }

    private static final class TestBrick implements Brick {
        private final List<BrickShape> shapes;

        private TestBrick(List<BrickShape> shapes) {
            this.shapes = shapes;
        }

        static Brick ofValue(int value) {
            BrickShape shape = BrickShape.fromMatrix(new int[][]{{value}});
            return new TestBrick(List.of(shape));
        }

        @Override
        public List<BrickShape> getShapes() {
            return shapes;
        }
    }
}
