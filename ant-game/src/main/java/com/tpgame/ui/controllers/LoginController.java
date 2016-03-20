package com.tpgame.ui.controllers;

import com.tpgame.core.entities.security.SecurityContext;
import com.tpgame.core.services.LoginService;
import com.tpgame.core.utils.AppBeans;
import com.tpgame.ui.views.Views;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.controlsfx.control.PopOver;

import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class LoginController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());
    protected LoginService loginService = AppBeans.get(LoginService.class);

    @FXML
    private HBox rootHbox2;
    @FXML
    private Button signInButton;
    @FXML
    private Button registrationButton;
    @FXML
    private TextField loginTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private RadioButton pupilRadioButton;
    @FXML
    private RadioButton teacherRadioButton;

    @Override
    public void init(Map params) {
        prepareStage();

        prepareRoleButton();
        prepareBottomButtons();
    }

    protected void prepareStage() {
        Stage stage = getStage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Authorization");
        stage.getScene().setFill(null);
        prepareScene();
        stage.show();
    }

    protected void prepareScene() {
        makeMouseMovable(scene);

        scene.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                Platform.runLater(this::tryLogin);
            }
        });
    }

    protected void makeMouseMovable(Scene scene) {
        class Delta {
            double x, y;
        }
        final Delta dragDelta = new Delta();

        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = getStage().getX() - mouseEvent.getScreenX();
            dragDelta.y = getStage().getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            getStage().setX(mouseEvent.getScreenX() + dragDelta.x);
            getStage().setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    protected void prepareBottomButtons() {
        signInButton.setOnMouseClicked(event -> tryLogin());

        registrationButton.setOnMouseClicked(e -> {
            changeScene(Views.REGISTRATION_VIEW, null, null);
        });
    }

    protected void prepareRoleButton() {
        ToggleGroup roleGroup = new ToggleGroup();
        pupilRadioButton.setToggleGroup(roleGroup);
        pupilRadioButton.setSelected(true);

        teacherRadioButton.setToggleGroup(roleGroup);
    }

    protected void tryLogin() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        SecurityContext.Mode mode = teacherRadioButton.isSelected() ? SecurityContext.Mode.TEACHER : SecurityContext.Mode.PUPIL;

        boolean successfulLogin = loginService.login(login, password, mode);
        if (successfulLogin) {
            if (SecurityContext.Mode.TEACHER.equals(mode)) {
                openWindow(Views.TEACHER_MAIN_VIEW, null);
            } else {
                openWindow(Views.PUPIL_MAIN_VIEW, null);
            }
            closeWindow();
        } else {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("No such user or password is incorrect"));
            popOver.setDetachable(false);
            popOver.show(signInButton);
            Platform.runLater(signInButton::requestFocus);
        }
    }
}

