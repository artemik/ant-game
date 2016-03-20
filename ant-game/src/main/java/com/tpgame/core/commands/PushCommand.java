package com.tpgame.core.commands;

import com.tpgame.core.entities.GameMap;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class PushCommand extends Command {
    private static final long serialVersionUID = 3074523154900893273L;

    private String direction;

    public PushCommand(Type direction) {
        this.direction = direction.toValue();
    }

    @Override
    public CommandExecutionResult execute(GameMap map) {
        Pair<Integer, Integer> antPos = map.getAntCell();
        int antX = antPos.getLeft();
        int antY = antPos.getRight();
        int newAntX = antX;
        int newAntY = antY;
        int newCubePositionX = antX;
        int newCubePositionY = antY;

        switch (getDirection()) {
            case LEFT:
                newAntX--;
                newCubePositionX -= 2;
                break;
            case RIGHT:
                newAntX++;
                newCubePositionX += 2;
                break;
            case UP:
                newAntY--;
                newCubePositionY -= 2;
                break;
            case DOWN:
                newAntY++;
                newCubePositionY += 2;
                break;
        }

        boolean result = false;
        String message = null;

        if (!isInBounds(newAntX, newAntY, map)) {
            message = "Ant is out of map bounds";
        } else if (!isCube(newAntX, newAntY, map)) {
            message = "Ant should push a cube";
        } else if (!isInBounds(newCubePositionX, newCubePositionY, map)) {
            message = "Cube is out of bounds";
        } else if (!(isFree(newCubePositionX, newCubePositionY, map) || isHole(newCubePositionX, newCubePositionY, map))) {
            message = "Cube can be pushed onto a free cell or a hole only";
        } else {
            result = true;
            if (!isHole(newCubePositionX, newCubePositionY, map)) {
                map.moveCell(newAntX, newAntY, newCubePositionX, newCubePositionY);
            } else {
                map.clearCell(newCubePositionX, newCubePositionY);
            }
            map.moveCell(antX, antY, newAntX, newAntY);
        }
        return new CommandExecutionResult(result, message);
    }

    public enum Type {
        LEFT("LEFT"), RIGHT("RIGHT"), UP("UP"), DOWN("DOWN");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public static Type fromValue(String value) {
            if (value != null) {
                for (Type color : values()) {
                    if (color.value.equals(value)) {
                        return color;
                    }
                }
            }

            return getDefault();
        }

        public String toValue() {
            return value;
        }

        public static Type getDefault() {
            return null;
        }
    }

    public Type getDirection() {
        return Type.fromValue(direction);
    }
}
