package it.unipi.donatocafarelli.silent_app.audioUtils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private AudioManager audioManager;

    public void setAudioMode(Context context, int mode) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.v(TAG, "Sono nell'audio mode");

        if(Build.VERSION.SDK_INT < 24 || notificationManager.isNotificationPolicyAccessGranted()) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        } else {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
        }
    }


    public void switchAudio(Context context) {

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }
}
