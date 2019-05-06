package com.javarush.games.racer.road;

import com.javarush.engine.cell.Game;
import com.javarush.games.racer.GameObject;
import com.javarush.games.racer.PlayerCar;
import com.javarush.games.racer.RacerGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private List<RoadObject> items = new ArrayList<>();
    private static final int PLAYER_CAR_DISTANCE = 12;
    private int passedCarsCount = 0;

    public int getPassedCarsCount() {
        return passedCarsCount;
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for (RoadObject r : items) {
            if (r.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE))
                return false;
        }
        return true;
    }

    private boolean isMovingCarExists() {
        for (RoadObject r : items) {
            if (r instanceof MovingCar)
                return true;
        }
        return false;
    }

    private void generateMovingCar(Game game) {
        int number = game.getRandomNumber(100);
        if (number < 10 && !isMovingCarExists()) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    private void generateRegularCar(Game game) {
        int number = game.getRandomNumber(100);
        int carTypeNumber = game.getRandomNumber(4);
        if (number < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        }
        if (!type.equals(RoadObjectType.THORN)) {
            return new Car(type, x, y);
        }
        else
            return new Thorn(x, y);
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject object = createRoadObject(type, x, y);
        if (object != null && isRoadSpaceFree(object))
            items.add(object);
    }

    public void draw(Game game) {
        for (RoadObject r : items) {
            r.draw(game);
        }
    }

    public void move(int boost) {
        for (RoadObject r : items) {
            r.move(boost + r.speed, items);
        }
        deletePassedItems();
    }

    private boolean isThornExists() {
        for (RoadObject r : items) {
            if (r.type.equals(RoadObjectType.THORN))
                return true;
        }
        return false;
    }

    private void generateThorn(Game game) {
        int number = game.getRandomNumber(100);
        if (number < 10 && !isThornExists()) {
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        Iterator<RoadObject> iterator = items.iterator();
        while (iterator.hasNext()) {
            RoadObject item = iterator.next();
            if (item.type != RoadObjectType.THORN && item.y >= RacerGame.HEIGHT) {
                iterator.remove();
                passedCarsCount++;
            }
            else if (item.type == RoadObjectType.THORN && item.y >= RacerGame.HEIGHT) {
                iterator.remove();
            }
        }
    }

    public boolean checkCrush(PlayerCar car) {
        for (GameObject g : items) {
            if (g.isCollision(car))
                return true;
        }
        return false;
    }
}
