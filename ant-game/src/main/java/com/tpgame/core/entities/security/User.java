package com.tpgame.core.entities.security;

import java.io.Serializable;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class User implements Serializable {
    private static final long serialVersionUID = 44560423945000906L;

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
