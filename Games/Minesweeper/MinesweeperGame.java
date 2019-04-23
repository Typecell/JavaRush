package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(0);
        createGame();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.LIMEGREEN, "You win!", Color.BLACK, 25);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.FIREBRICK, "Game Over!", Color.BLACK, 25);
    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject currentCell = gameField[y][x];

            if (countFlags > 0 && !currentCell.isOpen) {
                if (!currentCell.isFlag) {
                    currentCell.isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                }
                else {
                    currentCell.isFlag = false;
                    countFlags++;
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.ORANGE);
                }
            }
        }
    }

    private void openTile(int x, int y) {

        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.GREEN);

            if (!gameField[y][x].isMine) {
                score += 5;
                setScore(score);

                if (countClosedTiles == countMinesOnField) {
                    win();
                    return;
                }

                if (gameField[y][x].countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    ArrayList<GameObject> list = getNeighbors(gameField[y][x]);
                    for (GameObject g : list) {
                        if (!g.isOpen) {
                            openTile(g.x, g.y);
                        }
                    }
                }
                else
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
            else {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
        }
    }

    private void createGame() {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColor(i, j, Color.ORANGE);
                setCellValue(i, j, "");
                if (getRandomNumber(10) == 5) {
                    gameField[i][j] = new GameObject(j, i, true);
                    countMinesOnField++;
                }
                else
                    gameField[i][j] = new GameObject(j, i, false);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void countMineNeighbors() {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[i][j].isMine) {
                    int count = 0;
                    ArrayList<GameObject> list = getNeighbors(gameField[i][j]);
                    for (GameObject g : list) {
                        if (g.isMine)
                            count++;
                    }
                    gameField[i][j].countMineNeighbors = count;
                }
            }
        }
    }

    private ArrayList<GameObject> getNeighbors(GameObject object) {
        ArrayList<GameObject> list = new ArrayList<>();
        int borderY = object.y + 2;
        int borderX = object.x + 2;

        if (borderY > SIDE)
            borderY = SIDE;
        if (borderX > SIDE)
            borderX = SIDE;

        for (int i = object.y-1; i < borderY; i++) {
            if (i < 0)
                i = 0;
            for (int j = object.x-1; j < borderX; j++) {
                if (j < 0)
                    j = 0;
                if (object.x == j && object.y == i)
                    continue;
                list.add(gameField[i][j]);
            }
        }

        return list;
    }

    public static void main(String[] args) {
        new MinesweeperGame().initialize();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        }
        else
            openTile(x, y);
    }

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
}
