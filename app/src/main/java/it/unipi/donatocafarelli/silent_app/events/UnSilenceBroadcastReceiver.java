package it.unipi.donatocafarelli.silent_app.events;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.audioUtils.Utils;
import it.unipi.donatocafarelli.silent_app.places.GeoBroadcastReceiver;
import it.unipi.donatocafarelli.silent_app.places.PlaceActivity;

public class UnSilenceBroadcastReceiver extends BroadcastReceiver {
    private GeoBroadcastReceiver geoBroadcastReceiver = new GeoBroadcastReceiver();
    private Utils utilsAudio = new Utils();
    private static final String CHANNEL_ID = "channel_01";

    @Override
    public void onReceive(Context context, Intent intent) {
        //geoBroadcastReceiver.setAudioMode(context, AudioManager.RINGER_MODE_NORMAL);
        utilsAudio.setAudioMode(context, AudioManager.RINGER_MODE_NORMAL);
        sendSilence(context);
    }

    public void sendSilence(Context context){

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = context.getString(R.string.app_name);

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, mNotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(context.getApplicationContext(), PlaceActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(PlaceActivity.class);
        taskStackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_logo1)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo1))
                .setContentTitle("Modalita' Silenzioso Disattivata");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        builder.setContentText("Premi per avviare l'applicazione");
        builder.setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        mNotificationManager.notify(0, builder.build());
    }
}
