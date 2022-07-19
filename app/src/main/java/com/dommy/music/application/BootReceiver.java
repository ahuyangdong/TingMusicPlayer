package com.dommy.music.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dommy.music.MainActivity;

/**
 * 开机接收广播
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android设备开机时会发送一条开机广播："android.intent.action.BOOT_COMPLETED"
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }
}
