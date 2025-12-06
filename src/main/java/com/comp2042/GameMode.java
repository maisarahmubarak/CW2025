package com.comp2042;

public enum GameMode {
    CLASSIC(400, 0), SPEED(200, 0), DANGER(400, 0);

    private final int dropIntervalMs;
    private final int garbageRows;

    GameMode(int dropIntervalMs, int garbageRows) {
        this.dropIntervalMs = dropIntervalMs;
        this.garbageRows = garbageRows;
    }

    public int getDropIntervalMs() {
        return dropIntervalMs;
    }

    public int getGarbageRows() {
        return garbageRows;
    }
}
