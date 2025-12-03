package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    @Test
    void nextBrickMatchesUpcomingGetBrick() {
        RandomBrickGenerator generator = new RandomBrickGenerator();

        Brick preview = generator.getNextBrick();
        Brick actual = generator.getBrick();

        assertSame(preview, actual, "Peeked brick should be the next one returned by getBrick()");
    }

    @Test
    void generatorAlwaysMaintainsUpcomingBrick() {
        RandomBrickGenerator generator = new RandomBrickGenerator();

        for (int i = 0; i < 50; i++) {
            assertNotNull(generator.getNextBrick(), "Generator should always have a preview brick ready");
            Brick brick = generator.getBrick();
            assertNotNull(brick, "getBrick() must never return null");
        }
    }

    @Test
    void producedBricksExposeAtLeastOneShape() {
        RandomBrickGenerator generator = new RandomBrickGenerator();

        Brick brick = generator.getBrick();

        assertFalse(brick.getShapes().isEmpty(), "Tetrominoes must define at least one rotation");
    }
}
