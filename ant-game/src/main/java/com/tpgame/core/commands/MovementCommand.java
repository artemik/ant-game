package com.tpgame.core.commands;

import com.tpgame.core.entities.GameMap;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class MovementCommand extends Command {
    private static final long serialVersionUID = 6448086965159309755L;

    private String direction;

    public MovementCommand(Type direction) {
        this.direction = direction.toValue();
    }

    @Override
    public CommandExecutionResult execute(GameMap map) {
        Pair<Integer, Integer> antPos = map.getAntCell();
        int antX = antPos.getLeft();
        int antY = antPos.getRight();
        int newAntX = antX;
        int newAntY = antY;

        switch (getDirection()) {
            case LEFT:
                newAntX--;
                break;
            case RIGHT:
                newAntX++;
                break;
            case UP:
                newAntY--;
                break;
            case DOWN:
                newAntY++;
                break;
        }

        boolean result = false;
        String message = null;

        if (!isInBounds(newAntX, newAntY, map)) {
            message = "Ant is out of map bounds";
        } else if (isCube(newAntX, newAntY, map)) {
            message = "Ant can't step onto cube";
        } else if (isHole(newAntX, newAntY, map)) {
            message = "Ant fell into hole";
        } else {
            result = true;
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
