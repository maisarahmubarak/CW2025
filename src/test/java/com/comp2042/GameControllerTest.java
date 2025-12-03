package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GuiControllerStub view;

    @BeforeEach
    void setUp() {
        view = new GuiControllerStub();
    }

    @Test
    void onDownEventLocksRowsAndTriggersGameOver() throws Exception {
        GameController controller = new GameController(view);
        BoardStub board = new BoardStub();
        board.moveDownResult = false;
        board.clearRowResult = new ClearRow(1, board.matrix, 150);
        board.createNewBrickResult = true;
        injectBoard(controller, board);

        DownData data = controller.onDownEvent(new MoveAction(ActionType.DOWN, ActionSource.THREAD));

        assertTrue(board.mergeCalled);
        assertTrue(board.clearRowsCalled);
        assertEquals(150, board.getScore().scoreProperty().get());
        assertTrue(view.backgroundRefreshed);
        assertTrue(view.gameOverCalled);
        assertSame(board.clearRowResult, data.getClearRow());
        assertSame(board.viewData, data.getViewData());
    }

    @Test
    void onDownEventFromUserAwardsSoftDropPoint() throws Exception {
        GameController controller = new GameController(view);
        BoardStub board = new BoardStub();
        board.moveDownResult = true;
        injectBoard(controller, board);

        DownData data = controller.onDownEvent(new MoveAction(ActionType.DOWN, ActionSource.USER));

        assertEquals(1, board.getScore().scoreProperty().get());
        assertFalse(board.mergeCalled);
        assertNull(data.getClearRow());
    }

    @Test
    void createNewGameResetsBoardAndRefreshesBackground() throws Exception {
        GameController controller = new GameController(view);
        BoardStub board = new BoardStub();
        injectBoard(controller, board);

        controller.createNewGame();

        assertTrue(board.newGameCalled);
        assertTrue(view.backgroundRefreshed);
        assertSame(board.matrix, view.lastBackground);
    }

    private static void injectBoard(GameController controller, Board board) throws Exception {
        Field field = GameController.class.getDeclaredField("board");
        field.setAccessible(true);
        field.set(controller, board);
    }

    private static final class GuiControllerStub extends GuiController {
        private boolean backgroundRefreshed;
        private boolean gameOverCalled;
        private int[][] lastBackground;

        @Override
        public void setColorPalette(BrickColorPalette palette) {
            // no-op for tests
        }

        @Override
        public void setEventListener(InputActionListener eventListener) {
            // ignore wiring in tests
        }

        @Override
        public void initGameView(int[][] boardMatrix, ViewData brick) {
            // do nothing; avoids JavaFX dependencies
        }

        @Override
        public void refreshGameBackground(int[][] board) {
            backgroundRefreshed = true;
            lastBackground = board;
        }

        @Override
        public void gameOver() {
            gameOverCalled = true;
        }
    }

    private static final class BoardStub implements Board {
        private final GameScore score = new GameScore();
        private final BrickShape shape = BrickShape.fromMatrix(new int[][]{{9}});
        private final BrickShape nextShape = BrickShape.fromMatrix(new int[][]{{8}});
        private final int[][] matrix = new int[4][4];
        private final ViewData viewData = new ViewData(shape, 0, 0, nextShape);

        private boolean moveDownResult;
        private boolean createNewBrickResult;
        private boolean mergeCalled;
        private boolean clearRowsCalled;
        private boolean newGameCalled;
        private ClearRow clearRowResult = new ClearRow(0, matrix, 0);

        @Override
        public boolean moveBrickDown() {
            return moveDownResult;
        }

        @Override
        public boolean moveBrickLeft() {
            return true;
        }

        @Override
        public boolean moveBrickRight() {
            return true;
        }

        @Override
        public boolean rotateLeftBrick() {
            return true;
        }

        @Override
        public boolean createNewBrick() {
            return createNewBrickResult;
        }

        @Override
        public int[][] getBoardMatrix() {
            return matrix;
        }

        @Override
        public ViewData getViewData() {
            return viewData;
        }

        @Override
        public void mergeBrickToBackground() {
            mergeCalled = true;
        }

        @Override
        public ClearRow clearRows() {
            clearRowsCalled = true;
            return clearRowResult;
        }

        @Override
        public GameScore getScore() {
            return score;
        }

        @Override
        public void newGame() {
            newGameCalled = true;
        }
    }
}
