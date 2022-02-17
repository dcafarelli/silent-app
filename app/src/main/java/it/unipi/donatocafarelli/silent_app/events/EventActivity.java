package it.unipi.donatocafarelli.silent_app.events;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.roomDB.EventEntity;
import it.unipi.donatocafarelli.silent_app.roomDB.SilentViewModel;

public class EventActivity extends AppCompatActivity {

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SwitchDateTimeDialogFragment dateTimeDialogFragment;
    private TextInputEditText editEventText;
    private TextInputEditText editStartText;
    private TextInputEditText editEndText;
    private String event;
    private SilentViewModel silentViewModel;
    private Date startTime;
    private Date endTime;
    private Calendar calendarStart;
    private Calendar calendarEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);

        editEventText = findViewById(R.id.editTextEvent);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        silentViewModel = ViewModelProviders.of(this).get(SilentViewModel.class);

        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());

        dateTimeDialogFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if (dateTimeDialogFragment == null) {
            dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        dateTimeDialogFragment.set24HoursMode(false);
        dateTimeDialogFragment.setHighlightAMPMSelection(false);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        FloatingActionButton fabEvent = findViewById(R.id.buttonFABEvent);
        fabEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compareDates()){
                    scheduleEvent();
                    unScheduleEvent();
                    silentViewModel.addEventToDB(event, startTime, endTime);
                    finish();
                }

            }
        });

        editStartText = findViewById(R.id.editTextStart);
        editStartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        editStartText.setText(myDateFormat.format(date));

                        calendarStart = Calendar.getInstance();
                        calendarStart.set(Calendar.YEAR, dateTimeDialogFragment.getYear());
                        calendarStart.set(Calendar.MONTH, dateTimeDialogFragment.getMonth());
                        calendarStart.set(Calendar.DAY_OF_MONTH, dateTimeDialogFragment.getDay());
                        calendarStart.set(Calendar.HOUR_OF_DAY, dateTimeDialogFragment.getHourOfDay());
                        calendarStart.set(Calendar.MINUTE, dateTimeDialogFragment.getMinute());
                        calendarStart.set(Calendar.SECOND, 0);

                        startTime = calendarStart.getTime();
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {

                    }
                });
            }
        });

        editEndText = findViewById(R.id.editTextEnd);
        editEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        editEndText.setText(myDateFormat.format(date));

                        calendarEnd = Calendar.getInstance();
                        calendarEnd.set(Calendar.YEAR, dateTimeDialogFragment.getYear());
                        calendarEnd.set(Calendar.MONTH, dateTimeDialogFragment.getMonth());
                        calendarEnd.set(Calendar.DAY_OF_MONTH, dateTimeDialogFragment.getDay());
                        calendarEnd.set(Calendar.HOUR_OF_DAY, dateTimeDialogFragment.getHourOfDay());
                        calendarEnd.set(Calendar.MINUTE, dateTimeDialogFragment.getMinute());
                        calendarEnd.set(Calendar.SECOND, 0);

                        endTime = calendarEnd.getTime();
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {

                    }
                });
            }
        });
    }

    private void getTextEvent() {
        event = editEventText.getText().toString();
    }

    private void scheduleEvent(){

        int id = (int) startTime.getTime();

        Intent intent = new Intent(this, SilenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= 23){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Evento " + event + " aggiunto", Toast.LENGTH_LONG).show();
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarStart.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Evento " + event + " aggiunto" , Toast.LENGTH_LONG).show();
        }

        getTextEvent();
    }

    private void unScheduleEvent(){

        int id = (int) endTime.getTime();

        Intent intent = new Intent(this, UnSilenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >= 23){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarEnd.getTimeInMillis(), pendingIntent);
        }

    }


    private boolean compareDates(){
        if (startTime.getTime() > endTime.getTime()){
            Log.v(EventEntity.class.getSimpleName(), "Tempo inizio maggiore di tempo di fine");
            AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
            builder.setTitle("Evento non settato correttamente.")
                    .setMessage("La data di inizio è maggiore della data di fine.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editStartText.setText("");
                    }
                })
                .setIcon(R.drawable.common_full_open_on_phone)
                .show();
            return false;
        } else {
            return true;
        }
    }

}
