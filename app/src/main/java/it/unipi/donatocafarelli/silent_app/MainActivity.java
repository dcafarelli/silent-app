package it.unipi.donatocafarelli.silent_app;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import it.unipi.donatocafarelli.silent_app.events.ListEventsActivity;
import it.unipi.donatocafarelli.silent_app.places.PlaceActivity;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class MainActivity extends AppCompatActivity {

    private ImageView imagePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_ACTION_BAR | Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

         setContentView(R.layout.activity_main);

        ImageView imageEvent = findViewById(R.id.imageEvent);
        imageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEvents();
            }
        });

        imagePlace = findViewById(R.id.imagePlace);
        imagePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlace();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT > 24 && !notificationManager.isNotificationPolicyAccessGranted()){
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        if(!Nammu.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Nammu.askForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionRefused() {
                    imagePlace.setEnabled(false);
                }
            });
        }
    }

    private void goToPlace(){
        Intent intent = new Intent(this, PlaceActivity.class);
        startActivity(intent);
    }

    private void goToEvents(){
        Intent intent = new Intent(this, ListEventsActivity.class);
        startActivity(intent);
    }
}
