package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Owns the JavaFX board, sprites, and status display. */
public class GameView {
    private static final String SPRITE_PATH = "/resources/views/res/";
    private static final double PADDING = 20;
    private static final Color BOARD_COLOR = Color.web("feffe4");

    private Canvas gridCanvas;
    private GraphicsContext gc;

    private Label gameOverLabel;
    private Label gameWonLabel;
    private Label scoreLabel;

    private final Stage stage;
    private final double cellSize;
    private final double boardWidth;
    private final double boardHeight;
    private final ImageView headSprite = new ImageView(loadSprite("snakehead.png"));
    private final ImageView bodySprite = new ImageView(loadSprite("snakebody.png"));
    private final ImageView turnSprite = new ImageView(loadSprite("snaketurnleft.png"));
    private final ImageView tailSprite = new ImageView(loadSprite("snaketail.png"));
    private final SnapshotParameters spriteSnapshot = new SnapshotParameters();
    private final Map<CoinType, Image> coinSprites = new EnumMap<>(CoinType.class);
    private final int[][] turnArray;

    /** Sets up a view for the board on the JavaFX Application Thread. */
    public GameView(Game game, Stage stage) {
        this.stage = stage;
        cellSize = baseCellSize(game.getHeight());
        boardWidth = game.getWidth() * cellSize;
        boardHeight = game.getHeight() * cellSize;
        turnArray = new int[game.getWidth()][game.getHeight()];
        spriteSnapshot.setFill(Color.TRANSPARENT);
        for (CoinType type : CoinType.values()) {
            coinSprites.put(type, loadSprite(coinSpriteName(type)));
        }
    }

    /** Creates the scene and lays out the board and status labels. */
    public Scene createScene() {
        gridCanvas = new Canvas(boardWidth, boardHeight);
        gc = gridCanvas.getGraphicsContext2D();

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = GameView.class.getResource("/resources/views/styling.css").toExternalForm();
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setVisible(true);
        StackPane pane = new StackPane(gridCanvas, gameOverLabel, gameWonLabel, scoreLabel);
        pane.setPadding(new Insets(PADDING));
        StackPane.setAlignment(gameOverLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(gameWonLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(scoreLabel, Pos.BOTTOM_CENTER);
        return new Scene(pane, boardWidth, boardHeight);
    }

    /** Resizes and styles the canvas. The caller redraws the board afterward. */
    public void setFullscreen(boolean enabled) {
        double scale = 1;
        if (enabled) {
            Screen screen = Screen.getScreensForRectangle(
                    stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())
                    .stream().findFirst().orElse(Screen.getPrimary());
            Rectangle2D bounds = screen.getBounds();
            scale = fitScale(boardWidth, boardHeight,
                    bounds.getWidth() - 2 * PADDING, bounds.getHeight() - 2 * PADDING);
        }

        stage.setFullScreen(enabled);
        gridCanvas.setWidth(boardWidth * scale);
        gridCanvas.setHeight(boardHeight * scale);
        gc.setTransform(scale, 0, 0, scale, 0, 0);

        int statusSize = enabled ? 50 : 15;
        gameOverLabel.setStyle("-fx-font-size: " + statusSize + ";");
        gameWonLabel.setStyle("-fx-font-size: " + statusSize + ";");
        scoreLabel.setStyle("-fx-font-size: " + (enabled ? 35 : 15) + ";");
    }

    /** Chooses the existing windowed cell size for the board height. */
    static int baseCellSize(int height) {
        return height > 50 ? 10 : 20;
    }

    /** Returns the uniform scale that fits both dimensions within the viewport. */
    static double fitScale(double width, double height, double availableWidth, double availableHeight) {
        return Math.min(availableWidth / width, availableHeight / height);
    }

    /** Draws the current board without exposing the canvas or graphics context. */
    public void renderBoard(Snake snek, Coin coin, Obstacle obstacle) {
        gc.setFill(BOARD_COLOR);
        gc.fillRect(0, 0, boardWidth, boardHeight);

        drawCoin(coin);

        List<Point> body = snek.getBody();
        for (int index = 0; index < body.size(); index++) {
            drawBody(body, index, snek.getSnakeDir());
        }
        drawSprite(headSprite, snek.getHead(), snek.getSnakeDir());

        gc.setFill(Color.BLACK);
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * cellSize, point.y * cellSize, cellSize, cellSize);
        }
    }

