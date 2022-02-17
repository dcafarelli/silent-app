package it.unipi.donatocafarelli.silent_app.places;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class Geofences {

    private static final long GEO_DURATION = 24 * 60 * 60 * 1000; //24 ore
    private static final float GEOFENCE_RADIUS = 200; // in meters
    private final GoogleApiClient mGoogleAPIClient;
    private List<Geofence> mGeoFenceList;
    private PendingIntent mGeofencePendingIntent;
    private Context mContext;
    private GeofencingClient mGeofencingClient;
    private static final String TAG = Geofences.class.getSimpleName();

    public Geofences(Context context, GoogleApiClient client) {
        this.mGoogleAPIClient = client;
        mContext = context;
        mGeoFenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
    }

    public void geoFencesList(PlaceBuffer places) {
        //PlaceBuffer Data structure providing access to a list of Place objects.
        // A Place encapsulates information about a physical location, including its name, address, and any other information we might have about it.
        mGeoFenceList = new ArrayList<>();
        if (places == null || places.getCount() ==0 ) return;
        try {
            for (Place place : places) {

                String placeID = place.getId();
                String placeName = place.getName().toString();
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                mGeoFenceList.add(new Geofence.Builder()
                        .setRequestId(placeID)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }
        } catch (IllegalArgumentException ie){
            Log.v(Geofence.class.getSimpleName(), ie.toString());
            ie.printStackTrace();
        }

    }



    public void registerGeofences() {
        if (mGoogleAPIClient == null || !mGoogleAPIClient.isConnected()
                || mGeoFenceList == null || mGeoFenceList.size() == 0) {
            return;
        }

        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v(TAG, "Geofence Added");
                            Toast.makeText(mContext, "Geo added", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v(TAG, "Geofence Refused");
                        }
                    });
        } catch (SecurityException s) {
            Log.v(Geofences.class.getSimpleName(), s.getMessage());
        }
    }

    public void unRegisterGeoFences() {
        if (mGoogleAPIClient == null || !mGoogleAPIClient.isConnected()){
            return;
        }

        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        try {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v(TAG, "Geofence Removed");
                        }
                    })
                    .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v(TAG, "Geofence Not Removed");
                        }
                    });
        } catch (SecurityException s){
            Log.v(Geofences.class.getSimpleName(), s.getMessage());
        }
    }

    public void deleteGeofence(final String geofenceID){
        List<String> geofenceToDelete = new ArrayList<>();
        geofenceToDelete.add(geofenceID);
        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        mGeofencingClient.removeGeofences(geofenceToDelete).addOnSuccessListener((Activity) mContext, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "Geofence " + geofenceID + " removed");
            }
        })

        .addOnFailureListener((Activity) mContext, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "Geofence " + geofenceID + " not removed");
            }
        });

    }


    /* The following uses the GeofencingRequest class
    and its nested GeofencingRequestBuilder class to specify the geofences
    to monitor and to set how related geofence events are triggered */

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeoFenceList);
        return builder.build();
    }

    /*an IntentService is a good way to handle the intent. An IntentService can post a notification,
    do long-running background work, send intents to other services, or send a broadcast intent.
    The following shows how to define a PendingIntent that starts an IntentService: */

    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        Intent intent = new Intent(mContext, GeoBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
}
