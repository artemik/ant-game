package com.tpgame.ui.controllers;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.Problem;
import com.tpgame.core.services.LoginService;
import com.tpgame.core.services.MapService;
import com.tpgame.core.utils.AppBeans;
import com.tpgame.core.utils.PathHelper;
import com.tpgame.ui.views.Views;
import com.tpgame.ui.windows.WindowsUtils;
import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class TeacherMainController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());
    protected LoginService loginService = AppBeans.get(LoginService.class);
    protected MapService mapService = AppBeans.get(MapService.class);

    private static final int MAX_CELLS_NUM = 15;

    private TeacherMapEditController mapEditor;
    private TaskEditorController taskEditor;
    private boolean mapEditorOpened = false;

    private GameMap map;

    @FXML
    private MenuItem fileMenuNewMapAutoModeItem;
    @FXML
    private MenuItem fileMenuNewMapManualModeItem;
    @FXML
    private MenuItem fileMenuOpenMapItem;
    @FXML
    private MenuItem fileMenuSaveMapItem;
    @FXML
    private MenuItem fileMenuEditProblemItem;
    @FXML
    private MenuItem fileMenuCloseMapItem;
    @FXML
    private Menu changeUserMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private StackPane stackPane;

    @Override
    public void init(Map params) {
        prepareStage();
        prepareMenu();
    }

    protected void prepareStage() {
        String userName = loginService.getSecurityContext().getUser().getLogin();
        getStage().setTitle(String.format("Teacher mode (%s)", userName));
        getStage().setOnCloseRequest(event -> {
            if (!validateMapClosing()) {
                event.consume();
            }
        });
        getStage().show();
    }

    protected void prepareMenu() {
        setMapMenuControlsEnabled(false);

        fileMenuOpenMapItem.setOnAction(event -> {
            openMapChooser();
        });

        fileMenuSaveMapItem.setOnAction(event -> {
            saveCurrentMap();
        });

        fileMenuEditProblemItem.setOnAction(event -> {
            openTaskEditor();
        });

        fileMenuNewMapManualModeItem.setOnAction(event -> {
            openNewMap();
        });

        fileMenuNewMapAutoModeItem.setOnAction(event2 -> {
            openAutoGenerator();
        });

        fileMenuCloseMapItem.setOnAction(event -> {
            closeCurrentTaskEditor();
            if (validateMapClosing()) {
                changeMapsAnimating(null);
            }
        });

        changeUserMenu.setText("");
        changeUserMenu.setGraphic(
                LabelBuilder.create()
                        .text("Change User Mode")
                        .onMouseClicked(event1 -> {
                            userChangeRequested();
                        })
                        .build());

        helpMenu.setText("");
        helpMenu.setGraphic(
                LabelBuilder.create()
                        .text("Help")
                        .onMouseClicked(event1 -> {
                            openHelp();
                        })
                        .build());
    }

    private void openAutoGenerator() {
        openWindow(Views.AUTO_GEN_VIEW, null);
    }

    private void openHelp() {
        File helpFile = new File(PathHelper.getAppRootFolder() + "/help/index.html");
        HostServices hostServices = PathHelper.getHostServices();
        hostServices.showDocument(helpFile.getAbsolutePath());
    }

    private boolean validateMapClosing() {
        if (mapEditor != null && mapEditor.isChangesBeenMade()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Closing");
            alert.setContentText("All unsaved changed will be lost. Are you sure?");

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeCancel = new ButtonType("CANCEL", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeOk);

            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == buttonTypeOk;
        }

        return true;
    }

    private void closeCurrentTaskEditor() {
        if (taskEditor != null) {
            taskEditor.closeWindow();
        }
    }

    private void saveCurrentMap() {
        if (mapEditorOpened) {
            boolean validForSave = validateMapForSave();
            if (validForSave) {
                updateMapSnapshot();
                mapService.commitMap(map);
                mapEditor.setChangesBeenMade(false);
            }
        }
    }

    private void updateMapSnapshot() {
        File file = new File(PathHelper.getAppRootFolder() + "/map_previews/" + map.getProblem().getTitle() + ".png");
        file.mkdirs();

        Image snapshot = mapEditor.getSnapshot();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (Exception ignored) {
        }
    }

    private boolean validateMapForSave() {
        Problem problem = map.getProblem();
        String title = problem.getTitle();

        if (StringUtils.isEmpty(title)) {
            openTaskEditor();
            taskEditor.validateFields();
            return false;
        }

        boolean alreadyExists = mapService.getMap(problem.getTitle()) != null;
        if (alreadyExists) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Map Saving");
            alert.setContentText(String.format("Map %s already exists. Overwrite?", title));

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeCancel = new ButtonType("CANCEL", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeOk);

            Optional<ButtonType> result = alert.showAndWait();
            return result.get() == buttonTypeOk;
        }

        return true;
    }

    private void openNewMap() {
        map = new GameMap(MAX_CELLS_NUM, MAX_CELLS_NUM);
        loadMapEditor();
    }

    protected void setMapMenuControlsEnabled(boolean enabled) {
        fileMenuSaveMapItem.setDisable(!enabled);
        fileMenuEditProblemItem.setDisable(!enabled);
        fileMenuCloseMapItem.setDisable(!enabled);
    }

    public void loadMapEditor() {
        if (validateMapClosing()) {
            Map<String, GameMap> params = Collections.singletonMap(TeacherMapEditController.MAP_PARAM, map);
            TeacherMapEditController mapEditController = loadInnerControllableNode(Views.TEACHER_MAP_EDIT_INNER_VIEW, params);
            changeMapsAnimating(mapEditController);
        }
    }

    private void changeMapsAnimating(TeacherMapEditController newMapEditor) {
        if (mapEditor == null && newMapEditor == null) {
            return;
        }
        if (mapEditor == null) {
            Node next = newMapEditor.getRoot();
            stackPane.getChildren().add(next);

            animateShow(next, event -> {
                mapEditorOpened = true;
                setMapMenuControlsEnabled(true);
                mapEditor = newMapEditor;
            });
            return;
        }
        if (newMapEditor == null) {
            Node current = mapEditor.getRoot();

            animateHide(current, event -> {
                stackPane.getChildren().remove(current);
                mapEditor.closeAsInner();
                mapEditorOpened = false;
                setMapMenuControlsEnabled(false);
                mapEditor = null;
            });
            return;
        }

        // both are not null
        Node current = mapEditor.getRoot();
        Node next = newMapEditor.getRoot();

        stackPane.getChildren().add(next);

        animateHide(current, null);
        animateShow(next, event -> {
            stackPane.getChildren().remove(current);
            mapEditor.closeAsInner();
            mapEditorOpened = true;
            setMapMenuControlsEnabled(true);
            mapEditor = newMapEditor;
        });
    }

    private void animateShow(Node node, EventHandler<ActionEvent> onFinished) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(200 * 2), node);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.setOnFinished(onFinished);
        ft.play();
    }

    private void animateHide(Node node, EventHandler<ActionEvent> onFinished) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(200 * 2), node);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.setOnFinished(onFinished);
        ft.play();
    }

    public void openTaskEditor() {
        Map<String, Problem> params = Collections.singletonMap(TaskEditorController.PROBLEM_PARAM, map.getProblem());
        taskEditor = openWindow(Views.TASK_EDITOR_VIEW, params);
    }

    private void openMapChooser() {
        openWindow(Views.TASK_BROWSER_VIEW, null);
    }

    private void userChangeRequested() {
        closeWindow();
        WindowsUtils.openWindow(Views.LOGIN_VIEW, null, null);
    }

    @Override
    protected void onResult(BaseController resultSender, Object result) {
        if (resultSender instanceof TaskBrowserController || resultSender instanceof AutoGenController) {
            closeCurrentTaskEditor();
            map = (GameMap) result;
            loadMapEditor();
        } else if (resultSender instanceof TaskEditorController) {
            boolean problemChanged = (boolean) result;
            mapEditor.setChangesBeenMade(problemChanged);
        }
    }
}

