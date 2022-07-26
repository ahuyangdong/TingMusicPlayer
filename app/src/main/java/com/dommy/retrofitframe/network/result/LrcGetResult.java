package com.dommy.retrofitframe.network.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌曲LRC请求结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LrcGetResult extends BaseResult {
    private String content;
}
