package com.comp2042;

public class GameController implements InputActionListener {

    private Board board;

    private final GuiController viewGuiController;

    private final BrickThemeFactory brickThemeFactory;

    public GameController(GuiController c) {
        viewGuiController = c;
        this.brickThemeFactory = new ClassicBrickFactory();
        // Board: 26 visible rows + 2 hidden rows = 28 total; 10 columns
        // Container: 520px ÷ 20px/brick = 26 visible rows exactly
        this.board = new SimpleBoard(28, 10, brickThemeFactory);
        viewGuiController.setColorPalette(brickThemeFactory.createPalette());
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
    }

    @Override
    public DownData onDownEvent(MoveAction event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
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
