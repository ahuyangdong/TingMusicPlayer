package com.dommy.music.service;

import com.dommy.music.bean.Song;
import com.dommy.music.dao.DaoManager;
import com.dommy.music.greendao.DaoSession;
import com.dommy.music.greendao.SongDao;
import com.dommy.music.util.CommonUtil;

import java.util.List;

/**
 * 歌曲列表数据服务层
 */
public class DBService {

    /**
     * 从数据库获取歌曲列表
     *
     * @return List<Song> 数据列表
     */
    public static List<Song> getSongListFromDB() {
        DaoSession daoSession = null;
        List<Song> songs = null;
        try {
            daoSession = DaoManager.getInstance().getDaoSession();
            songs = daoSession.getSongDao().queryBuilder().orderAsc(SongDao.Properties.FileNamePinyin).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (daoSession != null) {
                daoSession.clear();
            }
        }
        return songs;
    }

    /**
     * 保存列表数据至数据库表中
     *
     * @param songs 列表
     */
    public static void saveSongList2DB(List<Song> songs) {
        if (songs == null || songs.size() == 0) {
            return;
        }
        DaoSession daoSession = null;
        try {
            daoSession = DaoManager.getInstance().getDaoSession();
            for (Song song : songs) {
                if (song == null) {
                    continue;
                }
                if (CommonUtil.isNull(song.getFileName())) {
                    continue;
                }
                // 保存至数据库
                saveSong2DB(daoSession, song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (daoSession != null) {
                daoSession.clear();
            }
        }
    }

    /**
     * 保存歌曲信息入库
     *
     * @param daoSession
     * @param song
     */
    private static void saveSong2DB(DaoSession daoSession, Song song) {
        daoSession.getSongDao().insert(song);
    }

    /**
     * 清空数据库中的歌曲列表
     */
    public static void clearSongList() {
        DaoSession daoSession = null;
        try {
            daoSession = DaoManager.getInstance().getDaoSession();
            daoSession.getSongDao().deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (daoSession != null) {
                daoSession.clear();
            }
        }
    }

}
