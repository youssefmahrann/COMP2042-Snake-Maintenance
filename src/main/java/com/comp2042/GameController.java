package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

public class GameController {
    private Snake snek;
    private Coin coin;
    private GameView gameView;
    private Game game;
    private Stage stage;
    private String coinSoundPath = "resources/views/res/coinSound.wav";
    private Obstacle obstacle;

    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed = false;

    private int legacyScoreMultiplier = 2;

    private void calculateBonusScore() {
        int bonus = game.getScorePoints() * legacyScoreMultiplier;
        System.out.println("Bonus score: " + bonus);
    }

    public GameController(Snake snek, Coin coin, Game game, GameView gameView, Obstacle obstacle, Stage stage) {
        this.snek = snek;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;

        this.obstacle = obstacle;
        if (coin == null) {
            game.markBoardFull();
        }
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    private final Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> tick()));

    private void tick() {
        if (snek.getDead() || game.gameWon()) {
            return;
        } else {

            if (key.equals(KeyCode.LEFT)) {
                snek.move('L');
            } else if (key.equals(KeyCode.RIGHT)) {
                snek.move('R');
            } else if (key.equals(KeyCode.UP)) {
                snek.move('U');
            } else if (key.equals(KeyCode.DOWN)) {
                snek.move('D');
            }

            if (!snek.getDead() && snek.ateCoin(coin)) {
                int points = coin.getPoints();
                powerCoin();
                game.recordCollection(points, this::newCoin);
                if (game.gameWon()) {
                    coin = null;
                    animation.stop();
                }
            }

            keyPressed = false;

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
        }
    }

    public void handle(KeyEvent event) {
        if (event.getCode().isArrowKey() && !game.gameWon()) animation.play();

        if (!game.gameWon() && valKey(event.getCode()) && !keyPressed) {
            lastKey = key;
            key = event.getCode();
            keyPressed = true;
            gameView.turnDir(snek);
        }

        if (event.getCode().equals(KeyCode.P)) {
            animation.stop();
        }

        if (event.getCode().equals(KeyCode.TAB)) {
            reset();
            gameView.scoreLabel.setText("Score: " + game.getScorePoints());
        }

        if (event.getCode().equals(KeyCode.PLUS)) {
            gameView.scaleToFullscreen(true, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

        if (event.getCode().equals(KeyCode.MINUS)) {
            gameView.scaleToFullscreen(false, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        if (event.getCode().equals(KeyCode.ESCAPE)) {
            MainMenuController mainMenuController = new MainMenuController();
            mainMenuController.getViewManager().getPrimaryStage().show();
            mainMenuController.getViewManager().getGameStage().close();
        }
    }

    private boolean valKey(KeyCode requested) {
    return DirectionValidator.isValid(key, requested);
}

    private void reset() {

        animation.jumpTo(Duration.ZERO);
        animation.stop();

        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        obstacle.reset();
        game.resetScore();
        coin = Coin.spawn(CoinType.REGULAR, game, snek, obstacle).orElse(null);
        if (coin == null) {
            game.markBoardFull();
        }

        animation.setRate(game.getSpeed());
        key = KeyCode.LEFT;
        keyPressed = false;

        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();

        gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
    }

    private void powerCoin() {
        animation.setRate(game.getSpeed());
        snek.ignoreBorders(false);
        if (coin.getClass().equals(RainbowCoin.class)) {
            RainbowCoin tempCoin = (RainbowCoin) coin;
            animation.setRate(game.getSpeed() + tempCoin.getSpeedBonus());
        } else if (coin.getClass().equals(SilverCoin.class)){
            Random rnd = new Random();
            int requested = rnd.nextInt(Math.max(1, game.getWidth() * game.getHeight() / 20)) + 3;
            int placed = obstacle.newObstacles(snek, coin, requested);
            game.decMaxScore(placed);
        } else if (coin.getClass().equals(BlueCoin.class)) {
            snek.ignoreBorders(true);
        }
    }

    /** Creates a replacement coin if the board still has room. */
    private boolean newCoin() {
        boolean powerUpsEnabled = game.isPowerUp();
        int roll = powerUpsEnabled ? new Random().nextInt(CoinType.ROLL_COUNT) : 0;
        CoinType type = CoinType.select(roll, powerUpsEnabled, !obstacle.getObstacles().isEmpty());
        coin = Coin.spawn(type, game, snek, obstacle).orElse(null);
        return coin != null;
    }
}
