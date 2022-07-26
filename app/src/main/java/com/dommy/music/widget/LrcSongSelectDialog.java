package com.dommy.music.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dommy.music.R;
import com.dommy.music.adapter.LrcSelectListAdapter;
import com.dommy.music.bean.LrcSelect;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * LRC选择歌曲名称
 */
public class LrcSongSelectDialog {
    @BindView(R.id.txt_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView; // 列表

    private Context context;
    private OnClickListener listener;

    private Dialog dialog;
    private LinearLayoutManager layoutManager;
    private LrcSelectListAdapter lrcSelectListAdapter;

    public LrcSongSelectDialog(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_lrc_song, null);
        ButterKnife.bind(this, layout);

        initView(layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        builder.setCancelable(false);
        dialog = builder.create();
    }

    private void initView(View layout) {
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        lrcSelectListAdapter = new LrcSelectListAdapter(context, R.layout.item_lrc_select, null);
        recyclerView.setAdapter(lrcSelectListAdapter);
        lrcSelectListAdapter.openLoadAnimation();
        lrcSelectListAdapter.setOnItemClickListener(itemClickListener);
    }

    /**
     * 修改标题名称
     *
     * @param text
     */
    public void setTitle(String text) {
        tvTitle.setText(text);
    }

    /**
     * 设置列表数据
     *
     * @param lrcList
     */
    public void setList(List<LrcSelect> lrcList) {
        lrcSelectListAdapter.setNewData(lrcList);
    }

    public void close() {
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }

    private BaseQuickAdapter.OnItemClickListener itemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            listener.onConfirm(position);
        }
    };

    public interface OnClickListener {
        void onConfirm(int position);
    }
}
