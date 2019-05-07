package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.Bullet;
import com.javarush.games.spaceinvaders.gameobjects.EnemyFleet;
import com.javarush.games.spaceinvaders.gameobjects.PlayerShip;
import com.javarush.games.spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpaceInvadersGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int COMPLEXITY = 5;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    private List<Bullet> enemyBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private List<Bullet> playerBullets;
    private static final int PLAYER_BULLETS_MAX = 1;
    private int score;

    private void createStars() {
        stars = new ArrayList<>();
        Random random = new Random(63);
        for (int i = 0; i < 8; i++) {
            stars.add(new Star(random.nextDouble(), random.nextDouble()));
        }
    }

    private void stopGame(boolean isWin) {
        isGameStopped = true;
        stopTurnTimer();
        if (isWin) {
            showMessageDialog(Color.WHITE, "YOU WIN", Color.GREEN, 25);
        }
        else {
            showMessageDialog(Color.WHITE, "YOU LOSE", Color.RED, 25);
        }
    }

    private void stopGameWithDelay() {
        animationsCount++;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }

    @Override
    public void onTurn(int step) {
        moveSpaceObjects();
        check();
        Bullet bullet = enemyFleet.fire(this);
        if (bullet != null)
            enemyBullets.add(bullet);
        setScore(score);
        drawScene();

    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.LEFT && playerShip.getDirection() == Direction.LEFT)
            playerShip.setDirection(Direction.UP);
        if (key == Key.RIGHT && playerShip.getDirection() == Direction.RIGHT)
            playerShip.setDirection(Direction.UP);
    }

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void removeDeadBullets() {
        enemyBullets.removeIf(bullet -> !bullet.isAlive || bullet.y >= HEIGHT - 1);
        playerBullets.removeIf(bullet -> bullet.y + bullet.height < 0 || !bullet.isAlive);
//        Iterator<Bullet> iterator = enemyBullets.iterator();
//        while (iterator.hasNext()) {
//            Bullet bullet = iterator.next();
//            if (!bullet.isAlive || bullet.y >= HEIGHT - 1)
//                iterator.remove();
//        }
    }

    @Override
    public void setCellValueEx(int x, int y, Color cellColor, String value) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            super.setCellValueEx(x, y, cellColor, value);
        }
    }

    private void check() {
        playerShip.verifyHit(enemyBullets);
        enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        double border = enemyFleet.getBottomBorder();
        if (border >= playerShip.y) {
            playerShip.kill();
        }
        int ships = enemyFleet.getShipsCount();
        if (ships == 0) {
            playerShip.win();
            stopGameWithDelay();
        }
        removeDeadBullets();
        if (!playerShip.isAlive)
            stopGameWithDelay();
        score += enemyFleet.verifyHit(playerBullets);
    }

    private void createGame() {
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<>();
        playerShip = new PlayerShip();
        isGameStopped = false;
        animationsCount = 0;
        playerBullets = new ArrayList<>();
        score = 0;
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene() {
        drawField();
        for (Bullet b : enemyBullets)
            b.draw(this);
        for (Bullet b : playerBullets)
            b.draw(this);
        enemyFleet.draw(this);
        playerShip.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                setCellValueEx(j, i, Color.BLACK, "");
            }
        }
        for (Star s : stars) {
            s.draw(this);
        }
    }

    private void moveSpaceObjects() {
        enemyFleet.move();
        for (Bullet b : enemyBullets)
            b.move();
        for (Bullet b : playerBullets)
            b.move();
        playerShip.move();
    }

    @Override
    public void onKeyPress(Key key) {
        switch (key) {
            case SPACE: if (isGameStopped) createGame(); else { Bullet bullet = playerShip.fire(); if (bullet != null && playerBullets.size() < PLAYER_BULLETS_MAX) playerBullets.add(bullet); } break;
            case LEFT: playerShip.setDirection(Direction.LEFT); break;
            case RIGHT: playerShip.setDirection(Direction.RIGHT); break;
        }
    }
}
