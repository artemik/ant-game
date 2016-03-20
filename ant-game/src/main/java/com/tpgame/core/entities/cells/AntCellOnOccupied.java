package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.IOException;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class AntCellOnOccupied extends AntCell {
    private static final long serialVersionUID = 4073520286582986388L;

    private static final Image ANT_OCCUPIED_IMG = new Image("/com/tpgame/ui/images/ant_occupied.png");

    public AntCellOnOccupied() {
        image = ANT_OCCUPIED_IMG;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ANT_OCCUPIED_IMG;
    }
}
