package com.comp2042.logic.game;

import com.comp2042.BrickProvider;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.board.MatrixOperations;
import com.comp2042.ClassicBrickFactory;
import com.comp2042.BrickRotator;
import java.awt.Point;
import java.util.List;

/**
 * Manages the state and movement of the currently falling brick in the game.
 * Handles movement (down, left, right), rotation, and collision detection with the board.
 */
public class ActiveBrick {

    private final BrickRotator brickRotator;
    private final BrickProvider preview;
    private static final int HIDDEN_ROWS = 1;
    private Point currentOffset;

    /**
     * Creates a new ActiveBrick with a specific generator.
     *
     * @param generator the generator to use for creating new bricks
     */
    public ActiveBrick(BrickGenerator generator) {
        this.preview = new BrickProvider(generator);
        this.brickRotator = new BrickRotator();
        this.currentOffset = new Point(0, 0);
    }

    /**
     * Creates a new ActiveBrick using the default ClassicBrickFactory.
     */
    public ActiveBrick() {
    /**
     * Attempts to move the brick down by one row.
     *
     * @param boardMatrix the current state of the game board
     * @return true if the move was successful, false if blocked by collision
     */
    public boolean moveDown(int[][] boardMatrix) {ator());
    }

    public boolean moveDown(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
    /**
     * Attempts to move the brick left by one column.
     *
     * @param boardMatrix the current state of the game board
     * @return true if the move was successful, false if blocked by collision
     */
    public boolean moveLeft(int[][] boardMatrix) {
            currentOffset = p;
            return true;
        }
    }

    public boolean moveLeft(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
    /**
     * Attempts to move the brick right by one column.
     *
     * @param boardMatrix the current state of the game board
     * @return true if the move was successful, false if blocked by collision
     */
    public boolean moveRight(int[][] boardMatrix) {
            currentOffset = p;
            return true;
        }
    }

    public boolean moveRight(int[][] boardMatrix) {
    /**
     * Attempts to rotate the brick to the left (counter-clockwise).
     * Tries wall kicks (shifting horizontally) if the rotation is initially blocked.
     *
     * @param boardMatrix the current state of the game board
     * @return true if the rotation was successful, false if no valid position was found
     */
    public boolean rotateLeft(int[][] boardMatrix) {y(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    public boolean rotateLeft(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
    /**
     * Spawns a new brick at the top center of the board.
     *
     * @param boardMatrix the current state of the game board
     * @return true if the new brick immediately collides (game over condition), false otherwise
     */
    public boolean createNewBrick(int[][] boardMatrix) {
        for (int offset : horizontalOffsets) {
            Point candidate = new Point(currentOffset);
            candidate.translate(offset, 0);
    /**
     * Gets the current shape of the active brick.
     *
     * @return the current BrickShape
     */
    public BrickShape getCurrentShape() {ct(currentMatrix, nextShape.getShape(), (int) candidate.getX(), (int) candidate.getY())) {
                currentOffset = candidate;
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }
        return false;
    }

    public boolean createNewBrick(int[][] boardMatrix) {
        Brick currentBrick = preview.consumeNext();
        brickRotator.setBrick(currentBrick);
        int boardWidth = boardMatrix[0].length;
    /**
     * Gets the next preview shape.
     *
     * @return the BrickShape of the next brick
     */
    /**
     * Gets the current X offset (column) of the brick.
     *
     * @return the x coordinate
    /**
     * Gets the current Y offset (row) of the brick.
     *
     * @return the y coordinate
     */
    public int getOffsetY() {
    public int getOffsetX() {Preview() {tCurrentShape().getWidth();
        int spawnX = (boardWidth - brickWidth) / 2;
        currentOffset = new Point(spawnX, HIDDEN_ROWS);
        return MatrixOperations.intersect(boardMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Gets a list of upcoming preview shapes.
     *
     * @param count the number of previews to retrieve
     * @return a list of BrickShapes
     */
    public List<BrickShape> getNextPreviews(int count) {
        return brickRotator.getCurrentShape();
    }

    public int getOffsetX() {
        return (int) currentOffset.getX();
    }

    public int getOffsetY() {
        return (int) currentOffset.getY();
    }

    public BrickShape getNextPreview() {
        return preview.peekNextPreview();
    }

    public List<BrickShape> getNextPreviews(int count) {
        return preview.peekNextPreviews(count);
    }
}