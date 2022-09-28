package com.dommy.retrofitframe.network.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌曲封面地址请求结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CoverGetResult extends BaseResult {
    private Data data;

    @lombok.Data
    public static class Data {
        // 封面图地址
        private String img;
    }
}
