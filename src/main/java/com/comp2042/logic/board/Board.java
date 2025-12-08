package com.comp2042.logic.board;

import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.ViewData;

/**
 * Interface defining game board operations.
 * <p>
 * Defines the operations available on the board, such as moving bricks,
 * checking for cleared rows, and managing the game state.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public interface Board {

    /**
     * Attempts to move the active brick down by one unit.
     *
     * @return true if the move was successful, false otherwise (e.g., collision).
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the active brick left by one unit.
     *
     * @return true if the move was successful, false otherwise.
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the active brick right by one unit.
     */
    void moveBrickRight();

    /**
     * Attempts to rotate the active brick.
     *
     * @return true if the rotation was successful, false otherwise.
     */
    boolean rotateLeftBrick();

    /**
     * Spawns a new brick at the top of the board.
     *
     * @return true if the brick was successfully created, false if there is no space (game over).
     */
    boolean createNewBrick();

    /**
     * Retrieves the current state of the board grid.
     *
     * @return a 2D integer array representing the board, where values correspond to brick IDs.
     */
    int[][] getBoardMatrix();

    /**
     * Retrieves the view data for the current state.
     *
     * @return a {@link ViewData} object containing the board matrix and active brick information.
     */
    ViewData getViewData();

    /**
     * Merges the current active brick into the board's background grid.
     * <p>
     * This is called when the brick can no longer move down.
     * </p>
     */
    void mergeBrickToBackground();

    /**
     * Checks for and clears any full rows on the board.
     *
     * @return a {@link ClearRow} object containing information about cleared rows and score bonuses.
     */
    ClearRow clearRows();

    /**
     * Retrieves the game score object.
     *
     * @return the {@link GameScore} associated with this board.
     */
    GameScore getScore();

    /**
     * Resets the board for a new game.
     */
    void newGame();

    /**
     * Adds garbage lines (randomly filled rows) to the bottom of the board.
     *
     * @param lines the number of garbage lines to add.
     */
    void addGarbageLines(int lines);
}