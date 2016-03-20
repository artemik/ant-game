package com.tpgame.ui.controllers;

import com.tpgame.core.commands.*;
import com.tpgame.core.entities.GameMap;
import com.tpgame.ui.views.Views;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class PupilProblemSolvingController extends BaseController {
    private static final int SIMULATION_DELAY_MS = 500;
    protected Log log = LogFactory.getLog(getClass());

    public static final String MAP_PARAM = "map";

    private MapViewerController mapViewer;
    private CommandOption lastSelectedCommandOption;
    private GameMap map;
    private byte[] mapBackup;
    private Thread executionThread;
    private boolean stopRequested;

    @FXML
    private ListView<Movement> moveListView;
    @FXML
    private List<ListView> optionsLists;
    @FXML
    private ListView<Push> pushListView;
    @FXML
    private Button stopBtn;
    @FXML
    private ListView<Cycle> cycleListView;
    @FXML
    private TreeView<CommandOption> codeTreeView;
    @FXML
    private Button startBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private ListView<JumpHole> jumpListView;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button addAfterBtn;
    @FXML
    private Button addInsideBtn;
    @FXML
    private Button addBeforeBtn;
    @FXML
    private Button replaceBtn;
    @FXML
    private Button clearBtn;

    private enum AddType {BEFORE, INSIDE, AFTER}

    @Override
    public void init(Map params) {
        map = (GameMap) params.get(MAP_PARAM);
        prepareCodeTreeView();
        prepareCommands();
        prepareButtons();
        prepareMapViewer();
    }

    private void prepareCommands() {
        moveListView.getItems().add(new Movement(MovementCommand.Type.LEFT));
        moveListView.getItems().add(new Movement(MovementCommand.Type.RIGHT));
        moveListView.getItems().add(new Movement(MovementCommand.Type.UP));
        moveListView.getItems().add(new Movement(MovementCommand.Type.DOWN));
        moveListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedCommandOption = newValue;
                updateToolsSelections(moveListView);
            }
        });
        moveListView.getSelectionModel().select(0);
        Platform.runLater(() -> moveListView.requestFocus());

        pushListView.getItems().add(new Push(PushCommand.Type.LEFT));
        pushListView.getItems().add(new Push(PushCommand.Type.RIGHT));
        pushListView.getItems().add(new Push(PushCommand.Type.UP));
        pushListView.getItems().add(new Push(PushCommand.Type.DOWN));
        pushListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedCommandOption = newValue;
                updateToolsSelections(pushListView);
            }
        });

        jumpListView.getItems().add(new JumpHole(JumpHoleCommand.Type.LEFT));
        jumpListView.getItems().add(new JumpHole(JumpHoleCommand.Type.RIGHT));
        jumpListView.getItems().add(new JumpHole(JumpHoleCommand.Type.UP));
        jumpListView.getItems().add(new JumpHole(JumpHoleCommand.Type.DOWN));
        jumpListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedCommandOption = newValue;
                updateToolsSelections(jumpListView);
            }
        });

        cycleListView.getItems().add(new Cycle(1));
        cycleListView.getItems().add(new Cycle(2));
        cycleListView.getItems().add(new Cycle(3));
        cycleListView.getItems().add(new Cycle(4));
        cycleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedCommandOption = newValue;
                updateToolsSelections(cycleListView);
            }
        });
    }

    private void updateToolsSelections(ListView selectedList) {
        Platform.runLater(() -> {
            if (moveListView != selectedList) {
                moveListView.getSelectionModel().clearSelection();
            }
            if (pushListView != selectedList) {
                pushListView.getSelectionModel().clearSelection();
            }
            if (jumpListView != selectedList) {
                jumpListView.getSelectionModel().clearSelection();
            }
            if (cycleListView != selectedList) {
                cycleListView.getSelectionModel().clearSelection();
            }
        });
    }

    private void prepareButtons() {
        addBeforeBtn.setOnAction(event -> addCommand(AddType.BEFORE));
        addBeforeBtn.setDisable(true);

        addAfterBtn.setOnAction(event -> addCommand(AddType.AFTER));
        addInsideBtn.setOnAction(event -> addCommand(AddType.INSIDE));
        addInsideBtn.setDisable(true);

        replaceBtn.setOnAction(event -> replaceCommand());
        replaceBtn.setDisable(true);

        removeBtn.setOnAction(event -> removeCommand());
        removeBtn.setDisable(true);

        startBtn.setOnAction(event -> startExecution());
        startBtn.setDisable(true);

        stopBtn.setOnAction(event -> stopExecution());
        stopBtn.setDisable(true);

        clearBtn.setOnAction(event -> clearAllCode());
    }

    private void clearAllCode() {
        Platform.runLater(() -> {
            codeTreeView.getRoot().getChildren().clear();
            codeTreeView.getSelectionModel().clearSelection();
        });
    }

    private boolean execute(TreeItem<CommandOption> node) {
        if (stopRequested) {
            return true;
        }

        CommandOption commandOption = node.getValue();

        if (node.isLeaf()) {
            try {
                Thread.sleep(SIMULATION_DELAY_MS);
            } catch (InterruptedException ignored) {
                if (stopRequested) {
                    return true;
                }
            }

            CommandExecutionResult result = commandOption.getCommand().execute(map);
            if (result.isSuccessful()) {
                Platform.runLater(() -> {
                    mapViewer.reDraw();
                });
            } else {
                Platform.runLater(() -> {
                    expandUp(node);
                    codeTreeView.getSelectionModel().select(node);
                    codeTreeView.requestFocus();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setX(830);
                    alert.setY(530);
                    alert.setTitle("Command Execution Failed");
                    alert.setHeaderText(null);
                    alert.setContentText(result.getMessage());
                    alert.showAndWait();
                });
            }
            return result.isSuccessful();
        } else {
            Cycle cycle = ((Cycle) commandOption);
            for (int i = 0; i < cycle.getN(); i++) {
                for (TreeItem<CommandOption> innerNode : node.getChildren()) {
                    boolean result = execute(innerNode);
                    if (!result) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private void startExecution() {
        if ((executionThread == null || !executionThread.isAlive()) && codeTreeView.getRoot().getChildren().size() > 0) {
            if (!validateCode()) {
                return;
            }

            stopRequested = false;
            mapBackup = SerializationUtils.serialize(map);
            Platform.runLater(() -> startBtn.setDisable(true));
            Platform.runLater(() -> stopBtn.setDisable(false));

            executionThread = new Thread(() -> {
                boolean successfulFinished = true;
                for (TreeItem<CommandOption> node : codeTreeView.getRoot().getChildren()) {
                    if (executionThread.isInterrupted()) {
                        successfulFinished = false;
                        break;
                    }
                    boolean result = execute(node);
                    if (!result) {
                        successfulFinished = false;
                        break;
                    }
                }
                final boolean finalSuccessfulFinished = successfulFinished;
                Platform.runLater(() -> {
                    if (finalSuccessfulFinished && !stopRequested) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setX(830);
                        alert.setY(530);
                        alert.setTitle("Program successfully finished");
                        alert.setHeaderText(null);
                        alert.setContentText("Task completed successfully!");
                        alert.showAndWait();
                    }
                    map = SerializationUtils.deserialize(mapBackup);
                    mapViewer.init(new HashMap<String, GameMap>() {{
                        put(MapViewerController.MAP_PARAM, map);
                    }});
                    Platform.runLater(() -> startBtn.setDisable(false));
                    Platform.runLater(() -> stopBtn.setDisable(true));
                });
            });
            executionThread.start();
        }
    }

    public boolean validateCode() {
        TreeItem emptyCycleNode = isHasEmptyCycles();
        if (emptyCycleNode != null) {
            Platform.runLater(() -> {
                codeTreeView.getSelectionModel().clearSelection();
                codeTreeView.getSelectionModel().select(emptyCycleNode);
            });
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setX(830);
                alert.setY(530);
                alert.setTitle("Program Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Program should not have empty cycles");
                alert.showAndWait();
            });
            return false;
        }
        return true;
    }

    private void stopExecution() {
        stopRequested = true;
        if (executionThread != null && executionThread.isAlive()) {
            executionThread.interrupt();
        }
    }

    protected void expandUp(TreeItem<?> node) {
        if (node != null) {
            node.setExpanded(true);
            expandUp(node.getParent());
        }
    }

    private void addCommand(AddType addType) {
        if (lastSelectedCommandOption == null) {
            return;
        }

        TreeItem<CommandOption> newCodeItem = new TreeItem<>(lastSelectedCommandOption);
        TreeItem<CommandOption> selectedCodeItem = codeTreeView.getSelectionModel().getSelectedItem();

        if (selectedCodeItem == null || selectedCodeItem == codeTreeView.getRoot()) {
            codeTreeView.getRoot().getChildren().add(newCodeItem);
            codeTreeView.getSelectionModel().select(0);
            return;
        }

        ObservableList<TreeItem<CommandOption>> children = selectedCodeItem.getParent().getChildren();

        if (selectedCodeItem.getValue() instanceof Cycle && AddType.INSIDE.equals(addType)) {
            children = selectedCodeItem.getChildren();
            children.add(newCodeItem);
        } else {
            int row = children.indexOf(selectedCodeItem);
            switch (addType) {
                case AFTER:
                    children.add(row + 1, newCodeItem);
                    Platform.runLater(() -> {
                        codeTreeView.getSelectionModel().select(newCodeItem);
                    });
                    break;
                case BEFORE:
                    children.add(row, newCodeItem);
                    Platform.runLater(() -> {
                        codeTreeView.getSelectionModel().select(newCodeItem);
                    });
                    break;
            }

        }

    }

    private void replaceCommand() {
        TreeItem<CommandOption> selected = codeTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected != codeTreeView.getRoot()) {
            selected.getChildren().clear();
            selected.setExpanded(false);
            selected.setValue(lastSelectedCommandOption);
        }

        // the hook in order for the addInside button activity to work out
        Platform.runLater(() -> codeTreeView.getSelectionModel().clearSelection());
        Platform.runLater(() -> codeTreeView.getSelectionModel().select(selected));
    }

    private void removeCommand() {
        TreeItem<CommandOption> selected = codeTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected != codeTreeView.getRoot()) {
            if (codeTreeView.getRoot().getChildren().size() == 1) {
                codeTreeView.getSelectionModel().clearSelection();
            }
            selected.getParent().getChildren().remove(selected);
        }
    }

    private void prepareCodeTreeView() {
        codeTreeView.setShowRoot(false);
        codeTreeView.setRoot(new TreeItem<>(null));
        codeTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    addInsideBtn.setDisable(!(newValue.getValue() instanceof Cycle));
                }
                boolean hasCommands = codeTreeView.getRoot().getChildren().size() > 0;
                removeBtn.setDisable(!hasCommands);
                replaceBtn.setDisable(!hasCommands);
                addBeforeBtn.setDisable(!hasCommands);
                startBtn.setDisable(!hasCommands);
            });

        });
    }

    protected void prepareMapViewer() {
        Map<String, GameMap> params = Collections.singletonMap(MapViewerController.MAP_PARAM, map);
        MapViewerController mapViewerController = loadInnerControllableNode(Views.MAP_VIEWER_INNER_VIEW, params);
        mapViewer = mapViewerController;
        gridPane.getChildren().add(mapViewerController.getRoot());
    }

    public void loadPackedCode(List<Object> codeList) {
        codeTreeView.getSelectionModel().clearSelection();
        codeTreeView.getRoot().getChildren().clear();
        for (Object line : codeList) {
            codeTreeView.getRoot().getChildren().add(loadCodeLine(line));
        }
        Platform.runLater(() -> {
            codeTreeView.getSelectionModel().selectLast();
        });
    }

    public TreeItem loadCodeLine(Object line) {
        if (!(line instanceof CycleCommandsList)) {
            return new TreeItem<>(line);
        } else {
            CycleCommandsList cycle = (CycleCommandsList) line;
            TreeItem cycleRootItem = new TreeItem<>(new Cycle(cycle.getN()));
            for (Object cycleLine : cycle) {
                TreeItem cycleItem = loadCodeLine(cycleLine);
                cycleRootItem.getChildren().add(cycleItem);
            }
            return cycleRootItem;
        }
    }

    public ArrayList<Object> getPackedCode() {
        ArrayList<Object> codeList = new ArrayList<>();
        for (TreeItem<CommandOption> node : codeTreeView.getRoot().getChildren()) {
            codeList.add(packCodeLine(node, codeList));
        }
        return codeList;
    }

    private TreeItem isHasEmptyCycles() {
        for (TreeItem<CommandOption> node : codeTreeView.getRoot().getChildren()) {
            TreeItem emptyCycleNode = isHasEmptyCycles(node);
            if (emptyCycleNode != null) {
                return emptyCycleNode;
            }
        }
        return null;
    }

    private TreeItem isHasEmptyCycles(TreeItem<CommandOption> node) {
        if (node.isLeaf()) {
            if (node.getValue() instanceof Cycle) {
                return node;
            } else {
                return null;
            }
        } else {
            for (TreeItem<CommandOption> innerNode : node.getChildren()) {
                TreeItem emptyCycleNode = isHasEmptyCycles(innerNode);
                if (emptyCycleNode != null) {
                    return emptyCycleNode;
                }
            }
            return null;
        }
    }

    private Object packCodeLine(TreeItem<CommandOption> node, List<Object> codeList) {
        if (!(node.getValue() instanceof Cycle)) {
            return node.getValue();
        } else {
            Cycle cycleValue = (Cycle) node.getValue();
            CycleCommandsList cycle = new CycleCommandsList();
            cycle.setN(cycleValue.getN());
            for (TreeItem<CommandOption> innerNode : node.getChildren()) {
                cycle.add(packCodeLine(innerNode, codeList));
            }
            return cycle;
        }
    }

    private abstract static class CommandOption implements Serializable {
        private static final long serialVersionUID = -770783833376806048L;
        protected String name;
        protected Command command;

        public Command getCommand() {
            return command;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class Movement extends CommandOption {
        private static final long serialVersionUID = -7251053440990677532L;

        private Movement(MovementCommand.Type type) {
            command = new MovementCommand(type);
            switch (type) {
                case LEFT:
                    name = "Left";
                    break;
                case RIGHT:
                    name = "Right";
                    break;
                case UP:
                    name = "Up";
                    break;
                case DOWN:
                    name = "Down";
                    break;
                default:
                    name = "-";
            }
        }
    }

    private static class Push extends CommandOption {
        private static final long serialVersionUID = 4217127398852070859L;

        private Push(PushCommand.Type type) {
            command = new PushCommand(type);
            switch (type) {
                case LEFT:
                    name = "Push left";
                    break;
                case RIGHT:
                    name = "Push right";
                    break;
                case UP:
                    name = "Push up";
                    break;
                case DOWN:
                    name = "Push down";
                    break;
                default:
                    name = "-";
            }
        }
    }

    private static class JumpHole extends CommandOption {
        private static final long serialVersionUID = -3296689867112879015L;

        private JumpHole(JumpHoleCommand.Type type) {
            command = new JumpHoleCommand(type);
            switch (type) {
                case LEFT:
                    name = "Jump left";
                    break;
                case RIGHT:
                    name = "Jump right";
                    break;
                case UP:
                    name = "Jump up";
                    break;
                case DOWN:
                    name = "Jump down";
                    break;
                default:
                    name = "-";
            }
        }
    }

    private static class Cycle extends CommandOption {
        private static final long serialVersionUID = 6245920819161484072L;
        private int n;

        private Cycle(int n) {
            switch (n) {
                case 1:
                    name = n + " iteration";
                    break;
                case 2:
                case 3:
                case 4:
                    name = n + " iterations";
                    break;
            }
            this.n = n;
        }

        public int getN() {
            return n;
        }
    }

    private static class CycleCommandsList extends ArrayList {
        private static final long serialVersionUID = -5372697331894874697L;

        private int n;

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }
    }
}

