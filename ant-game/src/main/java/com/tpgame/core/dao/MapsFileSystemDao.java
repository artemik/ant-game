package com.tpgame.core.dao;

import com.tpgame.core.entities.GameMap;
import com.tpgame.core.utils.PathHelper;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
@Singleton
public class MapsFileSystemDao {
    public static final String MAPS_PATH = PathHelper.getAppRootFolder() + "/maps/";

    public List<GameMap> getMaps() {
        List<GameMap> maps = new ArrayList<>();

        File mapsFolder = getMapsFolder();
        File[] mapsFiles = mapsFolder.listFiles();
        if (mapsFiles != null) {
            for (File mapFile : mapsFiles) {
                maps.add(convertToMap(mapFile));
            }
        }

        return maps;
    }

    public GameMap getMap(String title) {
        List<GameMap> maps = getMaps();

        for (GameMap map : maps) {
            if (StringUtils.equals(map.getProblem().getTitle(), title)) {
                return map;
            }
        }

        return null;
    }

    public List<GameMap> getMaps(int level) {
        List<GameMap> maps = new ArrayList<>();

        for (GameMap map : getMaps()) {
            if (map.getProblem().getLevel() == level) {
                maps.add(map);
            }
        }

        return maps;
    }

    public void commitMap(GameMap map) {
        File mapFile = createMapFile(map);
        try {
            SerializationUtils.serialize(map, new FileOutputStream(mapFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unexpected problem during map file saving.", e);
        }
    }

    private File createMapFile(GameMap map) {
        File mapsFolder = getMapsFolder();
        File mapFile = new File(mapsFolder, map.getProblem().getTitle());
        try {
            mapFile.delete();
            mapFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected problem during map file creation.", e);
        }
        return mapFile;
    }

    private File getMapsFolder() {
        File mapsFolder = new File(MAPS_PATH);
        mapsFolder.mkdirs();
        return mapsFolder;
    }

    private GameMap convertToMap(File mapFile) {
        try {
            return SerializationUtils.deserialize(new FileInputStream(mapFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unexpected problem during map file reading.", e);
        }
    }
}
