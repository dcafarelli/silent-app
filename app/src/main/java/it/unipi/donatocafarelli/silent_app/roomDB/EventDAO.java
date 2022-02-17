package it.unipi.donatocafarelli.silent_app.roomDB;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDAO {

    @Insert(onConflict = 1)
    void insertEvent(EventEntity eventEntity);

    @Query("SELECT * FROM events")
    LiveData<List<EventEntity>> getAllEvents();

    @Query("SELECT * FROM events WHERE eventID = :eventID")
    LiveData<List<EventEntity>> getEventByID(String eventID);

    @Query("DELETE FROM events")
    void deleteAll();

    @Delete
    void deleteEvent(EventEntity eventEntity);
}
