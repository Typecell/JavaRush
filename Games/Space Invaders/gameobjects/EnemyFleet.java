package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public EnemyFleet() {
        createShips();
    }

    public void draw(Game game) {
        for (EnemyShip e : ships)
            e.draw(game);
    }

    private double getSpeed() {
        double a = 2.0;
        double b = 3.0 / ships.size();
        return a >= b ? b : a;
    }

    private void createShips() {
        ships = new ArrayList<>();
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j <  COLUMNS_COUNT; j++) {
                ships.add(new EnemyShip(j * STEP, i * STEP + 12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    private double getLeftBorder() {
        double x = 64;
        for (EnemyShip e : ships) {
            if (e.x < x)
                x = e.x;
        }
        return x;
    }

    public double getBottomBorder() {
        double max = 0;
        for (EnemyShip ship : ships) {
            if (ship.y + ship.height > max)
                max = ship.y + ship.height;
        }
        return max;
    }

    public int getShipsCount() {
        return ships.size();
    }

    private double getRightBorder() {
        double x = 0;
        for (EnemyShip e : ships) {
            if (e.x + e.width > x) {
                x = e.x + e.width;
            }
        }
        return x;
    }

    public int verifyHit(List<Bullet> bullets) {
        if (bullets.isEmpty()) return 0;
        int score = 0;
        for (int i = 0; i < ships.size(); i++) {
            EnemyShip ship = ships.get(i);
            for (int j = 0; j < bullets.size(); j++) {
                Bullet bullet = bullets.get(j);
                if (ship.isCollision(bullet) && ship.isAlive && bullet.isAlive) {
                    ship.kill();
                    score += ship.score;
                    bullet.kill();
                }
            }
        }
        return score;
    }

    public void deleteHiddenShips() {
        ships.removeIf(ship -> !ship.isVisible());
    }

    public void move() {
        if (!ships.isEmpty()) {
            if (direction == Direction.LEFT && getLeftBorder() < 0) {
                direction = Direction.RIGHT;
                for (EnemyShip e : ships) {
                    e.move(Direction.DOWN, getSpeed());
                }
                return;
            }
            if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
                direction = Direction.LEFT;
                for (EnemyShip e : ships) {
                    e.move(Direction.DOWN, getSpeed());
                }
                return;
            }
            for (EnemyShip e : ships) {
                e.move(direction, getSpeed());
            }
        }
    }

    public Bullet fire(Game game) {
        int number = game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY);

        if (ships.isEmpty() || number > 0)
            return null;

        int number2 = game.getRandomNumber(ships.size());
        return ships.get(number2).fire();
    }
}
