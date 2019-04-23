package com.javarush.games.snake;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<GameObject> snakeParts = new ArrayList<>();
    private static final String HEAD_SIGN ="\uD83D\uDC7E";
    private static final String BODY_SIGN = "\u26AB";
    public boolean isAlive = true;
    private Direction direction = Direction.LEFT;

//    public void move() {
//        GameObject newHead = createNewHead();
//
//        if (newHead.x < 0 || newHead.x >= SnakeGame.WIDTH || newHead.y < 0 || newHead.y >= SnakeGame.HEIGHT) {
//            isAlive = false;
//            return;
//        }
//
//        snakeParts.add(0, newHead);
//        removeTail();
//    }

    public void move(Apple apple) {
        GameObject newHead = createNewHead();

        if (checkCollision(newHead)) {
            isAlive = false;
            return;
        }

        if (newHead.x == apple.x && newHead.y == apple.y) {
            apple.isAlive = false;
            snakeParts.add(0, newHead);
        }
        else {
            snakeParts.add(0, newHead);
            removeTail();
        }
    }

    public int getLength() {
        return snakeParts.size();
    }

    public GameObject createNewHead() {
        GameObject head = snakeParts.get(0);

        switch (direction) {
            case LEFT: return new GameObject(head.x - 1, head.y);
            case RIGHT: return new GameObject(head.x + 1, head.y);
            case UP: return new GameObject(head.x, head.y - 1);
            case DOWN: return new GameObject(head.x, head.y + 1);
        }

        return null;
    }

    public void removeTail() {
        snakeParts.remove(snakeParts.get(snakeParts.size() - 1));
    }

    public Snake(int x, int y) {
        snakeParts.add(new GameObject(x, y));
        snakeParts.add(new GameObject(x + 1, y));
        snakeParts.add(new GameObject(x + 2, y));
    }

    public void draw(Game game) {
        for (int i = 0; i < snakeParts.size(); i++) {
            GameObject part = snakeParts.get(i);

            if (isAlive) {
                if (i == 0) {
                    game.setCellValueEx(part.x, part.y, Color.NONE, HEAD_SIGN, Color.BLACK, 75);
                }
                else {
                    game.setCellValueEx(part.x, part.y, Color.NONE, BODY_SIGN, Color.BLACK, 75);
                }
            }
            else {
                if (i == 0) {
                    game.setCellValueEx(part.x, part.y, Color.NONE, HEAD_SIGN, Color.RED, 75);
                }
                else {
                    game.setCellValueEx(part.x, part.y, Color.NONE, BODY_SIGN, Color.RED, 75);
                }
            }
        }
    }

    public void setDirection(Direction direction) {
        GameObject head = snakeParts.get(0);
        GameObject neck = snakeParts.get(1);

        if (direction.equals(Direction.LEFT)) {
            if (head.y == neck.y)
                return;

            switch (this.direction) {
                case UP: this.direction = direction; break;
                case DOWN: this.direction = direction; break;
                default: break;
            }
        }
        else if (direction.equals(Direction.RIGHT)) {
            if (head.y == neck.y)
                return;

            switch (this.direction) {
                case UP: this.direction = direction; break;
                case DOWN: this.direction = direction; break;
                default: break;
            }
        }
        else if (direction.equals(Direction.DOWN)) {
            if (head.x == neck.x)
                return;

            switch (this.direction) {
                case RIGHT: this.direction = direction; break;
                case LEFT: this.direction = direction; break;
                default: break;
            }
        }
        else if (direction.equals(Direction.UP)) {
            if (head.x == neck.x)
                return;

            switch (this.direction) {
                case RIGHT: this.direction = direction; break;
                case LEFT: this.direction = direction; break;
                default: break;
            }
        }
    }

    public boolean checkCollision(GameObject object) {
        for (GameObject g : snakeParts) {
            if (object.x == g.x && object.y == g.y) {
                return true;
            }
        }
        return false;
    }
}
