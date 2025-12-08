package com.comp2042.logic.board;

import com.comp2042.BrickThemeFactory;
import com.comp2042.ClassicBrickFactory;
import com.comp2042.logic.game.ActiveBrick;
import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.ViewData;

/**
 * A concrete implementation of the {@link Board} interface.
 * <p>
 * Manages the game state, including the grid (matrix), the active brick, and the score.
 * </p>
 */
public class SimpleBoard implements Board {

    private final ActiveBrick activeBrick;
    private final BoardMatrix boardMatrix;
    private final GameScore score;
    private boolean gameOver;

    /**
     * Constructs a new SimpleBoard with the specified dimensions and default theme.
     *
     * @param width  the width of the board in blocks.
     * @param height the height of the board in blocks.
     */
    public SimpleBoard(int width, int height) {
        this(width, height, new ClassicBrickFactory());
    }

    /**
     * Constructs a new SimpleBoard with the specified dimensions and theme factory.
     *
     * @param width             the width of the board.
     * @param height            the height of the board.
     * @param brickThemeFactory the factory to use for creating bricks.
     */
    public SimpleBoard(int width, int height, BrickThemeFactory brickThemeFactory) {
        boardMatrix = new BoardMatrix(width, height);
        activeBrick = new ActiveBrick(brickThemeFactory.createGenerator());
        score = new GameScore();
        gameOver = false;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public boolean moveBrickDown() {
        return activeBrick.moveDown(boardMatrix.getBoardMatrix());
    }

    @Override
    public boolean moveBrickLeft() {
        return activeBrick.moveLeft(boardMatrix.getBoardMatrix());
    }

    @Override
    public void moveBrickRight() {
        activeBrick.moveRight(boardMatrix.getBoardMatrix());
    }

    @Override
    public boolean rotateLeftBrick() {
        return activeBrick.rotateLeft(boardMatrix.getBoardMatrix());
    }

    @Override
    public boolean createNewBrick() {
        boolean collision = activeBrick.createNewBrick(boardMatrix.getBoardMatrix());
        if (collision) {
            gameOver = true;
        }
        return collision;
    }

    @Override
    public int[][] getBoardMatrix() {
        return boardMatrix.getBoardMatrix();
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(activeBrick.getCurrentShape(), activeBrick.getOffsetX(), activeBrick.getOffsetY(), activeBrick.getNextPreviews(3));
    }

    @Override
    public void mergeBrickToBackground() {
        boardMatrix.merge(activeBrick.getCurrentShape(), activeBrick.getOffsetX(), activeBrick.getOffsetY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = boardMatrix.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            score.add(clearRow.getScoreBonus());
            score.add(clearRow.getLinesRemoved());
        }
        return clearRow;
    }

    @Override
    public GameScore getScore() {
        return score;
    }

    @Override
    public void newGame() {
        boardMatrix.reset();
        score.reset();
        gameOver = false;
        createNewBrick();
    }

    @Override
    public void addGarbageLines(int lines) {
        boardMatrix.addGarbageLines(lines);
    }
}