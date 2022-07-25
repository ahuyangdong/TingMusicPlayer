package com.dommy.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dommy.music.adapter.MainFragmentAdapter;
import com.dommy.music.adapter.SongListAdapter;
import com.dommy.music.bean.Song;
import com.dommy.music.service.DBService;
import com.dommy.music.util.CommonUtil;
import com.dommy.music.util.Constant;
import com.dommy.music.util.MediaUtil;
import com.dommy.music.util.PreferenceUtil;
import com.dommy.music.widget.AudioPlayer;
import com.dommy.music.widget.LoadingDialog;
import com.dommy.music.widget.NoticeToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DiskFragment.OnFragmentInteractionListener,
        LyricFragment.OnFragmentInteractionListener {
    @BindView(R.id.btn_loop)
    ImageButton btnLoop; // 循环模式
    @BindView(R.id.txt_song_name)
    TextView tvSongName; // 歌曲名称
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView; // 列表
    @BindView(R.id.relative_audio)
    RelativeLayout relativeAudio; // 音频播放组件
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private MainFragmentAdapter mainFragmentAdapter;
    private AudioPlayer audioPlayer; // 音频播放器
    private SongListAdapter songListAdapter;
    private LinearLayoutManager layoutManager;
    private List<Song> mSongList;
    private Song currentSong;
    private int loopStyle;
    private int currentPlay; // 当前播放歌曲序号
    private int currentPlayTime; // 当前播放歌曲时间
    private String showTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();

        loadData();
    }

    private void initView() {
        loopStyle = PreferenceUtil.getInt(MainActivity.this, Constant.PREF_LOOP_STYLE, Constant.LOOP_ORDER);
        if (loopStyle == Constant.LOOP_ORDER) {
            btnLoop.setImageResource(R.drawable.car_main_circle_selector);
        } else if (loopStyle == Constant.LOOP_SINGLE) {
            btnLoop.setImageResource(R.drawable.car_main_singlecycle_selector);
        } else if (loopStyle == Constant.LOOP_RANDOM) {
            btnLoop.setImageResource(R.drawable.car_main_shuffle_selector);
        }
        int listVisible = PreferenceUtil.getInt(MainActivity.this, Constant.PREF_LIST_VISIBLE, View.VISIBLE);
        if (listVisible == View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        songListAdapter = new SongListAdapter(this, R.layout.item_song, null);
        recyclerView.setAdapter(songListAdapter);

        songListAdapter.openLoadAnimation();
        songListAdapter.setOnItemClickListener(itemClickListener);

        // 播放控制全部在插件里面
        audioPlayer = new AudioPlayer(this, relativeAudio, onStateChangeListener);

        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainFragmentAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void loadData() {
        showSongList();

        currentPlay = PreferenceUtil.getInt(this, Constant.PREF_PLAY_CURRENT, 0);
        currentPlayTime = PreferenceUtil.getInt(this, Constant.PREF_PLAY_TIME, 0);
        if (currentPlay >= mSongList.size()) {
            currentPlay = 0;
        }
        flushListState(currentPlay);
        playSong(currentPlay, currentPlayTime);
    }

    /**
     * 扫描所有音乐文件，并存入数据库
     */
    @OnClick(R.id.btn_scan)
    void scan() {
        if (!checkPermissions()) {
            return;
        }
        audioPlayer.pause();
        LoadingDialog.show(this);
        // 查询媒体库
        List<Song> songList = MediaUtil.scanAllAudioFiles(this);
        // 保存至数据库
        DBService.clearSongList();
        DBService.saveSongList2DB(songList);
        LoadingDialog.close();

        showSongList();

        if (currentPlay >= mSongList.size()) {
            currentPlay = 0;
        }
        flushListState(currentPlay);
        playSong(currentPlay, 0);
    }

    /**
     * 切换播放顺序
     */
    @OnClick(R.id.btn_loop)
    void toggleLoop() {
        if (loopStyle == Constant.LOOP_RANDOM) {
            loopStyle = Constant.LOOP_ORDER;
            btnLoop.setImageResource(R.drawable.car_main_circle_selector);
            NoticeToast.show(this, "顺序播放");
        } else if (loopStyle == Constant.LOOP_ORDER) {
            loopStyle = Constant.LOOP_SINGLE;
            btnLoop.setImageResource(R.drawable.car_main_singlecycle_selector);
            NoticeToast.show(this, "单曲循环");
        } else if (loopStyle == Constant.LOOP_SINGLE) {
            loopStyle = Constant.LOOP_RANDOM;
            btnLoop.setImageResource(R.drawable.car_main_shuffle_selector);
            NoticeToast.show(this, "随机播放");
        }
        PreferenceUtil.setInt(MainActivity.this, Constant.PREF_LOOP_STYLE, loopStyle);
    }

    /**
     * 切换列表显示状态
     */
    @OnClick(R.id.btn_list_state)
    void toggleList() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            PreferenceUtil.setInt(MainActivity.this, Constant.PREF_LIST_VISIBLE, View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            PreferenceUtil.setInt(MainActivity.this, Constant.PREF_LIST_VISIBLE, View.VISIBLE);
        }
    }

    /**
     * 显示歌曲列表
     */
    private void showSongList() {
        LoadingDialog.show(this);
        // 显示到列表上
        mSongList = DBService.getSongListFromDB();
        if (mSongList == null) {
            mSongList = new ArrayList<>();
        }
        flushListState(currentPlay);
        LoadingDialog.close();
    }

    /**
     * 播放下一首
     */
    @OnClick(R.id.btn_next)
    void playNextSong() {
        if (loopStyle == Constant.LOOP_RANDOM) {
            Random random = new Random();
            currentPlay = random.nextInt(mSongList.size());
        } else if (loopStyle == Constant.LOOP_ORDER) {
            currentPlay = (currentPlay + 1) % mSongList.size();
        } else if (loopStyle == Constant.LOOP_SINGLE) {
            // 单曲循环
        }
        flushListState(currentPlay);
        playSong(currentPlay, 0);
    }

    /**
     * 播放上一首
     */
    @OnClick(R.id.btn_prev)
    void playPrevSong() {
        if (loopStyle == Constant.LOOP_RANDOM) {
            Random random = new Random();
            currentPlay = random.nextInt(mSongList.size());
        } else if (loopStyle == Constant.LOOP_ORDER) {
            currentPlay = (currentPlay - 1) % mSongList.size();
            if (currentPlay < 0) {
                currentPlay = mSongList.size() - 1;
            }
        } else if (loopStyle == Constant.LOOP_SINGLE) {
            // 单曲循环
        }
        flushListState(currentPlay);
        playSong(currentPlay, 0);
    }

    /**
     * 播放特定索引的歌曲
     *
     * @param index
     */
    private void playSong(int index, int startTime) {
        if (mSongList.size() == 0) {
            return;
        }
        currentSong = mSongList.get(index);
        // 显示歌曲名称
        showTitle = MediaUtil.getSongShowTitle(currentSong);
        tvSongName.setText(showTitle);
        audioPlayer.play(currentSong.getFilePath(), startTime);

        DiskFragment diskFragment = getDiskFragment();
        if (diskFragment != null) {
            // 设置专辑封面
            onDiskLoaded();
        }
        LyricFragment lyricFragment = getLyricFragment();
        if (lyricFragment != null) {
            // 显示歌词
            onLyricLoaded();
        }
        PreferenceUtil.setInt(MainActivity.this, Constant.PREF_PLAY_CURRENT, currentPlay);
    }

    private BaseQuickAdapter.OnItemClickListener itemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            // 播放当前文件
            flushListState(position);
            currentPlay = position;
            playSong(position, 0);
        }
    };

    /**
     * 刷新列表中的数据状态
     *
     * @param position
     */
    private void flushListState(int position) {
        for (int i = 0; i < mSongList.size(); i++) {
            Song song = mSongList.get(i);
            if (i == position) {
                song.setSelected(true);
            } else {
                song.setSelected(false);
            }
        }
        songListAdapter.setNewData(mSongList);
        recyclerView.scrollToPosition(position);
    }

    private DiskFragment getDiskFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() <= 0) {
            return null;
        }
        return (DiskFragment) fragments.get(0);
    }

    private LyricFragment getLyricFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() <= 1) {
            return null;
        }
        return (LyricFragment) fragments.get(1);
    }

    private AudioPlayer.OnStateChangeListener onStateChangeListener = new AudioPlayer.OnStateChangeListener() {
        @Override
        public void start() {
            DiskFragment diskFragment = getDiskFragment();
            if (diskFragment != null) {
                diskFragment.startAnimation(false);
            }
        }

        @Override
        public void pause() {
            DiskFragment diskFragment = getDiskFragment();
            if (diskFragment != null) {
                diskFragment.stopAnimation();
            }
        }

        @Override
        public void complete() {
            playNextSong();
        }

        @Override
        public void progressChanged(int newProgress) {
            LyricFragment lyricFragment = getLyricFragment();
            if (lyricFragment != null) {
                lyricFragment.setDuration(newProgress);
            }
            PreferenceUtil.setInt(MainActivity.this, Constant.PREF_PLAY_TIME, newProgress);
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                NoticeToast.showLong(this, "请至权限中心打开本应用的读写权限");
            }
            // 申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQ_READ_WRITE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_READ_WRITE:
                // 权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    scan();
                } else {
                    // 被禁止授权
                    NoticeToast.showLong(MainActivity.this, "请至权限中心打开本应用的相关权限");
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停，释放内存
        if (audioPlayer != null) {
            audioPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }

    @Override
    public void onDiskLoaded() {
        if (currentSong == null) {
            return;
        }
        // 加载播放页数据
        DiskFragment diskFragment = getDiskFragment();
        Bitmap albumBitmap = MediaUtil.loadCoverFromMediaStore(this, currentSong.getAlbumId());
        diskFragment.setImgAlbum(albumBitmap);
        diskFragment.setCursorText((currentPlay + 1) + "/" + mSongList.size());
        diskFragment.stopAnimation();
        diskFragment.startAnimation(true);
    }

    @Override
    public void onLyricLoaded() {
        if (currentSong == null) {
            return;
        }
        LyricFragment lyricFragment = getLyricFragment();
        lyricFragment.setCurrentPlay(showTitle);
    }

    @Override
    public void onLrcDragged(int duration) {
        audioPlayer.seekTo(duration);
    }
}
