package com.dommy.music.bean;

import com.dommy.retrofitframe.network.result.BaseResult;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
