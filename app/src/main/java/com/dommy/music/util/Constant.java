package com.dommy.music.util;

/**
 * 常量
 */
public class Constant {
    // preference
    public static final String PREF_PLAY_CURRENT = "play_current"; // 当前播放序号
    public static final String PREF_PLAY_TIME = "play_time"; // 播放时间

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
}
