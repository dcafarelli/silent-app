package it.unipi.donatocafarelli.silent_app.roomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;


@Entity(tableName = "places")

@TypeConverters(DBConverter.class)
public class PlaceEntity {

    @ColumnInfo(name = "placeID")
    @PrimaryKey(autoGenerate = false)

    @NonNull
    public String placeID;
    @ColumnInfo (name = "name")

    @NonNull
    public String placeName;
    @ColumnInfo(name = "address")

    @NonNull
    public String addressName;
    @ColumnInfo(name = "latlong")

    @TypeConverters(DBConverter.class)
    public LatLng latLng;

    public String getPlaceID() {return placeID;}
    public void setPlaceID(String placeID) {this.placeID = placeID; }

    public String getPlaceName() {return placeName;}
    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public String getAddressName() {return addressName;}
    public void setAddressName(String addressName) {this.addressName = addressName;}

    @TypeConverters(DBConverter.class)
    public LatLng getLatLng() {return latLng;}
    public void setLatLng(LatLng latLng) {this.latLng = latLng;}

    public PlaceEntity(String placeID, String placeName, String addressName, LatLng latLng) {
        this.placeID = placeID;
        this.placeName = placeName;
        this.addressName = addressName;
        this.latLng = latLng;
    }
}
