package com.javarush.task.task35.task3513;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    public int score;
    public int maxTile;
    private Stack previousStates = new Stack();
    private Stack previousScores = new Stack();
    private boolean isSaveNeeded = true;

    public Model() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        resetGameTiles();
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty())
                    list.add(gameTiles[i][j]);
            }
        }
        return list;
    }

    public void resetGameTiles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        if (!getEmptyTiles().isEmpty()) {
            int weight = Math.random() < 0.9 ? 2 : 4;
            int random = (int) (getEmptyTiles().size() * Math.random());
            getEmptyTiles().get(random).value = weight;
        }
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean res = false;
        for (int i = 0; i < tiles.length; i++) {
            Tile tempTile;
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[j].isEmpty()) {
                    if (j == tiles.length-1)
                        break;
                    if (!tiles[j+1].isEmpty()) {
                        tempTile = tiles[j+1];
                        tiles[j+1] = tiles[j];
                        tiles[j] = tempTile;
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean res = false;
        for (int j = 0; j < tiles.length; j++) {
            if (j == tiles.length-1)
                break;
            if (tiles[j].value == tiles[j+1].value) {
                tiles[j].value += tiles[j+1].value;
                if (tiles[j].value != 0)
                    res = true;
                if (tiles[j].value > maxTile) {
                    maxTile = tiles[j].value;
                }
                score += tiles[j].value;
                tiles[j+1].value = 0;
                compressTiles(tiles);
            }
        }
        return res;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean res = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) {
                res = true;
            }
        }
        if (res)
            addTile();

        isSaveNeeded = true;
    }

    public void up() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        rotate90();
        left();
        rotate90();
    }

    public void down() {
        saveState(gameTiles);
        rotate90();
        left();
        rotate90();
        rotate90();
        rotate90();
    }

    public void right() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        left();
        rotate90();
        rotate90();
    }

    private void rotate90(){
        for (int i = 0; i < FIELD_WIDTH / 2; i++) {
            for (int j = i; j < FIELD_WIDTH-i-1; j++) {
                Tile temp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[FIELD_WIDTH-1-j][i];
                gameTiles[FIELD_WIDTH-1-j][i] = gameTiles[FIELD_WIDTH-1-i][FIELD_WIDTH-1-j];
                gameTiles[FIELD_WIDTH-1-i][FIELD_WIDTH-1-j] = gameTiles[j][FIELD_WIDTH-1-i];
                gameTiles[j][FIELD_WIDTH-1-i] = temp;
            }
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        boolean res = false;

        if (getEmptyTiles().size() > 0) {
            return true;
        }

        else {
            for (int i = 0; i < FIELD_WIDTH-1; i++) {
                for (int j = 0; j < FIELD_WIDTH-1; j++) {
                    Tile element = gameTiles[i][j];
                    if (element.value == gameTiles[i][j+1].value || element.value == gameTiles[i+1][j].value) {
                        res = true;
                        break;
                    }
                }
            }
        }
        return res;
    }

    private void saveState(Tile[][] tile) {
        tile = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tile[i][j] = new Tile(gameTiles[i][j].value);
            }
        }

        previousStates.push(tile);
        previousScores.push(score);

        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousScores.isEmpty() && !previousStates.isEmpty()) {
            gameTiles = (Tile[][]) previousStates.pop();
            score = (int) previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;

        switch (n) {
            case 0: left(); break;
            case 1: right(); break;
            case 2: up(); break;
            case 3: down(); break;
        }
    }

    public boolean hasBoardChanged() {
        Tile[][] tile = (Tile[][]) previousStates.peek();
        int mainSum = 0;
        int tileSum = 0;

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                mainSum += gameTiles[i][j].value;
                tileSum += tile[i][j].value;
            }
        }

        if (mainSum != tileSum)
            return true;
        else
            return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        int emptyTiles = getEmptyTiles().size();

        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }

        rollback();
        MoveEfficiency previous = new MoveEfficiency(emptyTiles, score, move);
        return previous;
    }

    public void autoMove() {
        PriorityQueue queue = new PriorityQueue(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::left));
        MoveEfficiency efficiency = (MoveEfficiency) queue.peek();
        efficiency.getMove().move();
    }
}
