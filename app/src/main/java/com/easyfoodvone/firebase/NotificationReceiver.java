package com.easyfoodvone.firebase;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.easyfoodvone.utility.ApplicationContext;

public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        playNotificationSound(context);
    }

    public void playNotificationSound(Context context) {
        try {
            ApplicationContext.getInstance().playNotificationSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
