package it.unipi.donatocafarelli.silent_app.roomDB;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

public class DBConverter {

    @TypeConverter
    public LatLng stringToLatLng(String ltLng) {
        Gson gson = new Gson();
        Type type = new TypeToken<LatLng>() {}.getType();
        LatLng latLng = gson.fromJson(ltLng, type);
        return latLng;
    }

    @TypeConverter
    public String latLngtoString(LatLng latLng){
        Gson gson = new Gson();
        return gson.toJson(latLng);
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
