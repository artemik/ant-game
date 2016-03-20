package com.tpgame.ui.controllers;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.security.SecurityContext;
import com.tpgame.core.services.LoginService;
import com.tpgame.core.services.MapService;
import com.tpgame.core.utils.AppBeans;
import com.tpgame.core.utils.PathHelper;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class TaskBrowserController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    protected LoginService loginService = AppBeans.get(LoginService.class);
    protected MapService mapService = AppBeans.get(MapService.class);

    protected static final int MAX_LEVEL = 4;

    @FXML
    private ComboBox<String> levelComboBox;
    @FXML
    private ComboBox<TaskOption> taskComboBox;
    @FXML
    private Button okBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ImageView previewImage;


    @Override
    public void init(Map params) {
        prepareStage();
        initFields();
    }

    protected void prepareStage() {
        Stage stage = getStage();

        SecurityContext.Mode mode = loginService.getSecurityContext().getMode();
        switch (mode) {
            case PUPIL:
                stage.setTitle("Pupil mode");
                break;
            case TEACHER:
                stage.setTitle("Teacher mode");
                break;
        }

        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    private void initFields() {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            levelComboBox.getItems().add(String.valueOf(i));
        }
        levelComboBox.getSelectionModel().selectFirst();
        levelComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            populateTaskList(Integer.parseInt(newValue));
        });

        populateTaskList(1);

        updatePreviewImageView();
        taskComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updatePreviewImageView();
            }
        });

        okBtn.setOnAction(event -> {
            TaskOption selectedOption = taskComboBox.getSelectionModel().getSelectedItem();
            if (selectedOption != null) {
                returnWithResult(selectedOption.getMap());
            }
        });

        cancelBtn.setOnAction(event -> {
            closeWindow();
        });
    }

    private void updatePreviewImageView() {
        Platform.runLater(() -> {
            GameMap map = taskComboBox.getSelectionModel().getSelectedItem().getMap();
            File snapshotFile = new File(
                    PathHelper.getAppRootFolder() + "/map_previews/" + map.getProblem().getTitle() + ".png");
            WritableImage snapshotImage = null;
            try {
                snapshotImage = SwingFXUtils.toFXImage(ImageIO.read(snapshotFile), null);
            } catch (IOException ignored) {
            }
            previewImage.setImage(snapshotImage);
        });
    }

    private void populateTaskList(int level) {
        taskComboBox.getItems().clear();
        List<GameMap> maps = mapService.getMaps(level);
        for (GameMap map : maps) {
            taskComboBox.getItems().add(new TaskOption(map));
        }
        taskComboBox.getSelectionModel().selectFirst();
    }

    private static class TaskOption {
        private GameMap map;

        private TaskOption(GameMap map) {
            this.map = map;
        }

        public GameMap getMap() {
            return map;
        }

        public void setMap(GameMap map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return map.getProblem().getTitle();
        }
    }
}