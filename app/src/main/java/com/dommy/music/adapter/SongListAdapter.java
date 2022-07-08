package com.dommy.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dommy.music.R;
import com.dommy.music.bean.Song;
import com.dommy.music.util.MediaUtil;

import java.util.List;

/**
 * 歌曲列表适配器
 */
public class SongListAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    private Context mContext;
    private int colorTrans;
    private int colorSongBg;

    public SongListAdapter(Context context, int layoutResId, @Nullable List data) {
        super(layoutResId, data);
        mContext = context;
        colorTrans = context.getResources().getColor(R.color.transparent);
        colorSongBg = context.getResources().getColor(R.color.bg_song_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Song item) {
        helper.setText(R.id.txt_title, item.getFileName());
        helper.setText(R.id.txt_duration, DateUtils.formatElapsedTime(item.getDuration() / 1000));
        if (item.getAlbumId() != null && item.getAlbumId() > 0) {
            Bitmap albumBitmap = MediaUtil.loadCoverFromMediaStore(mContext, item.getAlbumId());
            if (albumBitmap == null) {
                helper.setImageResource(R.id.img_album, R.drawable.default_post);
            } else {
                helper.setImageBitmap(R.id.img_album, albumBitmap);
            }
        } else {
            helper.setImageResource(R.id.img_album, R.drawable.default_post);
        }
        if (item.isSelected()) {
            helper.setBackgroundColor(R.id.linear_item, colorSongBg);
        } else {
            helper.setBackgroundColor(R.id.linear_item, colorTrans);
        }
    }

}
