package it.unipi.donatocafarelli.silent_app.roomDB;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaceDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlace(PlaceEntity placeEntity);

    @Query("SELECT * FROM places")
    LiveData<List<PlaceEntity>> getAllPlaces();

    @Query("SELECT * FROM places WHERE placeID = :placeID")
    LiveData<List<PlaceEntity>> getPlaceByID(String placeID);

    @Query("DELETE FROM places")
    void deleteAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePlaces(PlaceEntity placeEntity);

    @Delete
    void deletePlace(PlaceEntity placeEntity);
}

