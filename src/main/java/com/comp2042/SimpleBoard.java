package com.comp2042;

// imports reduced after SRP refactor

public class SimpleBoard implements Board {

    private final ActiveBrick activeBrick;
    private final BoardMatrix boardMatrix;
    private final GameScore score;

    public SimpleBoard(int width, int height) {
        this(width, height, new ClassicBrickFactory());
    }

    public SimpleBoard(int width, int height, BrickThemeFactory brickThemeFactory) {
        boardMatrix = new BoardMatrix(width, height);
        activeBrick = new ActiveBrick(brickThemeFactory.createGenerator());
        score = new GameScore();
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
    public boolean moveBrickRight() {
        return activeBrick.moveRight(boardMatrix.getBoardMatrix());
    }

    @Override
    public boolean rotateLeftBrick() {
        return activeBrick.rotateLeft(boardMatrix.getBoardMatrix());
    }

    @Override
    public boolean createNewBrick() {
        return activeBrick.createNewBrick(boardMatrix.getBoardMatrix());
    }

    @Override
    public int[][] getBoardMatrix() {
        return boardMatrix.getBoardMatrix();
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(activeBrick.getCurrentShape(), activeBrick.getOffsetX(), activeBrick.getOffsetY(), activeBrick.getNextPreview());
    }

    @Override
    public void mergeBrickToBackground() {
        boardMatrix.merge(activeBrick.getCurrentShape(), activeBrick.getOffsetX(), activeBrick.getOffsetY());
    }

    @Override
    public ClearRow clearRows() {
        return boardMatrix.clearRows();

    }

    @Override
    public GameScore getScore() {
        return score;
    }


    @Override
    public void newGame() {
        boardMatrix.reset();
        score.reset();
        createNewBrick();
    }

    @Override
    public void addGarbageLines(int lines) {
        boardMatrix.addGarbageLines(lines);
    }
}
