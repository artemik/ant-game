package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.IOException;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class AntCell extends Cell {
    private static final long serialVersionUID = -3143598920308707767L;

    private static final Image ANT_IMG = new Image("/com/tpgame/ui/images/ant.png");

    public AntCell() {
        image = ANT_IMG;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ANT_IMG;
    }
}
