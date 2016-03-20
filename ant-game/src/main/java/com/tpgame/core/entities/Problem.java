package com.tpgame.core.entities;

import java.io.Serializable;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class Problem implements Serializable {
    private static final long serialVersionUID = -2710459974070364139L;

    private String title;
    private int level;
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
