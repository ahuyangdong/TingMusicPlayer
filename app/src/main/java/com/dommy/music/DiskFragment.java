package com.dommy.music;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 唱片页
 */
public class DiskFragment extends Fragment {
    @BindView(R.id.img_album)
    ShapeableImageView imgAlbum; // 封面
    @BindView(R.id.img_needle)
    ImageView imgNeedle; // 唱针
    @BindView(R.id.txt_cursor)
    TextView tvCursor; // 当前序号
    Animator animAudio; // 旋转动画

    private OnFragmentInteractionListener mListener;

    public DiskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disk, container, false);
        ButterKnife.bind(this, view);

        animAudio = AnimatorInflater.loadAnimator(getContext(), R.animator.anim_disk);
        // 均匀转动
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        animAudio.setInterpolator(linearInterpolator);
        animAudio.setTarget(imgAlbum);

        if (mListener != null) {
            mListener.onDiskLoaded();
        }
        return view;
    }

    /**
     * 设置专辑封面图象
     *
     * @param albumBitmap
     */
    public void setImgAlbum(Bitmap albumBitmap) {
        if (albumBitmap == null) {
            imgAlbum.setImageResource(R.drawable.default_post);
        } else {
            imgAlbum.setImageBitmap(albumBitmap);
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
     * 停止旋转动画
     */
    public void stopAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animAudio.pause();
        }
    }

    /**
     * 开启旋转动画
     */
    public void startAnimation(boolean isNewStart) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isNewStart) {
                animAudio.cancel();
                animAudio.start();
            } else if (animAudio.isPaused()) {
                animAudio.resume();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        void onDiskLoaded();
    }
}
