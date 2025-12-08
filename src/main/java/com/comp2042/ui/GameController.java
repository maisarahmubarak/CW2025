package com.comp2042.ui;

import com.comp2042.BrickThemeFactory;
import com.comp2042.ClassicBrickFactory;
import com.comp2042.input.ActionSource;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
import com.comp2042.logic.board.Board;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.board.MatrixOperations;
import com.comp2042.logic.board.SimpleBoard;
import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.ViewData;
import javafx.util.Duration;

/**
 * The controller class that manages the game logic and interaction between the model (Board) and the view (GuiController).
 * <p>
 * This class implements {@link InputActionListener} to handle user input events such as moving and rotating bricks.
 * It initializes the game board, handles game state updates, manages scoring, and triggers UI updates.
 * </p>
 */
public class GameController implements InputActionListener {

    private Board board;

    private final GuiController viewGuiController;

    private final BrickThemeFactory brickThemeFactory;
    private final GameMode gameMode;

    /**
     * Constructs a new GameController with the default Classic game mode.
     *
     * @param c the {@link GuiController} responsible for the game's user interface.
     */
    public GameController(GuiController c) {
        this(c, GameMode.CLASSIC);
    }

    /**
     * Constructs a new GameController with a specified game mode.
     * <p>
     * This constructor initializes the game board, sets up the brick theme, binds the score to the UI,
     * and configures game mode specific settings such as garbage rows and speed adjustments.
     * </p>
     *
     * @param c    the {@link GuiController} responsible for the game's user interface.
     * @param mode the {@link GameMode} to be used for this game session.
     */
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

    /**
     * Handles the "down" movement event for the current brick.
     * <p>
     * This method attempts to move the brick down. If the brick cannot move further, it merges the brick
     * into the board, checks for cleared rows, updates the score, and spawns a new brick.
     * If the game is over (cannot spawn a new brick), it triggers the game over sequence.
     * </p>
     *
     * @param event the {@link MoveAction} event containing details about the move.
     * @return a {@link DownData} object containing information about cleared rows and the current view data.
     */
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

    /**
     * Handles the "left" movement event for the current brick.
     *
     * @param event the {@link MoveAction} event.
     * @return the updated {@link ViewData} reflecting the new position of the brick.
     */
    @Override
    public ViewData onLeftEvent(MoveAction event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the "right" movement event for the current brick.
     *
     * @param event the {@link MoveAction} event.
     * @return the updated {@link ViewData} reflecting the new position of the brick.
     */
    @Override
    public ViewData onRightEvent(MoveAction event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the "rotate" movement event for the current brick.
     *
     * @param event the {@link MoveAction} event.
     * @return the updated {@link ViewData} reflecting the new rotation of the brick.
     */
    @Override
    public ViewData onRotateEvent(MoveAction event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    /**
     * Starts a new game session.
     * <p>
     * Resets the board and refreshes the game background.
     * </p>
     */
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
