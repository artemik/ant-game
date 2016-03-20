package com.tpgame.ui.controllers;

import com.tpgame.core.entities.Problem;
import com.tpgame.core.entities.security.SecurityContext;
import com.tpgame.core.services.LoginService;
import com.tpgame.core.utils.AppBeans;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.controlsfx.control.PopOver;

import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class TaskEditorController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());

    protected LoginService loginService = AppBeans.get(LoginService.class);

    public static final String PROBLEM_PARAM = "problem";
    protected static final int MAX_LEVEL = 4;
    protected static final int MAX_TITLE_LENGTH = 50;

    private Problem problem;
    private String savedProblemFields;

    @FXML
    private TextField titleTextField;
    @FXML
    private ComboBox<String> levelComboBox;
    @FXML
    private TextArea textTextArea;
    @FXML
    private Button okBtn;
    @FXML
    private Button cancelBtn;


    @Override
    public void init(Map params) {
        problem = (Problem) params.get(PROBLEM_PARAM);
        saveProblemFields();
        prepareStage();
        initFields();
    }

    private void saveProblemFields() {
        savedProblemFields += String.format("%s|%s|%s", problem.getTitle(), problem.getLevel(), problem.getText());
    }

    private boolean isProblemChanged() {
        String problemFields = String.format("%s|%s|%s", problem.getTitle(), problem.getLevel(), problem.getText());
        return !StringUtils.equals(problemFields, savedProblemFields);
    }

    protected void prepareStage() {
        Stage stage = getStage();
        stage.initStyle(StageStyle.UTILITY);

        SecurityContext.Mode mode = loginService.getSecurityContext().getMode();
        String title = SecurityContext.Mode.TEACHER.equals(mode) ? "Problem Editor" : "Problem";
        stage.setTitle(title);

        getStage().setOnCloseRequest(event -> {
            event.consume();
            returnWithResult(isProblemChanged());
        });

        stage.show();
    }

    private void initFields() {
        SecurityContext.Mode mode = loginService.getSecurityContext().getMode();

        titleTextField.setText(problem.getTitle() != null ? problem.getTitle() : "");
        titleTextField.addEventFilter(KeyEvent.KEY_TYPED, textFieldValidator(MAX_TITLE_LENGTH));
        titleTextField.setEditable(SecurityContext.Mode.TEACHER.equals(mode));


        for (int i = 1; i <= MAX_LEVEL; i++) {
            levelComboBox.getItems().add(String.valueOf(i));
        }
        levelComboBox.getSelectionModel().select(String.valueOf(problem.getLevel()));
        levelComboBox.setDisable(!SecurityContext.Mode.TEACHER.equals(mode));

        textTextArea.setText(problem.getText() != null ? problem.getText() : "");
        textTextArea.setEditable(SecurityContext.Mode.TEACHER.equals(mode));

        okBtn.setOnAction(event -> {
            if (validateFields()) {
                problem.setTitle(titleTextField.getText());
                problem.setLevel(Integer.parseInt(levelComboBox.getSelectionModel().getSelectedItem()));
                problem.setText(textTextArea.getText());
                returnWithResult(isProblemChanged());
            }
        });

        cancelBtn.setOnAction(event -> {
            returnWithResult(isProblemChanged());
        });
        cancelBtn.setVisible(SecurityContext.Mode.TEACHER.equals(mode));
    }

    public boolean validateFields() {
        boolean valid = true;
        if (StringUtils.isEmpty(titleTextField.getText())) {
            valid = false;
            showErrorPopUp(titleTextField, "Enter title");
        }
        return valid;
    }

    private void showErrorPopUp(Node owner, String message) {
        PopOver popOver = new PopOver();
        popOver.setContentNode(new Label(message));
        popOver.setDetachable(false);
        popOver.show(owner);
    }

    public EventHandler<KeyEvent> textFieldValidator(final int maxLength) {
        return e -> {
            TextField txt_TextField = (TextField) e.getSource();
            if (txt_TextField.getText().length() >= maxLength) {
                e.consume();
            }
            if (e.getCharacter().matches("\\p{L}") ||
                    e.getCharacter().matches("[0-9.]") ||
                    e.getCharacter().equals(" ")
                    ) {
            } else {
                e.consume();
            }
        };
    }
}


