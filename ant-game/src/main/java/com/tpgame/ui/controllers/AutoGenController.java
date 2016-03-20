package com.tpgame.ui.controllers;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.cells.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class AutoGenController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    @FXML
    private ComboBox<String> heightBox;
    @FXML
    private ComboBox<String> widthBox;
    @FXML
    private ComboBox<String> cubesBox;
    @FXML
    private ComboBox<String> holesBox;
    @FXML
    private ComboBox<String> occupiedBox;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button okBtn;


    @Override
    public void init(Map params) {
        prepareStage();
        initFields();
    }

    protected void prepareStage() {
        Stage stage = getStage();
        stage.setTitle("Automatic Map Generation");
        stage.show();
    }

    private void initFields() {
        for (int i = GameMap.MIN_WIDTH; i <= GameMap.MAX_WIDTH; i++) {
            heightBox.getItems().add(String.valueOf(i));
            widthBox.getItems().add(String.valueOf(i));
        }
        heightBox.getSelectionModel().selectLast();
        widthBox.getSelectionModel().selectLast();

        heightBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateBounds();
            }
        });
        widthBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateBounds();
            }
        });

        updateBounds();

        okBtn.setOnAction(event -> {
            generateMap();
        });

        cancelBtn.setOnAction(event -> {
            closeWindow();
        });
    }

    private void generateMap() {
        int height = Integer.parseInt(heightBox.getSelectionModel().getSelectedItem());
        int width = Integer.parseInt(widthBox.getSelectionModel().getSelectedItem());
        int cubes = Integer.parseInt(cubesBox.getSelectionModel().getSelectedItem());
        int holes = Integer.parseInt(holesBox.getSelectionModel().getSelectedItem());
        int occupied = Integer.parseInt(occupiedBox.getSelectionModel().getSelectedItem());

        GameMap map = new GameMap(width, height);

        ArrayList<Cell> allLined = new ArrayList<>();
        String alphabet = "¿¡¬√ƒ≈∆«» ÀÃÕŒœ–—“”‘’÷◊ÿŸ›ﬁﬂ";
        for (int i = 0; i < cubes; i++) {
            int rndAlphabetIdx = new Random().nextInt(alphabet.length());
            allLined.add(new CubeCell(alphabet.charAt(rndAlphabetIdx)));
        }
        for (int i = 0; i < holes; i++) {
            allLined.add(new HoleCell());
        }
        for (int i = 0; i < occupied; i++) {
            allLined.add(new OccupiedCell());
        }
        allLined.add(new AntCell());
        for (int i = cubes + holes + occupied + 1; i < width * height; i++) {
            allLined.add(null);
        }

        Collections.shuffle(allLined);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map.setCell(allLined.get(width * y + x), x, y);
            }
        }

        returnWithResult(map);
    }

    private void updateBounds() {
        Platform.runLater(() -> {
            int height = Integer.parseInt(heightBox.getSelectionModel().getSelectedItem());
            int width = Integer.parseInt(widthBox.getSelectionModel().getSelectedItem());
            int bound = (int) ((height * width) * GameMap.OBJECTS_LIMIT_MULTIPLIER);

            int cubesSelectedIndex = cubesBox.getSelectionModel().getSelectedIndex();
            int holesSelectedIndex = holesBox.getSelectionModel().getSelectedIndex();
            int occupiedSelectedIndex = occupiedBox.getSelectionModel().getSelectedIndex();

            cubesBox.getItems().clear();
            holesBox.getItems().clear();
            occupiedBox.getItems().clear();
            for (int i = 0; i <= bound; i++) {
                cubesBox.getItems().add(String.valueOf(i));
                holesBox.getItems().add(String.valueOf(i));
                occupiedBox.getItems().add(String.valueOf(i));
            }

            if (cubesSelectedIndex != -1 && cubesSelectedIndex < cubesBox.getItems().size()) {
                cubesBox.getSelectionModel().select(cubesSelectedIndex);
            } else {
                cubesBox.getSelectionModel().selectLast();
            }

            if (holesSelectedIndex != -1 && holesSelectedIndex < holesBox.getItems().size()) {
                holesBox.getSelectionModel().select(holesSelectedIndex);
            } else {
                holesBox.getSelectionModel().selectLast();
            }

            if (occupiedSelectedIndex != -1 && occupiedSelectedIndex < occupiedBox.getItems().size()) {
                occupiedBox.getSelectionModel().select(occupiedSelectedIndex);
            } else {
                occupiedBox.getSelectionModel().selectLast();
            }
        });
    }
}


