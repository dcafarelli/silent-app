package it.unipi.donatocafarelli.silent_app.roomDB;



import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "events")
@TypeConverters(DBConverter.class)
public class EventEntity {

    @PrimaryKey(autoGenerate = false)

    @NonNull
    @ColumnInfo(name = "eventID")
    public String mEventID;

    @TypeConverters(DBConverter.class)
    public Date mStartTime;

    @TypeConverters(DBConverter.class)
    public Date mEndTime;

    public String getEventID(){return mEventID;}

    @TypeConverters(DBConverter.class)
    public Date getStartTime(){return mStartTime;}

    @TypeConverters(DBConverter.class)
    public Date getEndTime(){return mEndTime;}

    public EventEntity(@NonNull String eventID, Date startTime, Date endTime){
        this.mEventID = eventID;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }
}
