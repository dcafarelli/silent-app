package it.unipi.donatocafarelli.silent_app.roomDB;


import android.app.Application;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class SilentDBInitializer {

    private PlaceDAO placeDAO;
    private EventDAO eventDAO;

    public SilentDBInitializer(Application application) {

        SilentRoomDatabase db = SilentRoomDatabase.getDatabase(application);
        placeDAO = db.getPlaceDAO();
        eventDAO = db.getEventDAO();
    }

    /*---------------- PLACES ---------------- */

    public void addPlace (Place place) {

        String placeId = place.getId();
        String placeName = place.getName().toString();
        String placeAddress = place.getAddress().toString();
        LatLng latLng = place.getLatLng();

        PlaceEntity placeEntity = new PlaceEntity(placeId, placeName, placeAddress, latLng);

        Log.i(SilentDBInitializer.class.getSimpleName(), "PlaceID :" + placeId);

        new populateDBAsync(placeDAO).execute(placeEntity);
    }

    public LiveData<List<PlaceEntity>> getPlace() {
        return placeDAO.getAllPlaces();
    }

    public LiveData<List<PlaceEntity>> getPlaceByID(String placeID) {
        return placeDAO.getPlaceByID(placeID);
    }

    public void deleteAllPlaces() {

        new deleteDBAsync(placeDAO).execute();
    }

    public void deletePlace(PlaceEntity placeEntity){

        new deletePlaceAsync(placeDAO).execute(placeEntity);
    }


    /*---------------- EVENTS ---------------- */

    /*public void addEvent (String event, int year, int month, int day, int hour, int minute){

        EventEntity eventEntity = new EventEntity(event, year, month, day, minute);

        new populateEventAsync(eventDAO).execute(eventEntity);

    }*/

    public void addEvent (String event, Date startTime, Date endTime){

        EventEntity eventEntity = new EventEntity(event, startTime, endTime);

        new populateEventAsync(eventDAO).execute(eventEntity);

    }

    public LiveData<List<EventEntity>> getEvent(){
        return eventDAO.getAllEvents();
    }

    public void deleteEvent(EventEntity eventEntity){

        new deleteEventAsync(eventDAO).execute(eventEntity);
    }

    public void deleteAllEvents(){

        new deleteAllEventsAsync(eventDAO).execute();
    }

    /*---------------- ASYNC TASKS ----------- */

    private static class populateDBAsync extends AsyncTask<PlaceEntity, Void, Void> {

        private PlaceDAO mAsyncTaskDao;

        populateDBAsync(PlaceDAO placeDAO) {
            mAsyncTaskDao = placeDAO;
        }

        @Override
        protected Void doInBackground(final PlaceEntity... params) {
            mAsyncTaskDao.insertPlace(params[0]);
            return null;
        }
    }

    private static class deleteDBAsync extends AsyncTask<Void, Void, Void> {

        private PlaceDAO mAsyncTaskDao;

        deleteDBAsync(PlaceDAO placeDAO) { mAsyncTaskDao = placeDAO;}

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deletePlaceAsync extends AsyncTask<PlaceEntity, Void, Void> {

        private PlaceDAO mAsyncTaskDao;

        deletePlaceAsync(PlaceDAO placeDAO) {mAsyncTaskDao = placeDAO;}

        @Override
        protected Void doInBackground(final PlaceEntity... params) {
            mAsyncTaskDao.deletePlace(params[0]);
            return null;
        }
    }

    private static class populateEventAsync extends AsyncTask<EventEntity, Void, Void> {

        private EventDAO mAsyncTaskDao;

        populateEventAsync(EventDAO eventDAO){ mAsyncTaskDao = eventDAO; }

        @Override
        protected Void doInBackground(final EventEntity... params) {
            mAsyncTaskDao.insertEvent(params[0]);
            return null;
        }
    }

    private static class deleteAllEventsAsync extends AsyncTask<Void, Void, Void> {

        private EventDAO mAsyncTAskDao;

        deleteAllEventsAsync(EventDAO eventDAO) {mAsyncTAskDao = eventDAO;}

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTAskDao.deleteAll();
            return null;
        }
    }

    private static class deleteEventAsync extends AsyncTask<EventEntity, Void, Void> {

        private EventDAO mAsyncTaskDao;

        deleteEventAsync(EventDAO eventDAO) {mAsyncTaskDao = eventDAO;}

        @Override
        protected Void doInBackground(EventEntity... params) {
            mAsyncTaskDao.deleteEvent(params[0]);
            return null;
        }
    }
}
