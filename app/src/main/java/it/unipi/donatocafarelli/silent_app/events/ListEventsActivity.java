package it.unipi.donatocafarelli.silent_app.events;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.audioUtils.Utils;
import it.unipi.donatocafarelli.silent_app.roomDB.EventEntity;
import it.unipi.donatocafarelli.silent_app.roomDB.SilentViewModel;

public class ListEventsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private SilentViewModel mSilentViewModel;
    private BottomAppBar bottomAppBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listevents);

        mRecyclerView = (RecyclerView)findViewById(R.id.events_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new EventAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        bottomAppBar = findViewById(R.id.bar);
        setSupportActionBar(bottomAppBar);

        mSilentViewModel = ViewModelProviders.of(this).get(SilentViewModel.class);
        getEvents();

        final Utils utils = new Utils();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        EventEntity eventEntity = mAdapter.getEventAtPosition(position);
                        mSilentViewModel.deleteEvent(eventEntity);
                        deleteAlarmStart(eventEntity.getStartTime());
                        deleteAlarmEnd(eventEntity.getEndTime());
                        utils.switchAudio(ListEventsActivity.this);
                        Toast.makeText(ListEventsActivity.this, eventEntity.getEventID() + " eliminato.", Toast.LENGTH_LONG).show();
                    }
                });

        FloatingActionButton fab = findViewById(R.id.buttonEvent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListEventsActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuEvents){
        getMenuInflater().inflate(R.menu.menu_events, menuEvents);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == R.id.clear_events){
            Toast.makeText(this, R.string.clear_all_data, Toast.LENGTH_SHORT).show();
            mSilentViewModel.deleteAllEvents();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void getEvents() {
        mSilentViewModel.getAllEvents().observe(this, new Observer<List<EventEntity>>() {
            @Override
            public void onChanged(@Nullable List<EventEntity> eventEntities) {
                mAdapter.setEvent(eventEntities);

            }
        });
    }

    public void deleteAlarmStart(Date startDate){
        int id = (int) startDate.getTime();

        Intent intent = new Intent(ListEventsActivity.this, SilenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void deleteAlarmEnd(Date endDate){
        int id = (int) endDate.getTime();

        Intent intent = new Intent(getBaseContext(), UnSilenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
