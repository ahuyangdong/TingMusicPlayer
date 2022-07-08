package com.dommy.music.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dommy.music.R;

/**
 * 自定义的Toast显示
 */
public class NoticeToast {
    private static View view; // notice界面
    private static TextView tvMessage; // 提示文本
    private static Toast toast;

    /**
     * 显示Toast提示
     *
     * @param context
     * @param message 提示内容
     */
    public static void show(Context context, String message) {
        initViews(context);
        tvMessage.setText(message);
        toast.show();
    }

    /**
     * 显示Toast提示
     *
     * @param context
     * @param message 提示内容
     */
    public static void showLong(Context context, String message) {
        initViews(context);
        tvMessage.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 在底部显示Toast提示
     *
     * @param context
     * @param message 提示内容
     */
    public static void showAtBottom(Context context, String message) {
        initViews(context);
        tvMessage.setText(message);
        toast.show();
    }

    /**
     * 显示Toast提示
     *
     * @param context
     * @param resId   string资源ID
     */
    public static void show(Context context, int resId) {
        initViews(context);
        tvMessage.setText(resId);
        toast.show();
    }

    private static void initViews(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.toast_notice, null);
            tvMessage = (TextView) view.findViewById(R.id.txt_toast_message);
            // 设置Toast的位置
            toast.setDuration(Toast.LENGTH_SHORT);
            // 让Toast显示为我们自定义的窗体
            toast.setView(view);
        }
    }
}
