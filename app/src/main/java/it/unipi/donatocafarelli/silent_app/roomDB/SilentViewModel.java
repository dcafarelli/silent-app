package it.unipi.donatocafarelli.silent_app.roomDB;

import android.app.Application;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.places.Place;

import java.util.Date;
import java.util.List;

import it.unipi.donatocafarelli.silent_app.places.Geofences;

public class SilentViewModel extends AndroidViewModel {

    private SilentDBInitializer silentDBInitializer;

    private LiveData<List<PlaceEntity>> mPlaceEntities;
    private LiveData<List<EventEntity>> mEventEntities;


    public SilentViewModel(@NonNull Application application) {
        super(application);
        silentDBInitializer = new SilentDBInitializer(application);
        mPlaceEntities = silentDBInitializer.getPlace();
        mEventEntities = silentDBInitializer.getEvent();


    }

    public LiveData<List<PlaceEntity>> getAllPlaces() {return mPlaceEntities;}

    public LiveData<List<PlaceEntity>> getPlacesByID(String placeID) {return silentDBInitializer.getPlaceByID(placeID);}

    public void addPlaceToDB(Place place){
        silentDBInitializer.addPlace(place);
    }

    public void deleteAllPlaces() { silentDBInitializer.deleteAllPlaces();}

    public void deletePlace(PlaceEntity placeEntity){
        silentDBInitializer.deletePlace(placeEntity);;
    }


    public LiveData<List<EventEntity>> getAllEvents() {return mEventEntities;}

    public void addEventToDB(String event, Date startTime, Date endTime){

        silentDBInitializer.addEvent(event, startTime, endTime);
        Log.v(SilentViewModel.class.getSimpleName(), "Added");
    }

    public void deleteAllEvents() {silentDBInitializer.deleteAllEvents();}

    public void deleteEvent(EventEntity eventEntity) {silentDBInitializer.deleteEvent(eventEntity);}
}