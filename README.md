# My Tetris Game

## Author

| | |
|---|---|
| **Full Name** | Maisarah binti Mubarakad Arshad |
| **Student ID** | 20618336 |
| **OWA** | hfymm18 |
| **GitHub** | [@maisarahmubarak](https://github.com/maisarahmubarak) |

## GitHub Repository
[Link to Repository](https://github.com/maisarahmubarak/CW2025)

---

## Table of Contents

1. [Compilation Instructions](#compilation-instructions)
   - [Prerequisites](#prerequisites)
   - [IntelliJ IDEA Setup](#intellij-idea-setup)
   - [Visual Studio Code Setup](#visual-studio-code-setup)
   - [Command Line (Any OS)](#command-line-any-os)
2. [Implemented and Working Properly](#implemented-and-working-properly)
   - [Gameplay Features](#gameplay-features)
   - [UI and Interactivity](#ui-and-interactivity)
   - [Audio and Visual Features](#audio-and-visual-features)
   - [Technical Features](#technical-features)
3. [Features Implemented but Not Working Properly](#features-implemented-but-not-working-properly)
4. [Features Not Implemented](#features-not-implemented)
5. [New Java Classes](#new-java-classes)
   - [Root Package](#root-package-comcomp2042)
   - [Input Package](#input-package-comcomp2042input)
   - [Logic - Board Package](#logic---board-package-comcomp2042logicboard)
   - [Logic - Bricks Package](#logic---bricks-package-comcomp2042logicbricks)
   - [Logic - Game Package](#logic---game-package-comcomp2042logicgame)
   - [UI Package](#ui-package-comcomp2042ui)
   - [UI - Effects Package](#ui---effects-package-comcomp2042uieffects)
   - [UI - Menu Package](#ui---menu-package-comcomp2042uimenu)
   - [UI - Overlay Package](#ui---overlay-package-comcomp2042uioverlay)
   - [UI - State Package](#ui---state-package-comcomp2042uistate)
6. [Design Patterns Used](#design-patterns-used)
7. [Package Structure](#package-structure)
8. [Resources](#resources)
9. [Unit Tests](#unit-tests)
10. [Javadoc Documentation](#javadoc-documentation)
11. [Controls](#controls)

---

## Compilation Instructions

### Prerequisites
- **Java Development Kit (JDK 17 or higher)** installed (project uses Java 23)
- **Maven** installed and configured in system PATH
- **Git** for cloning the repository

### Clone the Repository
```bash
git clone https://github.com/maisarahmubarak/CW2025.git
```

---

### IntelliJ IDEA Setup

1. Open IntelliJ IDEA and select `File > Open...`
2. Navigate to the cloned `CW2025` folder and open it.
3. **Load Maven Dependencies:** IntelliJ auto-loads dependencies. If not, click the Maven tool window (right sidebar) and click the refresh icon.
4. **Build Project:** Select `Build > Build Project` or press `Ctrl + F9`.
5. **Run Application:** 
   - Option 1: Navigate to `src/main/java/com/comp2042/ui/Main.java`, right-click and select `Run 'Main'`
   - Option 2: Open Terminal and run:
     ```bash
     mvn clean javafx:run
     ```
6. **Play the Game:** The game window will open. Enjoy!

---

### Visual Studio Code Setup

1. **Install Required Extensions:**
   - [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
   - [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)

2. **Open Project:**
   - Open VS Code and select `File > Open Folder...`
   - Navigate to the cloned `CW2025` folder and open it.

3. **Wait for Project to Load:**
   - VS Code will automatically detect the Maven project and download dependencies.
   - Wait for the Java language server to initialize (check the status bar).

4. **Build Project:**
   - Open the Command Palette (`Ctrl + Shift + P`)
   - Type and select `Java: Force Java Compilation > Full`
   - Or open Terminal (`Ctrl + `` `) and run:
     ```bash
     mvn clean compile
     ```

5. **Run Application:**
   - **Option 1 - Maven Command (Recommended):**
     - Open Terminal and run:
       ```bash
       mvn clean javafx:run
       ```
   - **Option 2 - Run from Editor:**
     - Open `src/main/java/com/comp2042/ui/Main.java`
     - Click the `Run` button above the `main` method or press `F5`

6. **Play the Game:** The game window will open. Enjoy!

---

### Command Line (Any OS)

1. Navigate to the project directory:
   ```bash
   cd CW2025
   ```

2. Build and run using Maven:
   ```bash
   mvn clean javafx:run
   ```

3. To only build without running:
   ```bash
   mvn clean compile
   ```

4. To run tests:
   ```bash
   mvn test
   ```

---

### Dependencies
- **Maven** for dependency management
- **JavaFX 21.0.6** (Controls, FXML, Media)
- **JUnit 5.12.1** for testing

---

## Implemented and Working Properly

### Gameplay Features

#### Game Modes

| Mode | Drop Interval | Description |
|------|---------------|-------------|
| **Classic** | 400ms | Standard Tetris gameplay with no modifiers. Perfect for learning the game mechanics. |
| **Speed** | 200ms | Fast-paced mode where bricks drop at double speed. Speed increases every 100 points for an escalating challenge. |
| **Danger** | 400ms | Unpredictable gameplay with random effects including screen flashes, sudden speed boosts, and inverted controls. |

#### Core Tetris Mechanics

- **Seven Classic Tetrominoes:** I (Cyan), J (Blue), L (Orange), O (Yellow), S (Green), T (Purple), Z (Red)
- **Movement Controls:** Arrow keys or WASD for Left, Right, Down, and Rotate
- **Full Rotation System:** All pieces support 4-state rotation (except O-block with 1 state)
- **Row Clearing:** Complete horizontal lines are cleared with score bonus calculation (50 × lines² points)
- **Next Brick Preview:** Shows 3 upcoming bricks to help players strategize
- **Collision Detection:** Standard Tetris boundary and piece collision mechanics
- **Garbage Lines:** Support for adding garbage rows (utilized in Danger mode)

#### Scoring System

- **Dynamic Scoring:** Points awarded based on cleared rows with exponential bonus
- **High Score Persistence:** Best scores are saved to file and displayed during gameplay
- **Score Binding:** Real-time score updates using JavaFX property bindings

#### Game Timer

- Displays elapsed game time in `MM:SS` format
- Timer pauses when game is paused and resumes seamlessly

---

### UI and Interactivity

#### Main Menu

**Functionality:**
- Entry point to the game with navigation options:
  - **Start Game:** Opens mode selection dialog
  - **Tutorial:** Displays control instructions
  - **Settings:** Access brightness and volume controls

**Visual Design:**
- Animated retro synthwave aesthetic with neon colors
- Character-by-character title reveal animation with flicker effect
- Floating particle background effects

#### Mode Selection Screen

**Functionality:**
- Allows players to choose between Classic, Speed, and Danger modes
- Clear descriptions of each mode's characteristics
- Seamless transition to gameplay upon selection

#### Settings Screen

**Functionality:**
- **Brightness Slider:** Adjusts screen brightness (0.0 to 1.0)
- **Volume Slider:** Controls sound effects and music volume (0% to 100%)
- Settings persist using JavaFX property bindings

**Design:**
- Clean, intuitive slider interface
- Real-time visual feedback when adjusting settings

#### Tutorial Screen

**Functionality:**
- Visual guide showing keyboard controls
- Explains game mechanics and objectives
- Easy navigation back to main menu

**Design:**
- Clear visual representation of control keys
- Concise explanations for new players

#### Pause Menu

**Functionality:**
- Accessible via `P` key during gameplay
- Options include:
  - **Resume:** Continues gameplay with 3-2-1 countdown animation
  - **Restart:** Begins a new game in the same mode
  - **Main Menu:** Returns to the main menu

**Design:**
- Semi-transparent overlay maintaining game context
- Clearly labeled buttons for quick navigation

#### Game Over Screen

**Functionality:**
- Displays final score achieved
- Provides options to restart or return to main menu
- Game over sound effect plays upon triggering

**Design:**
- Custom `GameOverPanel` component with styled layout
- Prominent score display with action buttons

#### Score Display Panel

- Current score with real-time updates
- High score display showing best achievement
- Timer showing elapsed game time

---

### Audio and Visual Features

#### Sound Effects

| Sound | Trigger |
|-------|---------|
| `Tetris_Theme.wav` | Background music during gameplay |
| `Tetris_RowClear.wav` | Played when rows are cleared |
| `Tetris_GameOver.wav` | Played when game ends |

**Volume Control:** Managed through `GameSettings` with adjustable volume slider.

#### Visual Effects

**Neon Grid Background (`NeonGridBackground`):**
- Animated 80s synthwave perspective grid
- Scrolling horizon effect with neon sun
- Glow and bloom effects for retro aesthetic

**Particle Background (`RetroParticleBackground`):**
- Floating neon particles in menu screens
- Smooth movement with color variations

**Title Animation (`TitleRevealAnimator`):**
- Character-by-character reveal effect
- Flicker animation for authentic retro feel

**Row Clear Animation (`BoardAnimationController`):**
- Falling/fading block animation
- Board shake effect on row clear
- Sound synchronization with visual effects

#### Custom Fonts

| Font | Usage |
|------|-------|
| `PressStart2P-Regular.ttf` | Retro pixel font for titles and menus |
| `digital.ttf` | Digital display font for score and timer |

---

### Technical Features

#### Brick Preview System (`BrickProvider`)
- Maintains queue of upcoming bricks
- Provides preview of next 3 bricks
- Random generation using `RandomBrickGenerator`

#### Brick Rotation (`BrickRotator`)
- Handles rotation state management
- Calculates next rotation shape
- Supports all 7 tetromino types

#### Board Management (`SimpleBoard`, `BoardMatrix`)
- 28×10 grid (26 visible + 2 hidden rows)
- Matrix operations for collision detection
- Row clearing with score calculation

#### Game Loop (`GameLoop`)
- JavaFX Timeline-based tick system
- Configurable drop interval per game mode
- Pause/resume support

---

## Features Implemented but Not Working Properly

### Speed Increase in Speed Mode

**Current Status:** Speed mode starts at 200ms drop interval and is designed to increase speed every 100 points.

**Issue:** The speed increase mechanism may not consistently apply at exact point thresholds due to timing between score updates and loop interval changes.

**Possible Causes:**
- Race condition between score property listener and game loop update
- Need for more granular speed increase thresholds

---

## Features Not Implemented

### Extended Background Music

**Description:**
Additional background music tracks were planned to accompany gameplay, providing an immersive audio experience while players engage with the game. The goal was to have thematic music that dynamically complements the retro synthwave aesthetic.

**Reason for Omission:**
- Audio Selection Challenges: Finding suitable royalty-free music that aligned with the game's retro synthwave theme proved difficult.
- Quality Standards: Rather than settling for audio that did not match the game's visual identity, the decision was made to prioritize other features.
- Time Allocation: Resources were redirected to implementing core gameplay mechanics and visual effects.

### Earthquake Effect in Danger Mode

**Description:**
An earthquake visual effect was planned for Danger Mode to enhance the chaotic gameplay experience. This effect would have caused the game board to shake and tremble unpredictably, adding an additional layer of challenge.

**Reason for Omission:**
- Technical Complications: The implementation introduced unexpected bugs affecting game stability and rendering.
- Collision Detection Conflicts: The screen shake interfered with precise collision detection required for Tetris gameplay.
- Gameplay Balance: After testing, the effect proved too disorienting and negatively impacted player experience.

### Loading Panel Transitions

**Description:**
Animated loading panels were planned to provide smooth visual transitions between menus and game screens. These would have included progress indicators and thematic animations consistent with the retro aesthetic.

**Reason for Omission:**
- Time Constraints: Development time was prioritized for core gameplay features and bug fixes.
- Scope Management: The feature was deemed non-essential for the minimum viable product.
- Future Enhancement: This remains a candidate for future iterations of the project.

---

## New Java Classes

### Root Package: `com.comp2042`

#### BrickColorPalette
- **Location:** `com.comp2042.BrickColorPalette`
- **Description:** Interface defining mapping of brick IDs to UI colors. Enables different visual themes for tetrominoes.

#### BrickThemeFactory
- **Location:** `com.comp2042.BrickThemeFactory`
- **Description:** Abstract factory interface for creating brick generators and color palettes. Implements Factory design pattern for theme management.

#### ClassicBrickFactory
- **Location:** `com.comp2042.ClassicBrickFactory`
- **Description:** Concrete factory implementing `BrickThemeFactory` for classic Tetris theme with traditional colors.

#### ClassicBrickPalette
- **Location:** `com.comp2042.ClassicBrickPalette`
- **Description:** Classic Tetris color scheme implementation: Cyan (I), Blue (J), Orange (L), Yellow (O), Green (S), Purple (T), Red (Z).

#### BrickProvider
- **Location:** `com.comp2042.BrickProvider`
- **Description:** Manages brick generation queue and provides preview of upcoming bricks (3 bricks preview). Central component for tetromino dispensing.

#### BrickRotator
- **Location:** `com.comp2042.BrickRotator`
- **Description:** Handles rotation logic for the active brick, calculating next rotation state based on current orientation.

---

### Input Package: `com.comp2042.input`

#### ActionSource
- **Location:** `com.comp2042.input.ActionSource`
- **Description:** Enum distinguishing between USER (keyboard input) and THREAD (game loop/gravity) actions.

#### ActionType
- **Location:** `com.comp2042.input.ActionType`
- **Description:** Enum for movement types: DOWN, LEFT, RIGHT, ROTATE.

#### GameKeyHandler
- **Location:** `com.comp2042.input.GameKeyHandler`
- **Description:** JavaFX EventHandler for keyboard input. Captures key presses and delegates to GuiController.

#### InputActionListener
- **Location:** `com.comp2042.input.InputActionListener`
- **Description:** Interface for handling game input actions with methods for each direction (onDownEvent, onLeftEvent, etc.).

#### InputAdapter
- **Location:** `com.comp2042.input.InputAdapter`
- **Description:** Generic adapter interface converting raw input events to game actions.

#### InputEvent
- **Location:** `com.comp2042.input.InputEvent`
- **Description:** Wrapper for user input events with Kind enum (MOVE, NEW_GAME).

#### InputSource
- **Location:** `com.comp2042.input.InputSource`
- **Description:** Interface for input event sources with bind/unbind functionality.

#### JavaFxInputSource
- **Location:** `com.comp2042.input.JavaFxInputSource`
- **Description:** JavaFX-specific implementation capturing KeyEvents from a Pane.

#### KeyEventInputAdapter
- **Location:** `com.comp2042.input.KeyEventInputAdapter`
- **Description:** Converts JavaFX KeyEvents to InputEvents. Supports both WASD and Arrow key schemes.

#### MoveAction
- **Location:** `com.comp2042.input.MoveAction`
- **Description:** Encapsulates action type and source for movement commands. Used throughout the input system.

---

### Logic - Board Package: `com.comp2042.logic.board`

#### Board
- **Location:** `com.comp2042.logic.board.Board`
- **Description:** Interface defining game board operations including move, rotate, merge, and clearRows.

#### BoardMatrix
- **Location:** `com.comp2042.logic.board.BoardMatrix`
- **Description:** 2D matrix representation managing board state and cell operations.

#### BoardView
- **Location:** `com.comp2042.logic.board.BoardView`
- **Description:** Abstraction interface for rendering board and bricks to the display.

#### ClearRow
- **Location:** `com.comp2042.logic.board.ClearRow`
- **Description:** DTO containing result of row clearing: lines removed, new matrix, score bonus, and cleared row indices.

#### MatrixOperations
- **Location:** `com.comp2042.logic.board.MatrixOperations`
- **Description:** Utility class for matrix operations including intersect, copy, merge, and checkRemoving.

#### SimpleBoard
- **Location:** `com.comp2042.logic.board.SimpleBoard`
- **Description:** Concrete Board implementation managing game state, active brick, scoring, and all game logic.

---

### Logic - Bricks Package: `com.comp2042.logic.bricks`

#### Brick
- **Location:** `com.comp2042.logic.bricks.Brick`
- **Description:** Interface defining brick behavior with list of rotation shapes.

#### BrickCell
- **Location:** `com.comp2042.logic.bricks.BrickCell`
- **Description:** Leaf node in composite pattern representing a single occupied cell.

#### BrickComponent
- **Location:** `com.comp2042.logic.bricks.BrickComponent`
- **Description:** Base interface for composite hierarchy enabling cell iteration.

#### BrickGenerator
- **Location:** `com.comp2042.logic.bricks.BrickGenerator`
- **Description:** Interface for generating new bricks in the game.

#### BrickShape
- **Location:** `com.comp2042.logic.bricks.BrickShape`
- **Description:** Composite shape definition for tetromino rotations with width/height metadata.

#### CellConsumer
- **Location:** `com.comp2042.logic.bricks.CellConsumer`
- **Description:** Functional interface for iterating over brick cells.

#### IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick
- **Location:** `com.comp2042.logic.bricks.*`
- **Description:** Seven classic tetromino implementations, each defining their unique rotation shapes and colors.

#### RandomBrickGenerator
- **Location:** `com.comp2042.logic.bricks.RandomBrickGenerator`
- **Description:** Generates bricks in random order with preview queue for upcoming pieces.

---

### Logic - Game Package: `com.comp2042.logic.game`

#### ActiveBrick
- **Location:** `com.comp2042.logic.game.ActiveBrick`
- **Description:** Represents currently falling brick. Handles movement, rotation, and collision detection.

#### DownData
- **Location:** `com.comp2042.logic.game.DownData`
- **Description:** DTO containing results of "move down" operation including ClearRow and ViewData.

#### GameLoop
- **Location:** `com.comp2042.logic.game.GameLoop`
- **Description:** Main game loop using JavaFX Timeline with configurable tick interval.

#### GameMode
- **Location:** `com.comp2042.logic.game.GameMode`
- **Description:** Enum defining game modes: CLASSIC (400ms), SPEED (200ms), DANGER (400ms + effects).

#### GameScore
- **Location:** `com.comp2042.logic.game.GameScore`
- **Description:** Manages score using JavaFX IntegerProperty for UI binding.

#### GameSettings
- **Location:** `com.comp2042.logic.game.GameSettings`
- **Description:** Global settings including brightness (0-1) and volume (0-100) with JavaFX properties.

#### NextShapeInfo
- **Location:** `com.comp2042.logic.game.NextShapeInfo`
- **Description:** Holds next brick shape information for preview display.

#### RenderLoop
- **Location:** `com.comp2042.logic.game.RenderLoop`
- **Description:** AnimationTimer-based rendering loop for frame updates.

#### ViewData
- **Location:** `com.comp2042.logic.game.ViewData`
- **Description:** DTO for rendering containing brick shape, position, and next brick shapes.

---

### UI Package: `com.comp2042.ui`

#### Main
- **Location:** `com.comp2042.ui.Main`
- **Description:** Application entry point. Loads fonts, main menu FXML, and launches JavaFX application.

#### GuiController
- **Location:** `com.comp2042.ui.GuiController`
- **Description:** Main GUI controller handling FXML bindings and initializing all sub-controllers.

#### GameController
- **Location:** `com.comp2042.ui.GameController`
- **Description:** Controller managing game logic, board, and view interaction.

#### GameInputController
- **Location:** `com.comp2042.ui.GameInputController`
- **Description:** Handles keyboard input mapping and dispatching to appropriate handlers.

#### GameViewController
- **Location:** `com.comp2042.ui.GameViewController`
- **Description:** Coordinates game view updates, animations, and event handling.

#### JavaFxBoardView
- **Location:** `com.comp2042.ui.JavaFxBoardView`
- **Description:** JavaFX canvas-based board renderer implementing BoardView interface.

#### ScoreUiController
- **Location:** `com.comp2042.ui.ScoreUiController`
- **Description:** Manages score display UI and high score file persistence.

---

### UI - Effects Package: `com.comp2042.ui.effects`

#### BackgroundEffectController
- **Location:** `com.comp2042.ui.effects.BackgroundEffectController`
- **Description:** Manages background visual effects lifecycle including neon grid initialization.

#### BoardAnimationController
- **Location:** `com.comp2042.ui.effects.BoardAnimationController`
- **Description:** Handles row clear animations, sounds, and board shake effects.

#### NeonGridBackground
- **Location:** `com.comp2042.ui.effects.NeonGridBackground`
- **Description:** Animated 80s synthwave neon grid background with scrolling perspective and horizon sun.

#### RetroParticleBackground
- **Location:** `com.comp2042.ui.effects.RetroParticleBackground`
- **Description:** Animated floating particle background with neon colors for menu screens.

#### TitleRevealAnimator
- **Location:** `com.comp2042.ui.effects.TitleRevealAnimator`
- **Description:** Character-by-character title reveal animation with flicker effect.

---

### UI - Menu Package: `com.comp2042.ui.menu`

#### MainMenuController
- **Location:** `com.comp2042.ui.menu.MainMenuController`
- **Description:** Main menu screen controller with Start, Tutorial, and Settings navigation.

#### ModeSelectionController
- **Location:** `com.comp2042.ui.menu.ModeSelectionController`
- **Description:** Game mode selection dialog controller (Classic, Speed, Danger).

#### SettingsController
- **Location:** `com.comp2042.ui.menu.SettingsController`
- **Description:** Settings screen controller with brightness and volume sliders.

#### TutorialController
- **Location:** `com.comp2042.ui.menu.TutorialController`
- **Description:** Tutorial/help screen controller showing control instructions.

---

### UI - Overlay Package: `com.comp2042.ui.overlay`

#### GameOverlayController
- **Location:** `com.comp2042.ui.overlay.GameOverlayController`
- **Description:** Facade coordinating pause and game over overlay sub-controllers.

#### GameOverOverlayController
- **Location:** `com.comp2042.ui.overlay.GameOverOverlayController`
- **Description:** Manages game over overlay with score display and action buttons.

#### GameOverPanel
- **Location:** `com.comp2042.ui.overlay.GameOverPanel`
- **Description:** Custom BorderPane component for game over screen with score, restart, and menu buttons.

#### NotificationPanel
- **Location:** `com.comp2042.ui.overlay.NotificationPanel`
- **Description:** Animated notification panel for displaying score bonuses and countdown timers.

#### PauseOverlayController
- **Location:** `com.comp2042.ui.overlay.PauseOverlayController`
- **Description:** Manages pause menu overlay with Resume, Restart, and Main Menu options.

---

### UI - State Package: `com.comp2042.ui.state`

#### DangerModeController
- **Location:** `com.comp2042.ui.state.DangerModeController`
- **Description:** Manages Danger mode special effects: screen flash, speed boost, and control inversion.

#### GameLifecycleController
- **Location:** `com.comp2042.ui.state.GameLifecycleController`
- **Description:** Coordinates game lifecycle events: start, pause, resume, game over, and mode switching.

#### GamePauseController
- **Location:** `com.comp2042.ui.state.GamePauseController`
- **Description:** Handles pause/resume with countdown animation (3, 2, 1).

#### GameStateManager
- **Location:** `com.comp2042.ui.state.GameStateManager`
- **Description:** Manages game state properties (isPaused, isGameOver) with JavaFX bindings.

#### GameTimerController
- **Location:** `com.comp2042.ui.state.GameTimerController`
- **Description:** Manages game timer display in MM:SS format with pause support.

---

## Design Patterns Used

### MVC (Model-View-Controller)
- **Model:** `Board`, `SimpleBoard`, `BoardMatrix`
- **View:** `JavaFxBoardView`, FXML layouts
- **Controller:** `GameController`, `GuiController`, `GameViewController`
- **Usage:** Separates game logic from UI rendering and user input handling.

### Abstract Factory Pattern
- **Interface:** `BrickThemeFactory`
- **Implementation:** `ClassicBrickFactory`
- **Usage:** Creates families of related objects (brick generators and color palettes) for different visual themes.

### Composite Pattern
- **Component:** `BrickComponent` interface
- **Leaf:** `BrickCell` (single cell)
- **Composite:** `BrickShape` (collection of cells)
- **Usage:** Represents tetromino shapes as hierarchical structures for flexible rendering and collision detection.

### Observer Pattern
- **Implementation:** JavaFX Property bindings
- **Examples:** `GameScore.scoreProperty()`, `GameSettings.volumeProperty()`, `GameStateManager`
- **Usage:** Enables real-time UI updates when game state changes.

### Adapter Pattern
- **Interface:** `InputAdapter`
- **Implementation:** `KeyEventInputAdapter`
- **Usage:** Converts JavaFX KeyEvents to game-specific InputEvents, decoupling input handling from JavaFX.

### Facade Pattern
- **Implementation:** `GameOverlayController`
- **Usage:** Provides simplified interface to coordinate pause and game over overlay subsystems.

### Strategy Pattern
- **Interface:** `BrickGenerator`
- **Implementation:** `RandomBrickGenerator`
- **Usage:** Allows different brick generation strategies to be swapped without changing client code.

### State Pattern
- **Implementation:** `GameStateManager`
- **Usage:** Manages game states (running, paused, game over) with clean transitions.

### DTO (Data Transfer Object)
- **Classes:** `ViewData`, `ClearRow`, `DownData`, `NextShapeInfo`, `MoveAction`
- **Usage:** Encapsulates data passed between layers without exposing internal implementation.

### Singleton-like Pattern
- **Implementation:** `GameSettings` with static properties
- **Usage:** Provides global access to game settings across all components.

---

## Package Structure

```
com.comp2042/
├── BrickColorPalette.java
├── BrickProvider.java
├── BrickRotator.java
├── BrickThemeFactory.java
├── ClassicBrickFactory.java
├── ClassicBrickPalette.java
│
├── input/
│   ├── ActionSource.java
│   ├── ActionType.java
│   ├── GameKeyHandler.java
│   ├── InputActionListener.java
│   ├── InputAdapter.java
│   ├── InputEvent.java
│   ├── InputEventListener.java
│   ├── InputSource.java
│   ├── JavaFxInputSource.java
│   ├── KeyEventInputAdapter.java
│   ├── MoveAction.java
│   └── MoveEvent.java
│
├── logic/
│   ├── board/
│   │   ├── Board.java
│   │   ├── BoardMatrix.java
│   │   ├── BoardView.java
│   │   ├── ClearRow.java
│   │   ├── MatrixOperations.java
│   │   └── SimpleBoard.java
│   │
│   ├── bricks/
│   │   ├── Brick.java
│   │   ├── BrickCell.java
│   │   ├── BrickComponent.java
│   │   ├── BrickGenerator.java
│   │   ├── BrickShape.java
│   │   ├── CellConsumer.java
│   │   ├── IBrick.java
│   │   ├── JBrick.java
│   │   ├── LBrick.java
│   │   ├── OBrick.java
│   │   ├── SBrick.java
│   │   ├── TBrick.java
│   │   ├── ZBrick.java
│   │   └── RandomBrickGenerator.java
│   │
│   └── game/
│       ├── ActiveBrick.java
│       ├── DownData.java
│       ├── GameLoop.java
│       ├── GameMode.java
│       ├── GameScore.java
│       ├── GameSettings.java
│       ├── NextShapeInfo.java
│       ├── RenderLoop.java
│       ├── Score.java
│       └── ViewData.java
│
└── ui/
    ├── Main.java
    ├── GuiController.java
    ├── GameController.java
    ├── GameInputController.java
    ├── GameViewController.java
    ├── JavaFxBoardView.java
    ├── GameBoardView.java
    ├── ScoreUiController.java
    │
    ├── effects/
    │   ├── BackgroundEffectController.java
    │   ├── BoardAnimationController.java
    │   ├── NeonGridBackground.java
    │   ├── RetroParticleBackground.java
    │   └── TitleRevealAnimator.java
    │
    ├── menu/
    │   ├── MainMenuController.java
    │   ├── ModeSelectionController.java
    │   ├── SettingsController.java
    │   └── TutorialController.java
    │
    ├── overlay/
    │   ├── GameOverlayController.java
    │   ├── GameOverOverlayController.java
    │   ├── GameOverPanel.java
    │   ├── NotificationPanel.java
    │   └── PauseOverlayController.java
    │
    └── state/
        ├── DangerModeController.java
        ├── GameLifecycleController.java
        ├── GamePauseController.java
        ├── GameStateManager.java
        └── GameTimerController.java
```

---

## Resources

### FXML Layout Files

| File | Description |
|------|-------------|
| `main_menu.fxml` | Main menu with Start, Tutorial, Settings buttons |
| `mode_selection.fxml` | Game mode selection dialog |
| `settings.fxml` | Settings panel with sliders |
| `tutorial.fxml` | Control instructions display |
| `gameLayout.fxml` | Main game screen layout with board, score, timer |

### Sound Files

| File | Description |
|------|-------------|
| `Tetris_Theme.wav` | Background music |
| `Tetris_RowClear.wav` | Row clear sound effect |
| `Tetris_GameOver.wav` | Game over sound effect |

### Font Files

| File | Description |
|------|-------------|
| `PressStart2P-Regular.ttf` | Retro pixel font for titles and menus |
| `digital.ttf` | Digital display font for score and timer |

### Stylesheet

| File | Description |
|------|-------------|
| `window_style.css` | Complete UI styling for menus, game, and overlays |

---

## Unit Tests

The project includes **17 unit test classes** in `src/test/java/com/comp2042/`:

| Test Class | Coverage Area |
|------------|---------------|
| `ActiveBrickTest` | Active brick movement and state |
| `ActiveBrickRotationTest` | Brick rotation mechanics |
| `BoardMatrixTest` | Board matrix operations |
| `SimpleBoardTest` | Simple board functionality |
| `SimpleBoardLogicTest` | Board logic and rules |
| `BrickProviderTest` | Brick provider queue |
| `BrickRotatorTest` | Rotation calculations |
| `BrickShapeTest` | Brick shape definitions |
| `ClassicBrickPaletteTest` | Color palette mapping |
| `ClearRowTest` | Row clearing logic |
| `GameLoopTest` | Game loop timing |
| `GameScoreTest` | Score calculation |
| `GuiControllerAnimationTest` | GUI animation triggers |
| `InputEventTest` | Input event handling |
| `KeyEventInputAdapterTest` | Key event adaptation |
| `MatrixOperationsTest` | Matrix utility functions |
| `RandomBrickGeneratorTest` | Random generation |

**Run Tests:**
```bash
mvn test
```

---

## Javadoc Documentation

Complete Javadoc documentation is pre-generated and available in the `Javadoc/` folder.

**View Documentation:** Open `Javadoc/index.html` in a web browser.

**Regenerate Javadoc:**
```bash
mvn javadoc:javadoc
```

---

## Controls

| Key | Action |
|-----|--------|
| ← / A | Move Left |
| → / D | Move Right |
| ↓ / S | Move Down (Soft Drop) |
| ↑ / W | Rotate |
| P | Pause Game |
| N | New Game |


