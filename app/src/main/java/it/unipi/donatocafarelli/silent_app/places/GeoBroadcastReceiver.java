package it.unipi.donatocafarelli.silent_app.places;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GeoBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeoBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        GeoTransitionsJobIntentService.enqueueWork(context, intent);
    }
}
