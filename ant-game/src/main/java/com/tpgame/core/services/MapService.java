package com.tpgame.core.services;

import com.tpgame.core.dao.MapsFileSystemDao;
import com.tpgame.core.entities.GameMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Artem on 22.11.2015.
 */
@Singleton
public class MapService {
    @Inject
    protected MapsFileSystemDao mapsFileSystemDao;

    public void commitMap(GameMap map) {
        mapsFileSystemDao.commitMap(map);
    }

    public List<GameMap> getMaps() {
        return mapsFileSystemDao.getMaps();
    }

    public GameMap getMap(String title) {
        return mapsFileSystemDao.getMap(title);
    }

    public List<GameMap> getMaps(int level) {
        return mapsFileSystemDao.getMaps(level);
    }
}
