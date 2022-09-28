package com.dommy.music.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.dommy.music.bean.Song;
import com.github.promeg.pinyinhelper.Pinyin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体工具
 */
public class MediaUtil {
    // 音乐文件最小文件大小
    private final static int FILE_SIZE_MIN = 1024 * 800;

    /**
     * 扫描所有的音频文件
     *
     * @param mContext
     * @return
     */
    public static List<Song> scanAllAudioFiles(Context mContext) {
        List<Song> songListInternal = scanAudioFiles(mContext, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        List<Song> songListExternal = scanAudioFiles(mContext, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        if (songListInternal == null) {
            return songListExternal;
        }
        if (songListExternal == null) {
            return songListInternal;
        }
        songListInternal.addAll(songListExternal);
        return songListInternal;
    }

    /**
     * 扫描音频文件
     *
     * @param mContext
     * @param uri
     * @return
     */
    public static List<Song> scanAudioFiles(Context mContext, Uri uri) {
        List<Song> songList = new ArrayList<>();
        //查询媒体数据库
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲名
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                // 专辑封面id，根据该id可以获得专辑封面图片
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                // 音乐文件名
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (size > FILE_SIZE_MIN) {//若是文件大小大于800K，将该文件信息存入到map集合中
                    Song song = new Song();
                    song.setTitle(trimUnknown(title));
                    song.setAuthor(trimUnknown(author));
                    song.setAlbum(trimUnknown(album));
                    song.setAlbumId(albumId);
                    song.setFileName(trimUnknown(fileName));
                    song.setFilePath(filePath);
                    song.setDuration(duration);
                    song.setFileNamePinyin(Pinyin.toPinyin(song.getFileName(), ""));
                    songList.add(song);
                }
                cursor.moveToNext();
            }
        }
        return songList;
    }

    /**
     * 移除"unknown字样"
     *
     * @param str
     * @return
     */
    private static String trimUnknown(String str) {
        if (CommonUtil.isNull(str)) {
            return str;
        }
        return str.replace("<unknown>", "");
    }

    /**
     * 获取歌曲显示标题
     *
     * @param song
     * @return
     */
    public static String getSongShowTitle(Song song) {
        String title = null;
        if (!CommonUtil.isNull(song.getTitle()) && !CommonUtil.isNull(song.getAuthor()) &&
                !CommonUtil.isMessyCode(song.getTitle()) && !CommonUtil.isMessyCode(song.getAuthor())) {
            title = song.getAuthor() + " - " + song.getTitle();
        } else if (!CommonUtil.isNull(song.getTitle()) && song.getTitle().contains("-")) {
            title = song.getTitle();
        } else if (!CommonUtil.isNull(song.getFileName()) && song.getFileName().contains("-")) {
            title = song.getFileName().substring(0, song.getFileName().lastIndexOf("."));
        } else if (!CommonUtil.isNull(song.getTitle())) {
            title = song.getTitle();
        }
        if (title == null
                || title.replace("-", "").length() + 1 < title.length()
                || title.contains("??")) {
            // 名字出现了两次以上-，或出现多个问号
            title = song.getFileName().substring(0, song.getFileName().lastIndexOf("."));
        }
        return title;
    }


    /**
     * 从媒体库加载封面
     *
     * @param mContext
     * @param albumId
     * @return
     */
    private static Bitmap loadCoverFromMediaStore(Context mContext, Long albumId) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (FileNotFoundException ignored) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(is, null, options);
    }

    private static Uri getMediaStoreAlbumCoverUri(Long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }

    /**
     * 加载封面
     *
     * @param mContext
     * @param song
     * @return
     */
    public static Bitmap loadCover(Context mContext, Song song) {
        String filePath = song.getFilePath();
        filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = loadCoverFromMediaStore(mContext, song.getAlbumId());
        if (bitmap == null) {
            return bitmap;
        }
        saveCoverFile(filePath, bitmap);
        return bitmap;
    }

    /**
     * 保存封面图片
     *
     * @param filePath
     * @param bitmap
     */
    public static void saveCoverFile(String filePath, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            // 存入本地
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
