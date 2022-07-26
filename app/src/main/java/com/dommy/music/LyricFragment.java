package com.dommy.music;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dommy.music.bean.LrcSelect;
import com.dommy.music.bean.Song;
import com.dommy.music.bean.SongSearch;
import com.dommy.music.util.CommonUtil;
import com.dommy.music.util.Constant;
import com.dommy.music.util.KuGouLrcUtil;
import com.dommy.music.util.MediaUtil;
import com.dommy.music.util.NetworkUtil;
import com.dommy.music.widget.LoadingDialog;
import com.dommy.music.widget.LrcSongSelectDialog;
import com.dommy.retrofitframe.network.result.LrcAccessResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.wcy.lrcview.LrcView;


/**
 * LRC歌词显示页面
 */
public class LyricFragment extends Fragment {
    @BindView(R.id.lrc_view)
    LrcView lrcView; // 歌词控件

    private LyricFragment.OnFragmentInteractionListener mListener;
    private Song currentPlay; // 当前播放歌曲
    private List<SongSearch> songSearchList; // 接口返回的歌曲查询列表
    private List<LrcAccessResult.Candidate> candidateList; // 接口返回的歌词查询列表
    private LrcSongSelectDialog lrcSongSelectDialog;
    private LrcSongSelectDialog lrcSelectDialog;
    private boolean isUserDownload; // 是否为用户主动下载歌词
    private String lrcFilePath; // lrc歌词文件地址

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
        // 选择歌曲
        lrcSongSelectDialog = new LrcSongSelectDialog(getContext(), new LrcSongSelectDialog.OnClickListener() {
            @Override
            public void onConfirm(int position) {
                SongSearch songSearch = songSearchList.get(position);
                if (songSearch == null) {
                    return;
                }
                String hash = songSearch.getSQFileHash();
                if (CommonUtil.isNull(hash)) {
                    requestLrcFailure();
                    return;
                }
                loadLrcAccessKey(hash);
                lrcSongSelectDialog.close();
            }
        });
        // 选择歌词
        lrcSelectDialog = new LrcSongSelectDialog(getContext(), new LrcSongSelectDialog.OnClickListener() {
            @Override
            public void onConfirm(int position) {
                LrcAccessResult.Candidate candidate = candidateList.get(position);
                if (candidate == null) {
                    requestLrcFailure();
                    return;
                }
                String url = String.format(Constant.URL_LRC_READ, candidate.getId(), candidate.getAccesskey());
                loadLrc(url);
                lrcSelectDialog.close();
            }
        });
        lrcSelectDialog.setTitle("选择歌词");
    }

    @OnClick(R.id.btn_lrc)
    void updateLrc() {
        isUserDownload = true;
        loadLrc();
    }

    /**
     * 设置播放数值显示内容
     *
     * @param song
     */
    public void setCurrentPlay(Song song) {
        isUserDownload = false;
        currentPlay = song;
        lrcView.loadLrc("[00:00.01]歌词加载中...");
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
        if (!CommonUtil.isNull(content) && !isUserDownload) {
            lrcView.loadLrc(content);
            isUserDownload = false;
            return;
        }
        if (!NetworkUtil.isNetworkConnected(getContext())) {
            lrcView.loadLrc("[00:00.01]暂无歌词，请连接网络后重试");
            isUserDownload = false;
            return;
        }
        if (isUserDownload) {
            LoadingDialog.show(getContext());
        }
        lrcFilePath = filePath;
        // 读取网络歌词
        String showTitle = MediaUtil.getSongShowTitle(currentPlay);
        KuGouLrcUtil.getSongSearchList(getContext(), showTitle, new KuGouLrcUtil.GetSongListListener() {
            @Override
            public void onSongList(List<SongSearch> songSearchList) {
                LoadingDialog.close();
                if (isUserDownload) {
                    // 弹窗
                    List<LrcSelect> lrcList = new ArrayList<>();
                    for (SongSearch songSearch : songSearchList) {
                        LrcSelect lrcSelect = new LrcSelect(songSearch.getSongName(), songSearch.getSingerName());
                        lrcList.add(lrcSelect);
                    }
                    LyricFragment.this.songSearchList = songSearchList;
                    lrcSongSelectDialog.setList(lrcList);
                    lrcSongSelectDialog.show();
                } else {
                    SongSearch songSearch = songSearchList.get(0);
                    if (songSearch == null) {
                        requestLrcFailure();
                        return;
                    }
                    String hash = songSearch.getSQFileHash();
                    if (CommonUtil.isNull(hash)) {
                        requestLrcFailure();
                        return;
                    }
                    loadLrcAccessKey(hash);
                }
            }

            @Override
            public void onFailure() {
                LoadingDialog.close();
                requestLrcFailure();
            }
        });
    }

    /**
     * 请求LRC查询授权码
     *
     * @param hash 歌曲Hash值
     */
    private void loadLrcAccessKey(String hash) {
        if (isUserDownload) {
            LoadingDialog.show(getContext());
        }
        KuGouLrcUtil.getLrcAccessKey(getContext(), hash, new KuGouLrcUtil.GetLrcAccessKeyListener() {
            @Override
            public void onCandidateList(List<LrcAccessResult.Candidate> candidateList) {
                LoadingDialog.close();
                if (isUserDownload) {
                    // 弹窗
                    List<LrcSelect> lrcList = new ArrayList<>();
                    for (LrcAccessResult.Candidate candidate : candidateList) {
                        LrcSelect lrcSelect = new LrcSelect(candidate.getSinger() + "-" + candidate.getSong(),
                                DateUtils.formatElapsedTime(candidate.getDuration() / 1000));
                        lrcList.add(lrcSelect);
                    }
                    LyricFragment.this.candidateList = candidateList;
                    lrcSelectDialog.setList(lrcList);
                    lrcSelectDialog.show();
                } else {
                    LrcAccessResult.Candidate candidate = candidateList.get(0);
                    if (candidate == null) {
                        requestLrcFailure();
                        return;
                    }
                    String url = String.format(Constant.URL_LRC_READ, candidate.getId(), candidate.getAccesskey());
                    loadLrc(url);
                }
            }

            @Override
            public void onFailure() {
                LoadingDialog.close();
                requestLrcFailure();
            }
        });
    }

    /**
     * 加载LRC歌词
     *
     * @param lrcUrl
     */
    private void loadLrc(String lrcUrl) {
        KuGouLrcUtil.getLrc(getContext(), lrcUrl, new KuGouLrcUtil.GetLrcListener() {

            @Override
            public void onLrcLoad(String lrc) {
                lrc = new String(Base64.decode(lrc, Base64.DEFAULT));
                lrcView.loadLrc(lrc);
                // 存入本地文件
                CommonUtil.writeFile(lrcFilePath, lrc);
                isUserDownload = false;
            }

            @Override
            public void onFailure() {
                requestLrcFailure();
            }
        });
    }

    private void requestLrcFailure() {
        if (NetworkUtil.isNetworkConnected(getContext())) {
            lrcView.loadLrc("[00:00.01]暂无歌词");
        } else {
            lrcView.loadLrc("[00:00.01]暂无歌词，请连接网络后重试");
        }
        isUserDownload = false;
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
