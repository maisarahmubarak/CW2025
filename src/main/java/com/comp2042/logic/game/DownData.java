package com.comp2042.logic.game;

import com.comp2042.logic.board.ClearRow;

/**
 * Data transfer object containing the results of a "move down" operation.
 * Includes information about cleared rows and the visual state of the brick.
 *
 * @author Maisarah
 * @version 1.0
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a new DownData object.
     *
     * @param clearRow information about any rows cleared during the move
     * @param viewData visual data for rendering the brick
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the cleared row information.
     *
     * @return the ClearRow object
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the view data for rendering.
     *
     * @return the ViewData object
     */
    public ViewData getViewData() {
        return viewData;
    }
}
