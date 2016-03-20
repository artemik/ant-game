package com.tpgame.core.services;

import com.tpgame.core.dao.UsersFileSystemDao;
import com.tpgame.core.entities.security.SecurityContext;
import com.tpgame.core.entities.security.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
@Singleton
public class LoginService {
    protected Log log = LogFactory.getLog(getClass());

    @Inject
    protected UsersFileSystemDao usersFileSystemDao;

    protected SecurityContext securityContext;

    public boolean login(String login, String password, SecurityContext.Mode mode) {
        User user = usersFileSystemDao.getUser(login);

        if (user != null && StringUtils.equals(password, user.getPassword())) {
            securityContext = new SecurityContext();
            securityContext.setUser(user);
            securityContext.setMode(mode);
            log.info(String.format("User: %s logged in, mode: %s", login, mode));
            return true;
        }

        return false;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }
}
