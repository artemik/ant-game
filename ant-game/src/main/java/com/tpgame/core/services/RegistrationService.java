package com.tpgame.core.services;

import com.tpgame.core.dao.UsersFileSystemDao;
import com.tpgame.core.entities.security.User;
import com.tpgame.core.exceptions.UserAlreadyExistsException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Artem on 22.11.2015.
 */
@Singleton
public class RegistrationService {
    @Inject
    protected UsersFileSystemDao usersFileSystemDao;

    public User register(String login, String password) throws UserAlreadyExistsException {
        User existingUser = usersFileSystemDao.getUser(login);
        if (existingUser != null) {
            throw new UserAlreadyExistsException();
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        usersFileSystemDao.storeUser(newUser);
        return newUser;
    }
}
