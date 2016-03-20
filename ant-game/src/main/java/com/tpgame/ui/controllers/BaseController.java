package com.tpgame.ui.controllers;

import com.tpgame.core.utils.AppBeans;
import com.tpgame.ui.events.EventsManager;
import com.tpgame.ui.windows.SceneChangeAnimator;
import com.tpgame.ui.windows.WindowsUtils;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public abstract class BaseController implements Initializable {
    protected Scene scene;
    protected Parent root;
    protected BaseController parent;
    protected EventsManager eventsManager = AppBeans.get(EventsManager.class);

    public BaseController() {
        eventsManager.register(this);
    }

    public void init(Map params) {
    }

    protected <T extends BaseController> T openWindow(String view, Map params) {
        return (T) WindowsUtils.openWindow(view, this, params);
    }

    public void closeWindow() {
        eventsManager.unregister(this);
        scene.getWindow().hide();
    }

    public void closeAsInner() {
        eventsManager.unregister(this);
        root = null;
    }

    protected void changeScene(String view, Map params, SceneChangeAnimator animator) {
        WindowsUtils.changeScene(view, this, params, animator);
    }

    protected void changeSceneBack(SceneChangeAnimator animator) {
        WindowsUtils.changeSceneBack(this, animator);
    }

    protected <T extends BaseController> T loadInnerControllableNode(String view, Map params) {
        return (T) WindowsUtils.loadInnerControllableNode(view, this, params);
    }

    protected void returnWithResult(Object result) {
        parent.onResult(this, result);
        closeWindow();
    }

    protected void onResult(BaseController resultSender, Object result) {}

    public final void initialize(URL location, ResourceBundle resources) {
    }

    public Stage getStage() {
        return scene == null ? null : (Stage) scene.getWindow();
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public BaseController getParent() {
        return parent;
    }

    public void setParent(BaseController parent) {
        this.parent = parent;
    }
}

