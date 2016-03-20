package com.tpgame.ui.controllers;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.cells.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class MapViewerController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    public static final String MAP_PARAM = "map";

    private static final Color BACK_COLOR = new Color(0.49, 0.64, 0.92, 1);
    private static final Color MAP_COLOR = new Color(0.39, 0.58, 0.92, 1);

    private static final int CELL_SIZE = 39;

    private GameMap gameMap;
    private int mapWidth;
    private int mapHeight;

    @FXML
    private Canvas canvas;
    @FXML
    private BorderPane borderPane;


    @Override
    public void init(Map params) {
        gameMap = (GameMap) params.get(MAP_PARAM);
        initBorderPane();
        initCanvas();
        reDraw();
    }

    private void initBorderPane() {
        borderPane.setBackground(new Background(new BackgroundFill(BACK_COLOR, null, null)));
    }

    private void initCanvas() {
        canvas.setOnMouseClicked(e -> {
            CellClicked event = new CellClicked();
            event.xIdx = getIdxX(e.getX());
            event.yIdx = getIdxY(e.getY());
            event.cell = getCell(e.getX(), e.getY());
            eventsManager.post(event);
        });
    }

    public Image getSnapshot() {
        WritableImage wim = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, wim);
        return wim;
    }

    public void reDraw() {
        Platform.runLater(() -> {
            drawBackground();
            drawObjects();
        });
    }

    private void drawObjects() {
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Cell cell = gameMap.getCell(x, y);
                if (cell != null) {
                    drawCell(cell.getImage(), x, y);
                }
            }
        }
    }

    private void drawBackground() {
        mapWidth = CELL_SIZE * gameMap.getWidth();
        mapHeight = CELL_SIZE * gameMap.getHeight();
        canvas.setWidth(mapWidth + 2);
        canvas.setHeight(mapHeight + 2);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(MAP_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.GHOSTWHITE);
        gc.setLineDashes(9.75);
        gc.setLineDashOffset(4);
        gc.setLineWidth(1);
        gc.setLineCap(StrokeLineCap.ROUND);

        double endY = getStartY() + mapHeight;
        for (int i = 0; i <= GameMap.MAX_WIDTH; i++) {
            double x = i * CELL_SIZE + getStartX();
            gc.strokeLine(x, getStartY(), x, endY);
        }

        double endX = getStartX() + mapWidth;
        for (int i = 0; i <= GameMap.MAX_WIDTH; i++) {
            double y = i * CELL_SIZE + getStartY();
            gc.strokeLine(getStartX(), y, endX, y);
        }
    }

    private void drawCell(Image image, int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double canvasX = getStartX() + x * CELL_SIZE;
        double canvasY = getStartY() + y * CELL_SIZE;
        gc.drawImage(image, canvasX, canvasY);
    }

    private double getStartX() {
        return 1;
    }

    private double getStartY() {
        return 1;
    }

    private Cell getCell(double canvasX, double canvasY) {
        return gameMap.getCell(getIdxX(canvasX), getIdxY(canvasY));
    }

    private int getIdxX(double canvasX) {
        return (int) ((canvasX - getStartX()) / CELL_SIZE);
    }

    private int getIdxY(double canvasY) {
        return (int) ((canvasY - getStartX()) / CELL_SIZE);
    }

    // EVENTS
    public static class CellClicked {
        public int xIdx;
        public int yIdx;
        public Cell cell;
    }
}


