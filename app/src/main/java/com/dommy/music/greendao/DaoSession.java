package com.dommy.music.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dommy.music.bean.Song;

import com.dommy.music.greendao.SongDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig songDaoConfig;

    private final SongDao songDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        songDaoConfig = daoConfigMap.get(SongDao.class).clone();
        songDaoConfig.initIdentityScope(type);

        songDao = new SongDao(songDaoConfig, this);

        registerDao(Song.class, songDao);
    }
    
    public void clear() {
        songDaoConfig.clearIdentityScope();
    }

    public SongDao getSongDao() {
        return songDao;
    }

}
