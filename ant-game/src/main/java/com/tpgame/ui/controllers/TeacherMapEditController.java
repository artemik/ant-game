package com.tpgame.ui.controllers;

import com.google.common.eventbus.Subscribe;
import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.cells.*;
import com.tpgame.ui.views.Views;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class TeacherMapEditController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    public static final String MAP_PARAM = "map";

    private final static int MAX_MAP_SIZE = 15;

    private KeyCode lastKeyPressed;

    private MapViewerController mapViewer;
    private GameMap map;
    private boolean changesBeenMade;

    @FXML
    private GridPane gridPane;
    @FXML
    private ListView<Tool> toolsList;
    @FXML
    private ComboBox<Integer> widthComboBox;
    @FXML
    private ComboBox<Integer> heightComboBox;


    @Override
    public void init(Map params) {
        map = (GameMap) params.get(MAP_PARAM);
        prepareCubeCell();
        prepareSizeComboBoxes();
        prepareTools();
        prepareMapViewer();
    }

    private void prepareCubeCell() {
        lastKeyPressed = KeyCode.F;
        gridPane.setOnKeyPressed(event -> {
            lastKeyPressed = event.getCode();
        });
    }

    private void prepareSizeComboBoxes() {
        for (int i = 3; i <= MAX_MAP_SIZE; i++) {
            widthComboBox.getItems().add(i);
            heightComboBox.getItems().add(i);
        }
        Integer mapWidth = map.getWidth();
        widthComboBox.getSelectionModel().select(mapWidth);
        widthComboBox.setOnAction(event -> {
            updateMapSize();
        });
        Integer mapHeight = map.getHeight();
        heightComboBox.getSelectionModel().select(mapHeight);
        heightComboBox.setOnAction(event -> {
            updateMapSize();
        });
    }

    private void updateMapSize() {
        Integer width = widthComboBox.getSelectionModel().getSelectedItem();
        Integer height = heightComboBox.getSelectionModel().getSelectedItem();
        map.initNewSize(width, height);
        mapViewer.reDraw();
    }

    private void prepareTools() {
        toolsList.getItems().add(new AntTool());
        toolsList.getItems().add(new CubeTool());
        toolsList.getItems().add(new HoleTool());
        toolsList.getItems().add(new OccupiedTool());
        toolsList.getItems().add(new EraserTool());

        toolsList.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
            Platform.runLater(() -> {
                if (newValue1 != null && !newValue1.isAvailable()) {
                    toolsList.getSelectionModel().select(oldValue1);
                }
            });
        });

    }

    protected void prepareMapViewer() {
        Map<String, GameMap> params = Collections.singletonMap(MapViewerController.MAP_PARAM, map);
        MapViewerController mapViewerController = loadInnerControllableNode(Views.MAP_VIEWER_INNER_VIEW, params);
        mapViewer = mapViewerController;
        gridPane.getChildren().add(mapViewerController.getRoot());
    }


    // PROCESSORS
    @Subscribe
    public void cellClicked(MapViewerController.CellClicked cellClicked) {
        Platform.runLater(() -> {
            Tool selectedTool = toolsList.getSelectionModel().getSelectedItem();
            if (selectedTool != null) {
                selectedTool.react(cellClicked.cell, cellClicked.xIdx, cellClicked.yIdx);
            }

            updateToolsAvailability();
            changesBeenMade = true;
        });
        mapViewer.reDraw();
    }

    public boolean isChangesBeenMade() {
        return changesBeenMade;
    }

    public void setChangesBeenMade(boolean changesBeenMade) {
        this.changesBeenMade = changesBeenMade;
    }

    protected void updateToolsAvailability() {
        Tool selectedItem = toolsList.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isAvailable()) {
            toolsList.getSelectionModel().select(null);
        }
    }

    public Image getSnapshot() {
        return mapViewer.getSnapshot();
    }

    private abstract class Tool {
        private String name;

        private Tool(String name) {
            this.name = name;
        }

        public abstract void react(Cell clickedCell, int x, int y);

        public abstract boolean isAvailable();

        @Override
        public String toString() {
            return name;
        }
    }

    private class AntTool extends Tool {
        private AntTool() {
            super("Ant");
        }

        @Override
        public void react(Cell clickedCell, int x, int y) {
            if (!map.isReachedLimit(AntCell.class)) {
                map.setCell(new AntCell(), x, y);
            }
        }

        @Override
        public boolean isAvailable() {
            return !map.isReachedLimit(AntCell.class);
        }
    }

    private class CubeTool extends Tool {
        private CubeTool() {
            super("Cube");
        }

        @Override
        public void react(Cell clickedCell, int x, int y) {
            if (!map.isReachedLimit(CubeCell.class)) {
                Character rusChar = getLastCyrillicChar();
                map.setCell(new CubeCell(rusChar), x, y);
            }
        }

        protected Character getLastCyrillicChar() {
            Character rusChar;
            switch (lastKeyPressed) {
                case F:
                    rusChar = 'À';
                    break;
                case COMMA:
                    rusChar = 'Á';
                    break;
                case D:
                    rusChar = 'Â';
                    break;
                case U:
                    rusChar = 'Ã';
                    break;
                case L:
                    rusChar = 'Ä';
                    break;
                case T:
                    rusChar = 'Å';
                    break;
                case SEMICOLON:
                    rusChar = 'Æ';
                    break;
                case P:
                    rusChar = 'Ç';
                    break;
                case B:
                    rusChar = 'È';
                    break;
                case R:
                    rusChar = 'Ê';
                    break;
                case K:
                    rusChar = 'Ë';
                    break;
                case V:
                    rusChar = 'Ì';
                    break;
                case Y:
                    rusChar = 'Í';
                    break;
                case J:
                    rusChar = 'Î';
                    break;
                case G:
                    rusChar = 'Ï';
                    break;
                case H:
                    rusChar = 'Ð';
                    break;
                case C:
                    rusChar = 'Ñ';
                    break;
                case N:
                    rusChar = 'Ò';
                    break;
                case E:
                    rusChar = 'Ó';
                    break;
                case A:
                    rusChar = 'Ô';
                    break;
                case OPEN_BRACKET:
                    rusChar = 'Õ';
                    break;
                case W:
                    rusChar = 'Ö';
                    break;
                case X:
                    rusChar = '×';
                    break;
                case I:
                    rusChar = 'Ø';
                    break;
                case O:
                    rusChar = 'Ù';
                    break;
                case S:
                    rusChar = 'Û';
                    break;
                case QUOTE:
                    rusChar = 'Ý';
                    break;
                case PERIOD:
                    rusChar = 'Þ';
                    break;
                case Z:
                    rusChar = 'ß';
                    break;
                default:
                    rusChar = 'À';
                    break;
            }
            log.info("rus char: " + rusChar);
            return rusChar;
        }

        @Override
        public boolean isAvailable() {
            return !map.isReachedLimit(CubeCell.class);
        }
    }

    private class HoleTool extends Tool {
        private HoleTool() {
            super("Hole");
        }

        @Override
        public void react(Cell clickedCell, int x, int y) {
            if (!map.isReachedLimit(HoleCell.class)) {
                map.setCell(new HoleCell(), x, y);
            }
        }

        @Override
        public boolean isAvailable() {
            return !map.isReachedLimit(HoleCell.class);
        }
    }

    private class OccupiedTool extends Tool {
        private OccupiedTool() {
            super("Occupied Cell");
        }

        @Override
        public void react(Cell clickedCell, int x, int y) {
            if (!map.isReachedLimit(OccupiedCell.class)) {
                map.setCell(new OccupiedCell(), x, y);
            }
        }

        @Override
        public boolean isAvailable() {
            return !map.isReachedLimit(OccupiedCell.class);
        }
    }

    private class EraserTool extends Tool {
        private EraserTool() {
            super("Eraser");
        }

        @Override
        public void react(Cell clickedCell, int x, int y) {
            map.setCell(null, x, y);
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}

