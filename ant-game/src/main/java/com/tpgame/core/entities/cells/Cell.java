package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.Serializable;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public abstract class Cell implements Serializable {
    protected transient Image image;

    public Image getImage() {
        return image;
    }
}
