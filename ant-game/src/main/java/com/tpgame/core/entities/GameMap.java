package com.tpgame.core.entities;

import com.tpgame.core.entities.cells.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class GameMap implements Serializable, Cloneable {
    private static final long serialVersionUID = -1577115178829529911L;

    public static final int MIN_WIDTH = 3;
    public static final int MAX_WIDTH = 15;

    public static final double OBJECTS_LIMIT_MULTIPLIER = 0.2;
    private final Map<Class<? extends Cell>, Integer> cellClassToLimit = new HashMap<>();

    private List<List<Cell>> cells;

    private Problem problem;

    public GameMap(final int width, final int height) {
        initNewSize(width, height);

        problem = new Problem();
        problem.setLevel(1);
    }

    public void initNewSize(final int width, final int height) {
        cells = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            ArrayList<Cell> column = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                column.add(null);
            }
            cells.add(column);
        }

        int area = getWidth() * getHeight();
        cellClassToLimit.clear();
        cellClassToLimit.put(AntCell.class, 1);
        cellClassToLimit.put(CubeCell.class, (int) (area * OBJECTS_LIMIT_MULTIPLIER));
        cellClassToLimit.put(HoleCell.class, (int) (area * OBJECTS_LIMIT_MULTIPLIER));
        cellClassToLimit.put(OccupiedCell.class, (int) (area * OBJECTS_LIMIT_MULTIPLIER));
    }

    public List<List<Cell>> getCells() {
        return cells;
    }

    public void setCells(List<List<Cell>> cells) {
        this.cells = cells;
    }

    public int getWidth() {
        return cells.size();
    }

    public int getHeight() {
        return cells.get(0).size();
    }

    public Cell getCell(int x, int y) {
        return cells.get(x).get(y);
    }

    public void moveCell(int x, int y, int newX, int newY) {
        Cell cell = getCell(x, y);
        if (cell instanceof AntCell) {
            handleAntMoveCell(x, y, newX, newY);
        } else {
            setCell(null, x, y);
            setCell(cell, newX, newY);
        }
    }

    private void handleAntMoveCell(int x, int y, int newX, int newY) {
        Cell cell = getCell(x, y);
        Cell destCell = getCell(newX, newY);

        if (cell instanceof AntCellOnOccupied) {
            cell = new OccupiedCell();
        } else {
            cell = null;
        }

        if (destCell instanceof OccupiedCell) {
            destCell = new AntCellOnOccupied();
        } else {
            destCell = new AntCell();
        }

        setCell(cell, x, y);
        setCell(destCell, newX, newY);
    }

    public void setCell(Cell cell, int x, int y) {
        cells.get(x).set(y, cell);
    }

    public void clearCell(int x, int y) {
        setCell(null, x, y);
    }

    public boolean isReachedLimit(Class<? extends Cell> cellClass) {
        int cellNum = getCellNum(cellClass);
        int objectLimit = cellClassToLimit.get(cellClass);
        return cellNum >= objectLimit;
    }

    public int getCellNum(Class<? extends Cell> cellClass) {
        int cnt = 0;
        for (List<Cell> column : cells) {
            for (Cell cell : column) {
                if (cell != null && cellClass.isAssignableFrom(cell.getClass())) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public Pair<Integer, Integer> getAntCell() {
        for (int x = 0; x < cells.size(); x++) {
            for (int y = 0; y < cells.get(x).size(); y++) {
                if (getCell(x, y) instanceof AntCell) {
                    return new ImmutablePair<>(x, y);
                }
            }
        }
        return null;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
