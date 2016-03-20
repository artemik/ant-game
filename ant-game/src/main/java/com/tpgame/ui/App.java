package com.tpgame.ui;

import com.tpgame.core.utils.AppBeans;
import com.tpgame.core.utils.ApplicationLifeCycleListener;
import com.tpgame.core.utils.PathHelper;
import com.tpgame.ui.views.Views;
import com.tpgame.ui.windows.WindowsUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class App extends Application {
    public static void main(final String[] args) throws InterruptedException, IOException {
        App.launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        AppBeans.get(ApplicationLifeCycleListener.class).onAppStart(this);
        WindowsUtils.openWindow(Views.LOGIN_VIEW, null, null);
    }
}
