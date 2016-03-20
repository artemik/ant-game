package com.tpgame.ui.controllers;

import com.tpgame.core.exceptions.UserAlreadyExistsException;
import com.tpgame.core.services.RegistrationService;
import com.tpgame.core.utils.AppBeans;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.controlsfx.control.PopOver;

import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class RegistrationController extends BaseController {
    protected Log log = LogFactory.getLog(getClass());
    protected RegistrationService registrationService = AppBeans.get(RegistrationService.class);

    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;
    @FXML
    private TextField loginTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField passwordConfirmTextField;

    @Override
    public void init(Map params) {
        prepareBottomButtons();
    }

    private void prepareBottomButtons() {
        okButton.setOnMouseClicked(event1 -> tryRegister());
        cancelButton.setOnMouseClicked(event -> changeSceneBack(null));
    }

    protected void tryRegister() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = passwordConfirmTextField.getText();

        if (!validate(login, password, confirmPassword)) {
            return;
        }

        if (!StringUtils.equals(password, confirmPassword)) {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("Passwords don't match"));
            popOver.setDetachable(false);
            popOver.show(okButton);
        } else {
            doTryRegister(login, password);
        }
    }

    private boolean validate(String login, String password, String confirmPassword) {
        if (StringUtils.isEmpty(login)) {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("Enter login"));
            popOver.setDetachable(false);
            popOver.show(loginTextField);
            return false;
        }

        if (StringUtils.isEmpty(password)) {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("Enter password"));
            popOver.setDetachable(false);
            popOver.show(passwordTextField);
            return false;
        }

        if (StringUtils.isEmpty(confirmPassword)) {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("Enter password one more time"));
            popOver.setDetachable(false);
            popOver.show(passwordConfirmTextField);
            return false;
        }
        return true;
    }

    private void doTryRegister(String login, String password) {
        try {
            registrationService.register(login, password);
            changeSceneBack(null);
        } catch (UserAlreadyExistsException e) {
            PopOver popOver = new PopOver();
            popOver.setContentNode(new Label("Such user already exist"));
            popOver.setDetachable(false);
            popOver.show(okButton);
        }
    }
}
