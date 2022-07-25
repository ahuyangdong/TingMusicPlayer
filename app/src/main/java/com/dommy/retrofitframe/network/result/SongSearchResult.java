package com.dommy.retrofitframe.network.result;

import com.dommy.music.bean.SongSearch;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌曲搜索网络请求结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SongSearchResult extends BaseResult {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<SongSearch> lists;
    }
}