    /** Draws the active coin when the board has a free position for it. */
    private void drawCoin(Coin coin) {
        if (coin == null) {
            return;
        }
        Point position = coin.getPos();
        double coinSize = cellSize - 3;
        gc.drawImage(coinSprites.get(coin.getType()), position.x * cellSize, position.y * cellSize, coinSize, coinSize);
    }

    private void drawBody(List<Point> body, int index, int heading) {
        Point point = body.get(index);
        if (index == 0) {
            drawSprite(tailSprite, point, body.size() == 1 ? heading : tailAngle(point, body.get(1)));
            turnArray[point.x][point.y] = 0;
            return;
        }

        int turn = turnArray[point.x][point.y];
        if (turn != 0) {
            drawSprite(turnSprite, point, 90 * turn);
        } else {
            drawSprite(bodySprite, point, point.y == body.get(index - 1).y ? 270 : 0);
        }
    }

    private void drawSprite(ImageView sprite, Point point, int angle) {
        sprite.setRotate(angle);
        Image rotated = sprite.snapshot(spriteSnapshot, null);
        gc.drawImage(rotated, point.x * cellSize, point.y * cellSize, cellSize, cellSize);
    }

    /** Finds the existing tail orientation from the adjacent body segment. */
    static int tailAngle(Point tail, Point next) {
        if (next.x == tail.x && next.y == tail.y - 1) {
            return 0;
        }
        if (next.x == tail.x && next.y == tail.y + 1) {
            return 180;
        }
        return next.x == tail.x - 1 && next.y == tail.y ? 270 : 90;
    }

    /** Records an accepted turn at the current head cell. */
    public void recordTurn(Point position, KeyCode previous, KeyCode next) {
        turnArray[position.x][position.y] = turnQuarterTurns(previous, next);
    }

    /** Clears visual turn markers from the previous round. */
    public void clearTurns() {
        for (int[] column : turnArray) {
            Arrays.fill(column, 0);
        }
    }

    /** Maps the existing turn artwork to quarter turns. */
    static int turnQuarterTurns(KeyCode previous, KeyCode next) {
        return switch (previous) {
            case LEFT -> next == KeyCode.UP ? 2 : next == KeyCode.DOWN ? 3 : 0;
            case RIGHT -> next == KeyCode.UP ? 1 : next == KeyCode.DOWN ? 4 : 0;
            case UP -> next == KeyCode.LEFT ? 4 : next == KeyCode.RIGHT ? 3 : 0;
            case DOWN -> next == KeyCode.LEFT ? 1 : next == KeyCode.RIGHT ? 2 : 0;
            default -> 0;
        };
    }

    /** Returns the existing sprite file for a coin type. */
    static String coinSpriteName(CoinType type) {
        return switch (type) {
            case REGULAR -> "redcoin.png";
            case GOLD -> "goldcoin.png";
            case RAINBOW -> "rainbowcoin.png";
            case BLUE -> "bluecoin.png";
            case SILVER -> "silvercoin.png";
        };
    }

    private static Image loadSprite(String name) {
        return new Image(GameView.class.getResource(SPRITE_PATH + name).toExternalForm());
    }

    /** Displays the score and outcomes already calculated by gameplay. */
    public void updateStatus(int score, boolean won, boolean dead) {
        scoreLabel.setText("Score: " + score);
        gameWonLabel.setVisible(won);
        gameOverLabel.setVisible(dead);
    }

}
