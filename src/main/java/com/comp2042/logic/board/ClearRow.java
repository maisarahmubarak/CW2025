package com.comp2042.logic.board;

/**
 * Represents the result of a row clearing operation.
 * Contains the number of lines removed, the updated board matrix, score bonus, and indices of cleared rows.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final int[] clearedRows;

    /**
     * Creates a ClearRow result without specific row indices.
     *
     * @param linesRemoved the number of lines cleared
    /**
     * Creates a ClearRow result with full details.
     *
     * @param linesRemoved the number of lines cleared
     * @param newMatrix the new state of the board matrix
     * @param scoreBonus the score bonus awarded
     * @param clearedRows array of row indices that were cleared
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, int[] clearedRows) {
     * @param scoreBonus the score bonus awarded
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, new int[0]);
    }
    /**
     * Gets the number of lines removed.
     *
     * @return the count of removed lines
     */
    public int getLinesRemoved() {
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, int[] clearedRows) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.clearedRows = (clearedRows == null) ? new int[0] : java.util.Arrays.copyOf(clearedRows, clearedRows.length);
    }
    /**
     * Gets the updated board matrix after clearing rows.
     *
     * @return the new 2D integer array representing the board
     */
    public int[][] getNewMatrix() {
    public int getLinesRemoved() {
        return linesRemoved;
    }
    /**
     * Gets the score bonus for this clear operation.
     *
     * @return the score bonus
    /**
     * Gets the indices of the rows that were cleared.
     *
     * @return an array of row indices
     */
    public int[] getClearedRows() {
    public int getScoreBonus() {
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