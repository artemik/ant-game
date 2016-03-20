package com.tpgame.ui.windows;

import com.tpgame.ui.controllers.BaseController;
import com.tpgame.ui.views.Views;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class WindowsUtils {
    public static BaseController openWindow(String view, BaseController parent, Map params) {
        Class<? extends BaseController> controllerClass = Views.resolveController(view);
        BaseController controller = createControllerInstance(controllerClass);

        Parent root = loadAndLink(controller, view);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/com/tpgame/ui/images/icon.png"));

        stage.setScene(scene);

        controller.setScene(scene);
        controller.setRoot(root);
        controller.setParent(parent);
        controller.init(params);
        return controller;
    }

    public static void changeScene(String view, BaseController parent, Map params) {
        Class<? extends BaseController> controllerClass = Views.resolveController(view);
        BaseController controller = createControllerInstance(controllerClass);

        Parent newRoot = loadAndLink(controller, view);
        Scene newScene = new Scene(newRoot);
        Stage stage = parent.getStage();
        stage.setScene(newScene);

        controller.setScene(newScene);
        controller.setRoot(newRoot);
        controller.setParent(parent);
        controller.init(params);
    }

    public static void changeScene(String view, BaseController parent, Map params, SceneChangeAnimator animator) {
        Class<? extends BaseController> newControllerClass = Views.resolveController(view);
        BaseController newController = createControllerInstance(newControllerClass);

        Stage stage = parent.getStage();
        Scene scene = stage.getScene();
        Parent newRoot = loadAndLink(newController, view);
        Scene newScene = new Scene(newRoot);

        newController.setScene(newScene);
        newController.setRoot(newRoot);
        newController.setParent(parent);
        newController.init(params);

        //animator.animate(scene, newScene);
        Node innerRoot = scene.getRoot().getChildrenUnmodifiable().get(0);
        Node newInnerRoot = newRoot.getChildrenUnmodifiable().get(0);

        newInnerRoot.setOpacity(0);
        WindowsUtils.hideSmoothly(innerRoot, 200, event -> {
            stage.setScene(newScene);
            stage.centerOnScreen();
            WindowsUtils.showSmoothly(newInnerRoot, 200, null);
        });
    }

    public static void changeSceneBack(BaseController controller, SceneChangeAnimator animator) {
        BaseController prevController = controller.getParent();

        Scene scene = controller.getScene();
        Scene prevScene = prevController.getScene();

        controller.closeAsInner();

        //animator.animate(scene, prevScene);
        Stage stage = controller.getStage();
        Node innerRoot = scene.getRoot().getChildrenUnmodifiable().get(0);
        Node prevInnerRoot = prevScene.getRoot().getChildrenUnmodifiable().get(0);

        prevInnerRoot.setOpacity(0);
        WindowsUtils.hideSmoothly(innerRoot, 200, event -> {
            stage.setScene(prevScene);
            stage.centerOnScreen();
            WindowsUtils.showSmoothly(prevInnerRoot, 200, null);
        });
    }

    public static BaseController loadInnerControllableNode(String view, BaseController parent, Map params) {
        Class<? extends BaseController> controllerClass = Views.resolveController(view);
        BaseController controller = createControllerInstance(controllerClass);

        Scene scene = parent.getScene();
        Parent root = loadAndLink(controller, view);

        controller.setScene(scene); // maybe set null?
        controller.setRoot(root);
        controller.setParent(parent);
        controller.init(params);

        return controller;
    }

    public static void hideSmoothly(Node node, long millisSpeed, EventHandler<ActionEvent> onFinished) {
        FadeTransition ft = new FadeTransition(Duration.millis(millisSpeed), node);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.setOnFinished(onFinished);
        ft.play();
    }

    public static void showSmoothly(Node node, long millisSpeed, EventHandler<ActionEvent> onFinished) {
        FadeTransition ft = new FadeTransition(Duration.millis(millisSpeed), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.setOnFinished(onFinished);
        ft.play();
    }

    public static Parent loadAndLink(BaseController controller, String view) {
        FXMLLoader loader = new FXMLLoader(WindowsUtils.class.getResource(view));
        loader.setController(controller);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading view", e);
        }

        return root;
    }

    private static <C extends BaseController> C createControllerInstance(Class<C> clazz) {
        try {
            return ConstructorUtils.invokeConstructor(clazz);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Error creating controller", e);
        }
    }
}
