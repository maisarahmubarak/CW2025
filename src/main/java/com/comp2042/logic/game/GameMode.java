package com.comp2042.logic.game;

/**
 * Defines the available game modes and their specific configuration parameters.
 */
public enum GameMode {
    /**
     * Standard game mode with normal speed and no garbage rows.
     */
    CLASSIC(400, 0), 
    
    /**
     * Fast-paced mode with reduced drop interval.
     */
    SPEED(200, 0), 
    
    /**
     * Challenging mode with normal speed but garbage rows added.
     */
    DANGER(400, 0);

    private final int dropIntervalMs;
    private final int garbageRows;

    GameMode(int dropIntervalMs, int garbageRows) {
        this.dropIntervalMs = dropIntervalMs;
        this.garbageRows = garbageRows;
    }

    /**
     * Gets the drop interval in milliseconds for this mode.
     *
     * @return the drop interval in ms
     */
    public int getDropIntervalMs() {
        return dropIntervalMs;
    }

    /**
     * Gets the number of garbage rows to add in this mode.
     *
     * @return the number of garbage rows
     */
    public int getGarbageRows() {
        return garbageRows;
    }
}