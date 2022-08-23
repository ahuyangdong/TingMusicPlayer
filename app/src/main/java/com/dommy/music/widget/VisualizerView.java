package com.dommy.music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.dommy.music.R;

/**
 * 音乐频谱视图
 */
public class VisualizerView extends View {
    private int visualColor; //频块颜色
    private int visualNum; //频块数量
    private float visualMargin; //频块间距
    private float strokeWidth = 0;

    /**
     * It is the paint which is used to draw to visual effect.
     */
    protected Paint mPaint = null;//画笔

    /**
     * It is the buffer of fft.
     */
    protected byte[] mData;//音量柱 数组

    /**
     * It constructs the base visualizer view.
     * 构造函数初始化画笔
     *
     * @param context It is the context of the view owner.
     */
    public VisualizerView(Context context) {
        this(context, null);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VisualizerView, 0, 0);
        visualColor = ta.getColor(R.styleable.VisualizerView_visual_color, Color.RED);
        visualNum = ta.getInteger(R.styleable.VisualizerView_visual_num, 10);
        visualMargin = ta.getDimension(R.styleable.VisualizerView_visual_margin, 5.0f);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();//初始化画笔工具
        mPaint.setAntiAlias(true);//抗锯齿
        mData = new byte[visualNum];
    }

    //执行 Layout 操作
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        strokeWidth = (getWidth() - (visualNum - 1) * visualMargin) / visualNum;
        mPaint.setStrokeWidth(strokeWidth); //设置频谱块宽度
    }

    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(visualColor);//画笔颜色
        if (mData == null) {
            return;
        }
        float[] mPoints = new float[mData.length * 4];
        Rect mRect = new Rect();
        mRect.set(0, 0, getWidth(), getHeight());
        //绘制频谱
        final int baseX = mRect.width() / visualNum;
        final int height = mRect.height();

        for (int i = 0; i < visualNum; i++) {
            if (mData[i] < 0) {
                mData[i] = 127;
            }
            int xi = baseX * i + baseX / 2;
            mPoints[i * 4] = xi;
            mPoints[i * 4 + 1] = height;
            mPoints[i * 4 + 2] = xi;
            mPoints[i * 4 + 3] = height - mData[i];
        }
        canvas.drawLines(mPoints, mPaint);
    }

    /**
     * 更新频谱数据
     *
     * @param fft
     */
    public void updateFft(byte[] fft) {
        byte[] model = new byte[fft.length / 2 + 1];
        model[0] = (byte) Math.abs(fft[1]);
        int j = 1;
        for (int i = 2; i < fft.length; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        mData = model;
        postInvalidate();//刷新界面
    }

}
