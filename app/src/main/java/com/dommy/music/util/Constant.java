package com.dommy.music.util;

/**
 * 常量
 */
public class Constant {
    // preference
    public static final String PREF_PLAY_CURRENT = "play_current"; // 当前播放序号
    public static final String PREF_PLAY_TIME = "play_time"; // 播放时间
    public static final String PREF_LOOP_STYLE = "loop_style"; // 循环模式
    public static final String PREF_LIST_VISIBLE = "list_visible"; // 列表是否显示

    public static final int REQ_READ_WRITE = 30002; // 读写权限申请

    /**
     * 播放顺序-单曲循环
     */
    public static final int LOOP_SINGLE = 1;
    /**
     * 播放顺序-随机
     */
    public static final int LOOP_RANDOM = -1;
    /**
     * 播放顺序-顺序播放
     */
    public static final int LOOP_ORDER = 0;

    public final static String URL_BASE = "http://krcs.kugou.com";
    /**
     * 搜索歌曲信息
     */
    public final static String URL_SONG_SEARCH = "http://songsearch.kugou.com/song_search_v2?keyword=%s&platform=WebFilter&page=1&pagesize=20";
    /**
     * 获取LRC获取授权
     */
    public final static String URL_LRC_ACCESSKEY = "http://krcs.kugou.com/search?ver=1&man=yes&client=mobi&keyword=&duration=&hash=%s&album_audio_id=";
    /**
     * 读取LRC歌词文本
     */
    public final static String URL_LRC_READ = "http://lyrics.kugou.com/download?&fmt=lrc&ver=1&id=%s&accesskey=%s";
}
