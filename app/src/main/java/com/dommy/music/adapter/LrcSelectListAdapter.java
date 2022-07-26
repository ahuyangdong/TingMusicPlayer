package com.dommy.music.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dommy.music.R;
import com.dommy.music.bean.LrcSelect;

import java.util.List;

/**
 * 歌词选择列表适配器
 */
public class LrcSelectListAdapter extends BaseQuickAdapter<LrcSelect, BaseViewHolder> {
    private Context mContext;

    public LrcSelectListAdapter(Context context, int layoutResId, @Nullable List data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, LrcSelect item) {
        helper.setText(R.id.txt_title, item.getTitle());
        helper.setText(R.id.txt_duration, item.getDuration());
    }

}
