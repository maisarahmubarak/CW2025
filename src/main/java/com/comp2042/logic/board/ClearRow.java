package com.comp2042.logic.board;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final int[] clearedRows;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, new int[0]);
    }

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, int[] clearedRows) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.clearedRows = (clearedRows == null) ? new int[0] : java.util.Arrays.copyOf(clearedRows, clearedRows.length);
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public int[] getClearedRows() {
        return java.util.Arrays.copyOf(clearedRows, clearedRows.length);
    }
}