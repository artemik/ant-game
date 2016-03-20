package com.tpgame.core.dao;

import com.tpgame.core.entities.security.User;
import com.tpgame.core.utils.PathHelper;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
@Singleton
public class UsersFileSystemDao {
    public static final String USERS_PATH = PathHelper.getAppRootFolder() + "/users/";

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        File usersFolder = getUsersFolder();
        File[] userFiles = usersFolder.listFiles();
        if (userFiles != null) {
            for (File userFile : userFiles) {
                users.add(convertToUser(userFile));
            }
        }

        return users;
    }

    public User getUser(String login) {
        List<User> users = getUsers();

        for (User user : users) {
            if (StringUtils.equals(user.getLogin(), login)) {
                return user;
            }
        }

        return null;
    }

    public void storeUser(User user) {
        File userFile = createUserFile(user);
        try {
            SerializationUtils.serialize(user, new FileOutputStream(userFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unexpected problem during user file saving.", e);
        }
    }

    private User convertToUser(File userFile) {
        try {
            return SerializationUtils.deserialize(new FileInputStream(userFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unexpected problem during user file reading.", e);
        }
    }

    private File getUsersFolder() {
        File usersFolder = new File(USERS_PATH);
        usersFolder.mkdirs();
        return usersFolder;
    }

    private File createUserFile(User user) {
        File usersFolder = getUsersFolder();
        File userFile = new File(usersFolder, user.getLogin());
        try {
            userFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userFile;
    }
}
