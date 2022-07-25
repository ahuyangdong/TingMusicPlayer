package com.dommy.retrofitframe.network.result;

import com.dommy.music.bean.SongSearch;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌曲LRC请求码请求结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LrcAccessResult extends BaseResult {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private String id;
        private String accesskey;
    }
}
