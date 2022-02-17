package it.unipi.donatocafarelli.silent_app.places;

import android.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.audioUtils.Utils;
import it.unipi.donatocafarelli.silent_app.roomDB.PlaceEntity;
import it.unipi.donatocafarelli.silent_app.roomDB.SilentViewModel;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = PlaceActivity.class.getSimpleName();
    private static final int PLACE_PICK_REQ = 1;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private SilentViewModel mSilentViewModel;
    private Geofences geofences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mRecyclerView = (RecyclerView)findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new PlaceAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        BottomAppBar bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        mSilentViewModel = ViewModelProviders.of(this).get(SilentViewModel.class);

        final Utils utils = new Utils();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        PlaceEntity placeEntity = mAdapter.getPlaceAtPosition(position);
                        Toast.makeText(PlaceActivity.this, placeEntity.getPlaceName() + " eliminato.", Toast.LENGTH_LONG).show();
                        mSilentViewModel.deletePlace(placeEntity);
                        String geofenceID = placeEntity.getPlaceID();
                        geofences.deleteGeofence(geofenceID);
                        utils.switchAudio(PlaceActivity.this);


                    }
                });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = findViewById(R.id.buttonFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlacePicker();
            }
        });



        //Accessing Google APIs with GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

         geofences = new Geofences(this, mGoogleApiClient);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == R.id.clear_data){
            Toast.makeText(this, R.string.clear_all_data, Toast.LENGTH_SHORT).show();
            mSilentViewModel.deleteAllPlaces();
            geofences.unRegisterGeoFences();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "Connected");
        getPlaces();
    }

    private void getGeofenceForPlace(String placeID){
        PendingResult<PlaceBuffer> placeBuffer = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID);
        placeBuffer.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                for(Place place : places){
                    Log.v(TAG, "PlaceID in geof " + place.getName().toString());
                }

                geofences.geoFencesList(places);
                geofences.registerGeofences();
            }
        });
    }

    private void getPlaces() {
        mSilentViewModel.getAllPlaces().observe(this, new Observer<List<PlaceEntity>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntity> placeEntities) {
                mAdapter.setPlace(placeEntities);
                for(PlaceEntity placeEntity : placeEntities){
                    Log.i(TAG,"PlaceID " + placeEntity.getPlaceName());
                    getGeofenceForPlace(placeEntity.getPlaceID());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Connection Failed...");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void goToPlacePicker() {
        if(!Nammu.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_SHORT).show();
            Nammu.askForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, new LocationPermissionCallback());
        }
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICK_REQ);
            } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext(),
                        R.style.Theme_AppCompat_Dialog_Alert);
                e.printStackTrace();
            }
 
    }

    protected void onActivityResult(int requestcode, int resultCode, Intent data) {
        super.onActivityResult(requestcode, resultCode, data);
        if (requestcode == PLACE_PICK_REQ && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);

            Log.i(TAG, "PlaceName " + place.getName());

            mSilentViewModel.addPlaceToDB(place);
        }
    }


    private class LocationPermissionCallback implements PermissionCallback {
        @Override
        public void permissionGranted() {
            getPlaces();
        }

        @Override
        public void permissionRefused() {
            Toast.makeText(PlaceActivity.this, "Permission Refused", Toast.LENGTH_SHORT).show();
        }
    }
}
