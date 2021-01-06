package com.easyfoodvone.utility;

import android.app.Application;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import com.easyfoodvone.R;
import com.easyfoodvone.utility.printerutil.AidlUtil;
import com.newrelic.agent.android.NewRelic;

import java.util.Calendar;
import java.util.Date;

public class ApplicationContext extends Application {
    static ApplicationContext applicationContext;
    private MediaPlayer player;
    private Date mediaPlayerStartTime;
    Handler handler;

    public static synchronized ApplicationContext getInstance() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NewRelic.withApplicationToken(
                "eu01xx8e8c3e4790f9795fc7133941ac935ff9a204"
        ).start(this.getApplicationContext());
        applicationContext = this;
        handler = new Handler();
        AidlUtil.getInstance().connectPrinterService(this);
    }

    public void playNotificationSound() {
        try {
            if (player == null) {
                handler.removeCallbacks(stopPlayer);
                Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.mobile_order_notification_sound);
                player = MediaPlayer.create(this, alarmSound);
                player.setLooping(true);
                player.start();
                mediaPlayerStartTime = Calendar.getInstance().getTime();
                handler.postDelayed(stopPlayer, (1000 * 180));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkPlayerPlayTime() {
        if (getDifferenceMin(mediaPlayerStartTime, Calendar.getInstance().getTime()) >= 1) {
            stopNotificationSound();
        }
    }

    public void stopNotificationSound() {
        try {

            try {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (player != null && player.isLooping() && player.isPlaying()) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getDifferenceMin(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
//        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
//        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return elapsedMinutes;

    }

    Runnable stopPlayer = new Runnable() {
        @Override
        public void run() {
//            checkPlayerPlayTime();
            stopNotificationSound();
        }
    };
}