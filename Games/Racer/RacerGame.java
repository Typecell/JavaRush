package com.javarush.games.racer;

import com.javarush.engine.cell.*;
import com.javarush.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private boolean isGameStopped;
    private FinishLine finishLine;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private ProgressBar progressBar;
    private int score;

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU WIN", Color.GREEN, 25);
        stopTurnTimer();
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU LOSE", Color.RED, 25);
        stopTurnTimer();
        player.stop();
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    private void createGame() {
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        setTurnTimer(40);
        isGameStopped = false;
        score = 3500;
    }

    private void drawScene() {
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < HEIGHT; i++) { // Отрисовка разделительной полосы
            setCellColor(CENTER_X, i, Color.WHITE);
        }
        for (int x = ROADSIDE_WIDTH; x < WIDTH - ROADSIDE_WIDTH; x++) { // Отрисовка дороги
            if (x == CENTER_X)
                continue;
            for (int y = 0; y < HEIGHT; y++) {
                setCellColor(x, y, Color.DIMGREY);
            }
        }
        for (int x = 0; x < ROADSIDE_WIDTH; x++) { // Отрисовка левой обочины
            for (int y = 0; y < HEIGHT; y++) {
                setCellColor(x, y, Color.GREEN);
            }
        }
        for (int x = WIDTH - ROADSIDE_WIDTH; x < WIDTH; x++) { // Отрисовка правой обочины
            for (int y = 0; y < HEIGHT; y++) {
                setCellColor(x, y, Color.GREEN);
            }
        }
    }

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        showGrid(false);
        createGame();
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            super.setCellColor(x, y, color);
        }
    }

    @Override
    public void onTurn(int step) {
        if (roadManager.checkCrush(player)) {
            gameOver();
            drawScene();
            return;
        }
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT)
            finishLine.show();
        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }
        score -= 5;
        setScore(score);
        moveAll();
        roadManager.generateNewRoadObjects(this);
        drawScene();
    }

    @Override
    public void onKeyPress(Key key) {
        switch (key) {
            case RIGHT: player.setDirection(Direction.RIGHT); break;
            case LEFT: player.setDirection(Direction.LEFT); break;
            case SPACE: if (isGameStopped) createGame(); break;
            case UP: player.setSpeed(2); break;
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key.equals(Key.RIGHT) && player.getDirection().equals(Direction.RIGHT)) {
            player.setDirection(Direction.NONE);
        }
        if (key.equals(Key.LEFT) && player.getDirection().equals(Direction.LEFT)) {
            player.setDirection(Direction.NONE);
        }
        if (key.equals(Key.UP))
            player.setSpeed(1);
    }
}
