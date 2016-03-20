package com.tpgame.core.utils;

import com.tpgame.core.dao.UsersFileSystemDao;
import com.tpgame.core.entities.security.User;
import com.tpgame.ui.App;

import javax.inject.Singleton;

/**
 * @author Artemik on 17.11.2015
 * @version $Id: $
 */
@Singleton
public class ApplicationLifeCycleListener {
    public void onAppStart(App app) {
        createDefaultUsers();
        PathHelper.setHostServices(app.getHostServices());
    }

    private static void createDefaultUsers() {
        User admin = new User();
        admin.setLogin("admin");
        admin.setPassword("admin");

        User simple = new User();
        simple.setLogin("simple");
        simple.setPassword("simple");

        UsersFileSystemDao usersFileSystemDao = AppBeans.get(UsersFileSystemDao.class);
        usersFileSystemDao.storeUser(admin);
        usersFileSystemDao.storeUser(simple);
    }
}
