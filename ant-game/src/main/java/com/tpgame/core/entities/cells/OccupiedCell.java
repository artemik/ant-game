package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.IOException;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class OccupiedCell extends Cell {
    private static final long serialVersionUID = 597950793203442097L;

    private static final Image OCCUPIED_IMG = new Image("/com/tpgame/ui/images/occupied.png");

    public OccupiedCell() {
        image = OCCUPIED_IMG;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = OCCUPIED_IMG;
    }
}
