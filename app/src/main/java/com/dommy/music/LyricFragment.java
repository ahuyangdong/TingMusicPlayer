package com.dommy.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.dommy.music.bean.LrcSelect;
import com.dommy.music.bean.Song;
import com.dommy.music.bean.SongSearch;
import com.dommy.music.util.CommonUtil;
import com.dommy.music.util.Constant;
import com.dommy.music.util.KuGouLrcUtil;
import com.dommy.music.util.MediaUtil;
import com.dommy.music.util.NetworkUtil;
import com.dommy.music.util.PreferenceUtil;
import com.dommy.music.widget.LoadingDialog;
import com.dommy.music.widget.LrcSongSelectDialog;
import com.dommy.music.widget.NoticeToast;
import com.dommy.music.widget.VisualizerView;
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
    @BindView(R.id.linear_album)
    LinearLayout linearAlbum; // 封面
    @BindView(R.id.img_album)
    ImageView imgAlbum; // 封面
    @BindView(R.id.txt_cursor)
    TextView tvCursor; // 当前序号
    @BindView(R.id.visualizer_view)
    VisualizerView visualizerView; // 频谱

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
        int listVisible = PreferenceUtil.getInt(getContext(), Constant.PREF_LIST_VISIBLE, View.VISIBLE);
        if (listVisible == View.VISIBLE) {
            setImgAlbumVisible(false);
        } else {
            setImgAlbumVisible(true);
        }
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
            public void onConfirm(int position, boolean isLrc, String coverFilePath) {
                SongSearch songSearch = songSearchList.get(position);
                if (songSearch == null) {
                    return;
                }
                String hash = songSearch.getSQFileHash();
                if (CommonUtil.isNull(hash)) {
                    if (isLrc) {
                        requestLrcFailure();
                    } else {
                        requestCoverFailure();
                    }
                    return;
                }
                if (isLrc) {
                    loadLrcAccessKey(hash);
                } else {
                    String url = String.format(Constant.URL_COVER, hash);
                    loadCover(url, coverFilePath);
                }
                lrcSongSelectDialog.close();
            }
        });
        // 选择歌词
        lrcSelectDialog = new LrcSongSelectDialog(getContext(), new LrcSongSelectDialog.OnClickListener() {
            @Override
            public void onConfirm(int position, boolean isLrc, String coverFilePath) {
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
     * 更新封面
     */
    @OnClick(R.id.img_album)
    public void updateCover() {
        if (currentPlay == null) {
            return;
        }
        if (!NetworkUtil.isNetworkConnected(getContext())) {
            NoticeToast.show(getContext(), "暂无封面，请连接网络后重试");
            return;
        }
        isUserDownload = true;
        LoadingDialog.show(getContext());
        showSongSelect(false);
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
     * 更新频谱
     *
     * @param fft
     */
    public void updateFft(byte[] fft) {
        visualizerView.updateFft(fft);
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
        showSongSelect(true);
    }

    /**
     * 显示歌曲选择列表
     *
     * @param isLrc
     */
    private void showSongSelect(boolean isLrc) {
        String showTitle = MediaUtil.getSongShowTitle(currentPlay);
        String coverFilePath = getCoverFilePath();
        KuGouLrcUtil.getSongSearchList(getContext(), showTitle, new KuGouLrcUtil.GetSongListListener() {
            @Override
            public void onSongList(List<SongSearch> songSearchList) {
                LoadingDialog.close();
                List<LrcSelect> lrcList = new ArrayList<>();
                List<SongSearch> songSearchListNew = new ArrayList<>();
                for (SongSearch songSearch : songSearchList) {
                    if (CommonUtil.isNull(songSearch.getSQFileHash())) {
                        continue;
                    }
                    LrcSelect lrcSelect = new LrcSelect(songSearch.getSongName(), songSearch.getSingerName());
                    lrcList.add(lrcSelect);
                    songSearchListNew.add(songSearch);
                }
                if (lrcList.size() == 0) {
                    if (isLrc) {
                        requestLrcFailure();
                    } else {
                        requestCoverFailure();
                    }
                    return;
                }

                if (isUserDownload) {
                    // 弹窗
                    LyricFragment.this.songSearchList = songSearchListNew;
                    lrcSongSelectDialog.setList(lrcList);
                    lrcSongSelectDialog.setLrc(isLrc);
                    lrcSongSelectDialog.setCoverFilePath(coverFilePath);
                    lrcSongSelectDialog.show();
                } else {
                    if (isLrc) {
                        SongSearch songSearch = songSearchListNew.get(0);
                        loadLrcAccessKey(songSearch.getSQFileHash());
                    } else {
                        SongSearch songSearch = songSearchListNew.get(0);
                        String url = String.format(Constant.URL_COVER, songSearch.getSQFileHash());
                        loadCover(url, coverFilePath);
                    }
                }
            }

            @Override
            public void onFailure() {
                LoadingDialog.close();
                if (isLrc) {
                    requestLrcFailure();
                } else {
                    requestCoverFailure();
                }
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
                List<LrcSelect> lrcList = new ArrayList<>();
                List<LrcAccessResult.Candidate> candidateListNew = new ArrayList<>();
                for (LrcAccessResult.Candidate candidate : candidateList) {
                    if (CommonUtil.isNull(candidate.getAccesskey())
                            || CommonUtil.isNull(candidate.getId())) {
                        continue;
                    }
                    LrcSelect lrcSelect = new LrcSelect(candidate.getSinger() + "-" + candidate.getSong(),
                            DateUtils.formatElapsedTime(candidate.getDuration() / 1000));
                    lrcList.add(lrcSelect);
                    candidateListNew.add(candidate);
                }
                if (lrcList.size() == 0) {
                    requestLrcFailure();
                    return;
                }
                if (isUserDownload) {
                    // 弹窗
                    LyricFragment.this.candidateList = candidateListNew;
                    lrcSelectDialog.setList(lrcList);
                    lrcSelectDialog.show();
                } else {
                    LrcAccessResult.Candidate candidate = candidateListNew.get(0);
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

    /**
     * 加载封面
     *
     * @param coverGetUrl
     * @param coverFilePath
     */
    private void loadCover(String coverGetUrl, String coverFilePath) {
        if (isUserDownload) {
            LoadingDialog.show(getContext());
        }
        KuGouLrcUtil.getCoverUrl(getContext(), coverGetUrl, new KuGouLrcUtil.GetCoverListener() {

            @Override
            public void onCoverLoad(String coverUrl) {
                LoadingDialog.close();
                readAndSaveCover(coverUrl, coverFilePath);
            }

            @Override
            public void onFailure() {
                LoadingDialog.close();
                requestCoverFailure();
            }
        });
    }

    /**
     * 读取并保存封面
     *
     * @param coverUrl
     * @param coverFilePath
     */
    private void readAndSaveCover(String coverUrl, String coverFilePath) {
        Glide.with(getContext()).load(coverUrl).asBitmap()
                .into(new BitmapImageViewTarget(imgAlbum) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        if (resource == null) {
                            requestCoverFailure();
                            return;
                        }
                        MediaUtil.saveCoverFile(coverFilePath, resource);
                        Bitmap albumBitmap = MediaUtil.loadCover(getContext(), currentPlay);
                        setImgAlbum(albumBitmap);
                        mListener.onCurrentCoverUpdate();
                    }
                });
    }

    /**
     * 加载封面失败
     */
    private void requestCoverFailure() {
        if (isUserDownload) {
            if (NetworkUtil.isNetworkConnected(getContext())) {
                NoticeToast.show(getContext(), "暂无封面");
            } else {
                NoticeToast.show(getContext(), "暂无封面，请连接网络后重试");
            }
            isUserDownload = false;
        }
        imgAlbum.setImageResource(R.drawable.default_post);
    }

    /**
     * 加载歌词失败
     */
    private void requestLrcFailure() {
        if (NetworkUtil.isNetworkConnected(getContext())) {
            lrcView.loadLrc("[00:00.01]暂无歌词");
        } else {
            lrcView.loadLrc("[00:00.01]暂无歌词，请连接网络后重试");
        }
        isUserDownload = false;
        lrcSongSelectDialog.close();
        lrcSelectDialog.close();
    }

    /**
     * 设置专辑封面图象
     *
     * @param albumBitmap
     */
    public void setImgAlbum(Bitmap albumBitmap) {
        if (albumBitmap == null) {
            imgAlbum.setImageResource(R.drawable.default_post);
            showSongSelect(false);
        } else {
            imgAlbum.setImageBitmap(albumBitmap);
        }
    }

    /**
     * 设置专辑控件是否可见
     *
     * @param isVisible
     */
    public void setImgAlbumVisible(boolean isVisible) {
        if (isVisible) {
            linearAlbum.setVisibility(View.VISIBLE);
        } else {
            linearAlbum.setVisibility(View.GONE);
        }
    }

    /**
     * 设置播放数值显示内容
     *
     * @param text
     */
    public void setCursorText(String text) {
        tvCursor.setText(text);
    }

    /**
     * 返回当前播放歌曲的封面文件地址
     *
     * @return
     */
    private String getCoverFilePath() {
        String filePath = currentPlay.getFilePath();
        String coverFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".jpg";
        return coverFilePath;
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

        /**
         * 专辑封面更新
         */
        void onCurrentCoverUpdate();
    }
}
