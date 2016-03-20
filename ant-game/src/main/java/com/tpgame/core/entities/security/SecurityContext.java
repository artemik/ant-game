package com.tpgame.core.entities.security;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class SecurityContext {
    public enum Mode { TEACHER, PUPIL }

    protected User user;

    protected Mode mode;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}


