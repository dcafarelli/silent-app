package it.unipi.donatocafarelli.silent_app.places;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import it.unipi.donatocafarelli.silent_app.R;

public class GeoTransitionsJobIntentService extends JobIntentService {

    //private static final int JOB_ID = 5445;
    private static final int JOB_ID = 61166046;

    private static final String TAG = "GeoTransitions";

    private static final String CHANNEL_ID = "channel_01";

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, GeoTransitionsJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()) {
            Log.e(TAG, ("Error : " + geofencingEvent.getErrorCode()));
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        Log.v(TAG, "La transizione è' " + geoFenceTransition);

        if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            setRingMode(AudioManager.RINGER_MODE_SILENT);

        } else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            setRingMode(AudioManager.RINGER_MODE_NORMAL);

        } else {
            Log.e(TAG, "Geofence Transition sconosciuta");
        }

        sendNotif(geoFenceTransition);

    }

    public void sendNotif(int transition){

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name =getString(R.string.app_name);

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, mNotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), PlaceActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(PlaceActivity.class);
        taskStackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        if(transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            builder.setSmallIcon(R.drawable.ic_logo1)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo1))
                    .setContentTitle("Modalita' Silenzioso Attivata");
        } else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            builder.setSmallIcon(R.drawable.ic_logo1)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo1))
                    .setContentTitle("Modalita' Normale Attivata");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        builder.setContentText("Premi per avviare l'applicazione");
        builder.setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        //NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }


    public void setRingMode(int mode) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.v(TAG, "Sono nell'audio mode");


        if(Build.VERSION.SDK_INT < 24 || notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        } else {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }


}

