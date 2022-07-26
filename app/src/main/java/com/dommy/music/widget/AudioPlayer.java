package com.dommy.music.widget;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dommy.music.R;
import com.dommy.music.util.CommonUtil;
import com.dommy.music.util.MyTimer;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自定义音频播放器组件
 */
public class AudioPlayer implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final int TIME_INTEVAL = 500;
    private static final int STATE_START = 1;
    private static final int STATE_PAUSE = 2;
    private static final int MSG_PROGRESS = 1;
    private static final int MSG_ERROR = 2;
    private Activity activity;
    private MediaPlayer mediaPlayer; // 媒体播放器

    @BindView(R.id.seekbar_player)
    SeekBar seekBar; // 进度条
    @BindView(R.id.txt_start_time)
    TextView tvStart; // 当前时间
    @BindView(R.id.txt_end_time)
    TextView tvEnd; // 结束时间
    @BindView(R.id.btn_start)
    ImageButton btnStart; // 播放控制

    private MyTimer secondTimer; // 秒级任务
    private boolean isPrepared; // 初始化成功

    private OnStateChangeListener onStateChangeListener;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_PROGRESS) {
                updateProgress();
            } else if (msg.what == MSG_ERROR) {
                error();
            }
        }
    };

    public AudioPlayer(Activity activity, View rootView, OnStateChangeListener onCompleteListener) {
        this.activity = activity;
        this.onStateChangeListener = onCompleteListener;
        ButterKnife.bind(this, rootView);

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        secondTimer = new MyTimer(activity) {
            @Override
            public void timerCallBack() {
                //  更新进度
                handler.sendEmptyMessage(MSG_PROGRESS);
            }
        };
    }

    @OnClick(R.id.btn_start)
    void startPlay() {
        if (mediaPlayer.getDuration() <= 10) {
            return;
        }
        startPlay(-1);
    }

    /**
     * 开始播放
     *
     * @param startTime
     */
    private void startPlay(int startTime) {
        secondTimer.stopTimer();
        if (mediaPlayer.isPlaying()) {
            // 本次暂停
            mediaPlayer.pause();
            setState(STATE_START);

            onStateChangeListener.pause();
        } else {
            // 本次播放
            if (startTime > 0) {
                mediaPlayer.seekTo(startTime);
            }
            mediaPlayer.start();
            secondTimer.startLoopTimer(TIME_INTEVAL);
            setState(STATE_PAUSE);

            onStateChangeListener.start();
        }
    }

    /**
     * 设置播放地址
     *
     * @param filePath
     */
    public void play(String filePath, int startTime) {
        if (CommonUtil.isNull(filePath)) {
            NoticeToast.show(activity, "抱歉，暂无音频源");
            return;
        }
        try {
            mediaPlayer.pause();
            setState(STATE_START);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            startPlay(startTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        onStateChangeListener.pause();
        setState(STATE_START);
    }

    /**
     * 销毁播放器
     */
    public void release() {
        if (secondTimer != null) {
            secondTimer.stopTimer();
        }
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void updateProgress() {
        try {
            int position = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(position);
        } catch (Exception e) {
        }
    }

    /**
     * 外部快进到制定时间
     *
     * @param duration
     */
    public void seekTo(int duration) {
        mediaPlayer.seekTo(duration);
        updateProgress();
    }

    /**
     * 设置播放图标状态
     *
     * @param state
     */
    private void setState(int state) {
        switch (state) {
            case STATE_START:
                btnStart.setImageResource(R.drawable.car_play_selector);
                break;
            case STATE_PAUSE:
                btnStart.setImageResource(R.drawable.car_pause_selector);
                break;
        }
    }

    /**
     * 加载出错处理
     */
    private void error() {
        NoticeToast.show(activity, "播放音频源出错");
        setState(STATE_START);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // 更新缓冲进度条
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        // 进度条总长度设为音频总长度
        seekBar.setMax(mp.getDuration());
        tvEnd.setText(DateUtils.formatElapsedTime(mp.getDuration() / 1000));
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        error();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        secondTimer.stopTimer();
        setState(STATE_START);

        onStateChangeListener.complete();
    }

    /**
     * 进度条监听
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (tvStart != null) {
                tvStart.setText(DateUtils.formatElapsedTime(progress / 1000));
            }
            onStateChangeListener.progressChanged(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 被拖动
            int progress = seekBar.getProgress();
            // 因为max progress与音频长度一致，所以拖到哪就播放哪里
            if (isPrepared) {
                secondTimer.stopTimer();
                secondTimer.startLoopTimer(TIME_INTEVAL);
                mediaPlayer.seekTo(progress);
            }
        }
    };

    public interface OnStateChangeListener {
        /**
         * 开始播放
         */
        void start();

        /**
         * 暂停
         */
        void pause();

        /**
         * 完成
         */
        void complete();

        /**
         * 进度变化
         *
         * @param newProgress
         */
        void progressChanged(int newProgress);
    }

}
