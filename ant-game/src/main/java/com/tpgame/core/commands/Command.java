package com.tpgame.core.commands;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.cells.Cell;
import com.tpgame.core.entities.cells.CubeCell;
import com.tpgame.core.entities.cells.HoleCell;

import java.io.Serializable;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public abstract class Command implements Serializable {
    private static final long serialVersionUID = -5099027280404025032L;

    public abstract CommandExecutionResult execute(GameMap map);

    protected boolean isInBounds(int x, int y, GameMap map) {
        return !(x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight());
    }

    protected boolean isCube(int x, int y, GameMap map) {
        Cell cell = map.getCell(x, y);
        return cell != null && cell instanceof CubeCell;
    }

    protected boolean isFree(int x, int y, GameMap map) {
        Cell cell = map.getCell(x, y);
        return cell == null;
    }

    protected boolean isHole(int x, int y, GameMap map) {
        Cell cell = map.getCell(x, y);
        return cell != null && cell instanceof HoleCell;
    }
}
