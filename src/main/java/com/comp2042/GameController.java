package com.comp2042;

import com.comp2042.logic.board.Board;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.board.MatrixOperations;
import com.comp2042.logic.board.SimpleBoard;
import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.ViewData;
import javafx.util.Duration;

public class GameController implements InputActionListener {

    private Board board;

    private final GuiController viewGuiController;

    private final BrickThemeFactory brickThemeFactory;
    private final GameMode gameMode;

    public GameController(GuiController c) {
        this(c, GameMode.CLASSIC);
    }

    public GameController(GuiController c, GameMode mode) {
        this.gameMode = mode == null ? GameMode.CLASSIC : mode;
        viewGuiController = c;
        this.brickThemeFactory = new ClassicBrickFactory();
        // Board: 26 visible rows + 2 hidden rows = 28 total; 10 columns
        // Container: 520px ÷ 20px/brick = 26 visible rows exactly
        this.board = new SimpleBoard(28, 10, brickThemeFactory);
        viewGuiController.setColorPalette(brickThemeFactory.createPalette());
        // bind the GameScore to the GUI so it can display current score
        viewGuiController.bindToScore(board.getScore());
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        // Apply mode-specific initial board state
        if (gameMode.getGarbageRows() > 0) {
            board.addGarbageLines(gameMode.getGarbageRows());
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }

        if (gameMode == GameMode.SPEED) {
            board.getScore().scoreProperty().addListener((obs, oldVal, newVal) -> {
                int oldLevel = oldVal.intValue() / 100;
                int newLevel = newVal.intValue() / 100;
                if (newLevel > oldLevel) {
                    // Use the mode's base drop interval rather than a hard-coded 400ms so
                    // SPEED mode remains based on its configured base drop interval.
                    double baseMs = gameMode.getDropIntervalMs();
                    double newMillis = baseMs * Math.pow(0.9, newLevel);
                    if (newMillis < 50) newMillis = 50;
                    viewGuiController.setGameSpeed(Duration.millis(newMillis));
                }
            });
        }
    }

    @Override
    public DownData onDownEvent(MoveAction event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            int[][] previousMatrix = MatrixOperations.copy(board.getBoardMatrix());
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                try {
                    System.out.println("GameController: detected clearedRows: " + java.util.Arrays.toString(clearRow.getClearedRows()));
                } catch (Exception ignored) {
                }
                // Trigger visual animation showing cleared row blocks fall and vanish
                try {
                    viewGuiController.animateClearedRows(previousMatrix, clearRow);
                } catch (Exception ignored) {
                }
                // Add configured bonus score for rows cleared
                board.getScore().add(clearRow.getScoreBonus());
                // Also increment the score per row cleared to make it apparent on each clear
                board.getScore().add(clearRow.getLinesRemoved());
            }
            if (board.createNewBrick()) {
                // show final score on the Game Over screen
                try {
                    int finalScore = board.getScore().scoreProperty().get();
                    viewGuiController.setFinalScore(finalScore);
                } catch (Exception ignored) {
                }
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == ActionSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveAction event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveAction event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveAction event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
