package com.tpgame.ui.views;

import com.tpgame.ui.controllers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public abstract class Views {
    private static final Map<String, Class<? extends BaseController>> VIEW_TO_CONTROLLER = new HashMap<>();

    // Main
    public static final String LOGIN_VIEW = "/com/tpgame/ui/views/login.fxml";
    public static final String REGISTRATION_VIEW = "/com/tpgame/ui/views/registration.fxml";
    public static final String TEACHER_MAIN_VIEW = "/com/tpgame/ui/views/teacher_main.fxml";
    public static final String PUPIL_MAIN_VIEW = "/com/tpgame/ui/views/pupil_main.fxml";
    public static final String TASK_EDITOR_VIEW = "/com/tpgame/ui/views/task_editor.fxml";
    public static final String TASK_BROWSER_VIEW = "/com/tpgame/ui/views/task_browser.fxml";
    public static final String AUTO_GEN_VIEW = "/com/tpgame/ui/views/auto_generation.fxml";

    // Inner
    public static final String TEACHER_MAP_EDIT_INNER_VIEW = "/com/tpgame/ui/views/teacher_map_edit.fxml";
    public static final String PUPIL_PROBLEM_SOLVING_INNER_VIEW = "/com/tpgame/ui/views/pupil_problem_solving.fxml";
    public static final String MAP_VIEWER_INNER_VIEW = "/com/tpgame/ui/views/map_viewer.fxml";

    static {
        // Main
        VIEW_TO_CONTROLLER.put(LOGIN_VIEW, LoginController.class);
        VIEW_TO_CONTROLLER.put(REGISTRATION_VIEW, RegistrationController.class);
        VIEW_TO_CONTROLLER.put(TEACHER_MAIN_VIEW, TeacherMainController.class);
        VIEW_TO_CONTROLLER.put(PUPIL_MAIN_VIEW, PupilMainController.class);
        VIEW_TO_CONTROLLER.put(TASK_EDITOR_VIEW, TaskEditorController.class);
        VIEW_TO_CONTROLLER.put(TASK_BROWSER_VIEW, TaskBrowserController.class);
        VIEW_TO_CONTROLLER.put(AUTO_GEN_VIEW, AutoGenController.class);

        // Inner
        VIEW_TO_CONTROLLER.put(TEACHER_MAP_EDIT_INNER_VIEW, TeacherMapEditController.class);
        VIEW_TO_CONTROLLER.put(PUPIL_PROBLEM_SOLVING_INNER_VIEW, PupilProblemSolvingController.class);
        VIEW_TO_CONTROLLER.put(MAP_VIEWER_INNER_VIEW, MapViewerController.class);
    }

    public static Class<? extends BaseController> resolveController(String view) {
        return VIEW_TO_CONTROLLER.get(view);
    }
}
