package com.tpgame.core.entities.cells;

import javafx.scene.image.Image;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class CubeCell extends Cell {
    private static final long serialVersionUID = 4600817606744812905L;

    private static final Map<Character, Image> images = new HashMap<>();

    static {
        images.put('À', new Image("/com/tpgame/ui/images/letters/a.png"));
        images.put('Á', new Image("/com/tpgame/ui/images/letters/b.png"));
        images.put('Â', new Image("/com/tpgame/ui/images/letters/v.png"));
        images.put('Ã', new Image("/com/tpgame/ui/images/letters/g.png"));
        images.put('Ä', new Image("/com/tpgame/ui/images/letters/d.png"));
        images.put('Å', new Image("/com/tpgame/ui/images/letters/e.png"));
        images.put('Æ', new Image("/com/tpgame/ui/images/letters/zh.png"));
        images.put('Ç', new Image("/com/tpgame/ui/images/letters/z.png"));
        images.put('È', new Image("/com/tpgame/ui/images/letters/i.png"));
        images.put('Ê', new Image("/com/tpgame/ui/images/letters/k.png"));
        images.put('Ë', new Image("/com/tpgame/ui/images/letters/l.png"));
        images.put('Ì', new Image("/com/tpgame/ui/images/letters/m.png"));
        images.put('Í', new Image("/com/tpgame/ui/images/letters/n.png"));
        images.put('Î', new Image("/com/tpgame/ui/images/letters/o.png"));
        images.put('Ï', new Image("/com/tpgame/ui/images/letters/p.png"));
        images.put('Ð', new Image("/com/tpgame/ui/images/letters/r.png"));
        images.put('Ñ', new Image("/com/tpgame/ui/images/letters/s.png"));
        images.put('Ò', new Image("/com/tpgame/ui/images/letters/t.png"));
        images.put('Ó', new Image("/com/tpgame/ui/images/letters/u.png"));
        images.put('Ô', new Image("/com/tpgame/ui/images/letters/f.png"));
        images.put('Õ', new Image("/com/tpgame/ui/images/letters/h.png"));
        images.put('Ö', new Image("/com/tpgame/ui/images/letters/c.png"));
        images.put('×', new Image("/com/tpgame/ui/images/letters/ch.png"));
        images.put('Ø', new Image("/com/tpgame/ui/images/letters/sh.png"));
        images.put('Ù', new Image("/com/tpgame/ui/images/letters/sch.png"));
        images.put('Û', new Image("/com/tpgame/ui/images/letters/y.png"));
        images.put('Ý', new Image("/com/tpgame/ui/images/letters/je.png"));
        images.put('Þ', new Image("/com/tpgame/ui/images/letters/yu.png"));
        images.put('ß', new Image("/com/tpgame/ui/images/letters/ya.png"));
    }

    private Character letter;

    public CubeCell(Character letter) {
        if (letter != null) {
            letter = Character.toUpperCase(letter);
        }
        this.letter = letter;
        image = images.get(letter);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = images.get(letter);
    }
}
