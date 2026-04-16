package com.anuragalert;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class AlertNotificationListener extends NotificationListenerService {

    private static final String TARGET_NAME = "Anurag Dwivedi";
    private static final String WHATSAPP_PACKAGE = "com.whatsapp";
    private static final String TELEGRAM_PACKAGE = "org.telegram.messenger";

    public static MediaPlayer mediaPlayer;
    public static boolean isServiceEnabled = false;

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopAlarm();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(stopReceiver, new IntentFilter("com.anuragalert.STOP_ALARM"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopReceiver);
        stopAlarm();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!isServiceEnabled) return;
        if (sbn == null) return;

        String pkg = sbn.getPackageName();
        if (!WHATSAPP_PACKAGE.equals(pkg) && !TELEGRAM_PACKAGE.equals(pkg)) return;

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) return;

        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence bigText = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);

        boolean matched = false;
        if (title != null && title.toString().contains(TARGET_NAME)) matched = true;
        if (!matched && text != null && text.toString().contains(TARGET_NAME)) matched = true;
        if (!matched && bigText != null && bigText.toString().contains(TARGET_NAME)) matched = true;

        if (matched) playAlarm();
    }

    private void playAlarm() {
        try {
            stopAlarm();
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                        0
                );
            }
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), alarmUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAlarm() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
    }
}
