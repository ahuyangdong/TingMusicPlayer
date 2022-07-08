package com.dommy.music.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 歌曲列表数据对象
 */
@Entity
public class Song {
    @Id(autoincrement = true)
    private Long id;
    @Property
    private String title; // 标题
    @Property
    private String author; // 歌手
    @Property
    private String album; // 专辑名
    @Property
    private Long albumId; // 专辑ID
    @Property
    private String fileName; // 文件名称
    @Property
    private String filePath; // 文件路径
    @Property
    private Integer duration; // 时长
    @Transient
    private boolean selected; // 是否选中

    @Generated(hash = 883003868)
    public Song(Long id, String title, String author, String album, Long albumId,
            String fileName, String filePath, Integer duration) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.album = album;
        this.albumId = albumId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.duration = duration;
    }
    @Generated(hash = 87031450)
    public Song() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAlbum() {
        return this.album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public Long getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public Integer getDuration() {
        return this.duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean getSelected() {
        return this.selected;
    }
}
