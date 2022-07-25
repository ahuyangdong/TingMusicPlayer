package com.dommy.music;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dommy.music.bean.Song;
import com.dommy.music.util.CommonUtil;
import com.dommy.music.util.KuGouLrcUtil;
import com.dommy.music.util.MediaUtil;
import com.dommy.music.util.NetworkUtil;
import com.dommy.music.widget.NoticeToast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.wcy.lrcview.LrcView;


/**
 * LRC歌词显示页面
 */
public class LyricFragment extends Fragment {
    @BindView(R.id.lrc_view)
    LrcView lrcView; // 歌词控件

    private LyricFragment.OnFragmentInteractionListener mListener;
    private Song currentPlay; // 当前播放歌曲

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyric, container, false);

        ButterKnife.bind(this, view);

        initView();

        if (mListener != null) {
            mListener.onLyricLoaded();
        }
        return view;
    }

    private void initView() {
        lrcView.setDraggable(true, new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(LrcView view, long time) {
                mListener.onLrcDragged((int) time);
                return false;
            }
        });
    }

    /**
     * 设置播放数值显示内容
     *
     * @param song
     */
    public void setCurrentPlay(Song song) {
        currentPlay = song;
        lrcView.updateTime(0);
        loadLrc();
    }

    /**
     * 设置歌词显示时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        lrcView.updateTime(duration);
    }

    /**
     * 加载LRC歌词内容
     */
    private void loadLrc() {
        // 读取本地文件
        String filePath = currentPlay.getFilePath();
        filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".lrc";
        String content = CommonUtil.readFile(filePath);
        if (!CommonUtil.isNull(content)) {
            lrcView.loadLrc(content);
            return;
        }
        // 读取网络歌词
        String showTitle = MediaUtil.getSongShowTitle(currentPlay);
        KuGouLrcUtil.getLrcUrl(getContext(), showTitle, new KuGouLrcUtil.GetLrcUrlListener() {
            @Override
            public void onLrcUrl(String lrcUrl) {
                loadLrc(lrcUrl);
            }

            @Override
            public void onFailure() {
                if (NetworkUtil.isNetworkConnected(getContext())) {
                    lrcView.setLabel("暂无歌词");
                } else {
                    lrcView.setLabel("暂无歌词，请连接网络后重试");
                }
            }
        });
    }

    private void loadLrc(String lrcUrl) {
        KuGouLrcUtil.getLrc(getContext(), lrcUrl, new KuGouLrcUtil.GetLrcListener() {

            @Override
            public void onLrcLoad(String lrc) {
                lrc = new String(Base64.decode(lrc, Base64.DEFAULT));
                lrcView.loadLrc(lrc);
                // 存入本地文件
                String filePath = currentPlay.getFilePath();
                filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".lrc";
                CommonUtil.writeFile(filePath, lrc);
            }

            @Override
            public void onFailure() {
                if (NetworkUtil.isNetworkConnected(getContext())) {
                    lrcView.setLabel("暂无歌词");
                } else {
                    lrcView.setLabel("暂无歌词，请连接网络后重试");
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LyricFragment.OnFragmentInteractionListener) {
            mListener = (LyricFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * 交互监听
     */
    public interface OnFragmentInteractionListener {
        /**
         * Fragment已加载
         */
        void onLyricLoaded();

        /**
         * LRC拖拽效果
         *
         * @param duration
         */
        void onLrcDragged(int duration);
    }
}
