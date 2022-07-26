package com.dommy.music.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 歌词选择对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LrcSelect {
    private String title;
    private String duration;
}
