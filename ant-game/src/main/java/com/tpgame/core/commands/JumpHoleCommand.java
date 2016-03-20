package com.tpgame.core.commands;

import com.tpgame.core.entities.GameMap;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class JumpHoleCommand extends Command {
    private static final long serialVersionUID = -2325263814377018524L;

    private String direction;

    public JumpHoleCommand(Type direction) {
        this.direction = direction.toValue();
    }

    @Override
    public CommandExecutionResult execute(GameMap map) {
        Pair<Integer, Integer> antPos = map.getAntCell();
        int antX = antPos.getLeft();
        int antY = antPos.getRight();
        int newAntX = antX;
        int newAntY = antY;
        int holePositionX = antX;
        int holePositionY = antY;

        switch (getDirection()) {
            case LEFT:
                newAntX -= 2;
                holePositionX--;
                break;
            case RIGHT:
                newAntX += 2;
                holePositionX++;
                break;
            case UP:
                newAntY -= 2;
                holePositionY--;
                break;
            case DOWN:
                newAntY += 2;
                holePositionY++;
                break;
        }


        boolean result = false;
        String message = null;

        if (!isInBounds(newAntX, newAntY, map)) {
            message = "Ant is out of map bounds";
        } else if (!isHole(holePositionX, holePositionY, map)) {
            message = "Ant can spring over one hole only";
        } else if (isCube(newAntX, newAntY, map)) {
            message = "Ant bumped into cube";
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
