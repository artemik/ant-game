package com.tpgame.ui.controllers;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.entities.Problem;
import com.tpgame.core.services.LoginService;
import com.tpgame.core.utils.AppBeans;
import com.tpgame.core.utils.PathHelper;
import com.tpgame.ui.views.Views;
import com.tpgame.ui.windows.WindowsUtils;
import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Artem on 15.11.2015.
 */
public class PupilMainController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    protected LoginService loginService = AppBeans.get(LoginService.class);

    private PupilProblemSolvingController problemSolver;
    private boolean problemSolverOpened = false;
    private TaskEditorController problemGoalViewer;
    private GameMap map;

    @FXML
    private MenuItem fileMenuChooseProblem;
    @FXML
    private MenuItem fileMenuSaveCode;
    @FXML
    private MenuItem fileMenuShowProblem;
    @FXML
    private VBox rootVBox;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem fileMenuLoadCode;
    @FXML
    private StackPane stackPane;
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu changeUserMenu;

    @Override
    public void init(Map params) {
        prepareStage();
        prepareMenu();
    }

    protected void prepareStage() {
        String userName = loginService.getSecurityContext().getUser().getLogin();
        getStage().setTitle(String.format("Pupil Mode (%s)", userName));
        getStage().show();
    }

    protected void prepareMenu() {
        setMapMenuControlsEnabled(false);

        fileMenuChooseProblem.setOnAction(event -> {
            openProblemChooser();
        });

        fileMenuShowProblem.setOnAction(event -> {
            openProblemGoalViewer();
        });

        fileMenuLoadCode.setOnAction(event -> {
            openProgramLoader();
        });

        fileMenuSaveCode.setOnAction(event -> {
            openProgramSaver();
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

    private void openHelp() {
        File helpFile = new File(PathHelper.getAppRootFolder() + "/help/index.html");
        HostServices hostServices = PathHelper.getHostServices();
        hostServices.showDocument(helpFile.getAbsolutePath());
    }

    private void userChangeRequested() {
        closeWindow();
        WindowsUtils.openWindow(Views.LOGIN_VIEW, null, null);
    }

    protected void setMapMenuControlsEnabled(boolean enabled) {
        fileMenuSaveCode.setDisable(!enabled);
        fileMenuLoadCode.setDisable(!enabled);
        fileMenuShowProblem.setDisable(!enabled);
    }

    public void loadProblemSolver() {
        Map<String, GameMap> params = Collections.singletonMap(PupilProblemSolvingController.MAP_PARAM, map);
        PupilProblemSolvingController problemSolvingController = loadInnerControllableNode(Views.PUPIL_PROBLEM_SOLVING_INNER_VIEW, params);
        changeMapsAnimating(problemSolvingController);
        openProblemGoalViewer();
    }

    private void changeMapsAnimating(PupilProblemSolvingController newProblemSolver) {
        if (problemSolver == null && newProblemSolver == null) {
            return;
        }
        if (problemSolver == null) {
            Node next = newProblemSolver.getRoot();
            stackPane.getChildren().add(next);

            animateShow(next, event -> {
                problemSolverOpened = true;
                setMapMenuControlsEnabled(true);
                problemSolver = newProblemSolver;
            });
            return;
        }
        if (newProblemSolver == null) {
            Node current = problemSolver.getRoot();

            animateHide(current, event -> {
                stackPane.getChildren().remove(current);
                problemSolver.closeAsInner();
                problemSolverOpened = false;
                setMapMenuControlsEnabled(false);
                problemSolver = null;
            });
            return;
        }

        // both are not null
        Node current = problemSolver.getRoot();
        Node next = newProblemSolver.getRoot();

        stackPane.getChildren().add(next);

        animateHide(current, null);
        animateShow(next, event -> {
            stackPane.getChildren().remove(current);
            problemSolver.closeAsInner();
            problemSolverOpened = true;
            setMapMenuControlsEnabled(true);
            problemSolver = newProblemSolver;
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

    public void openProblemGoalViewer() {
        Map<String, Problem> params = Collections.singletonMap(TaskEditorController.PROBLEM_PARAM, map.getProblem());
        problemGoalViewer = openWindow(Views.TASK_EDITOR_VIEW, params);
    }

    private void closeCurrentProblemViewer() {
        if (problemGoalViewer != null) {
            problemGoalViewer.closeWindow();
        }
    }

    private void openProblemChooser() {
        openWindow(Views.TASK_BROWSER_VIEW, null);
    }

    @Override
    protected void onResult(BaseController resultSender, Object result) {
        if (resultSender instanceof TaskBrowserController) {
            closeCurrentProblemViewer();
            map = (GameMap) result;
            loadProblemSolver();
        }
    }

    private void openProgramSaver() {
        if (!problemSolver.validateCode()) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Program Code");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Program code", "*.code"));
        fileChooser.setInitialDirectory(new File(PathHelper.getAppRootFolder()));
        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            ArrayList<Object> packedCode = problemSolver.getPackedCode();
            try {
                byte[] packedCodeBytes = SerializationUtils.serialize(packedCode);
                FileOutputStream os = new FileOutputStream(file);
                os.write(packedCodeBytes);
                os.close();
            } catch (IOException e) {
                log.error("Could not save program code", e);
            }
        }
    }

    private void openProgramLoader() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open program code");
        fileChooser.setInitialDirectory(new File(PathHelper.getAppRootFolder()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Program code", "*.code"));
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                ArrayList<Object> packedCode = SerializationUtils.deserialize(new FileInputStream(file));
                problemSolver.loadPackedCode(packedCode);
            } catch (IOException e) {
                log.error("Could not load program code", e);
            }
        }
    }
}



