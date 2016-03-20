package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.IOException;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class HoleCell extends Cell {
    private static final long serialVersionUID = -7103011552016180840L;

    private static final Image HOLE_IMG = new Image("/com/tpgame/ui/images/hole.png");

    public HoleCell() {
        image = HOLE_IMG;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = HOLE_IMG;
    }
}
