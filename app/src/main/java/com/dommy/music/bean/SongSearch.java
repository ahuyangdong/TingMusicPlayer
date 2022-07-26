package com.dommy.music.bean;

import lombok.Data;

/**
 * 酷狗歌曲搜索结果对象
 */
@Data
public class SongSearch {
    private String SQFileHash;
    private String SingerName;
    private int HQDuration;
    private String SongName;
}
