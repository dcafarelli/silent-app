package it.unipi.donatocafarelli.silent_app.roomDB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {PlaceEntity.class, EventEntity.class}, version = 1, exportSchema = false)
@TypeConverters({DBConverter.class})
public abstract class SilentRoomDatabase extends RoomDatabase {

    public abstract PlaceDAO getPlaceDAO();
    public abstract EventDAO getEventDAO();

    private static volatile SilentRoomDatabase INSTANCE;

    static SilentRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (SilentRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SilentRoomDatabase.class, "silent_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
