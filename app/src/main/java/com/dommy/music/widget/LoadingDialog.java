package com.dommy.music.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dommy.music.R;

/**
 * 加载或处理提示
 */
public class LoadingDialog {
    private static View view;
    private static TextView tvMessage;
    private static AlertDialog dialog;

    public static void show(Context context) {
        initViews(context);
        tvMessage.setText(R.string.note_processing);
        dialog.show();
    }

    public static void show(Context context, String message) {
        initViews(context);
        tvMessage.setText(message);
        dialog.show();
    }

    public static void show(Context context, int resId) {
        initViews(context);
        tvMessage.setText(resId);
        dialog.show();
    }

    public static void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private static void initViews(Context context) {
        if (view == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
            tvMessage = (TextView) view.findViewById(R.id.txt_dialog_message);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);

            dialog = builder.create();
            dialog.setCancelable(false);
        }
    }

}
