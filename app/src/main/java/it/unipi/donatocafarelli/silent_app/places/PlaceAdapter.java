package it.unipi.donatocafarelli.silent_app.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.PlaceBuffer;

import java.util.List;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.roomDB.PlaceEntity;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context mContext;
    private PlaceBuffer mPlace;
    private List<PlaceEntity> placeEntityArrayList;


    public PlaceAdapter(Context context) {

        //this.mPlace = mPlace;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_place_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //String placeName = mPlace.get(position).getName().toString();
        //String placeAddress = mPlace.get(position).getAddress().toString();
        //String placeAttributes = mPlace.get(position).getAttributions().toString();
        String placeName = placeEntityArrayList.get(position).getPlaceName();
        String placeAddress = placeEntityArrayList.get(position).getAddressName();

        holder.nameView.setText(placeName);
        holder.addressView.setText(placeAddress);

    }

    public void setPlace(List<PlaceEntity> place) {
        this.placeEntityArrayList = place;
        notifyDataSetChanged();
    }

    public PlaceEntity getPlaceAtPosition(int position){
        return placeEntityArrayList.get(position);
    }

    @Override
    public int getItemCount() {

        if(placeEntityArrayList == null) return 0;
        return placeEntityArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView addressView;

        public ViewHolder(View view) {

            super(view);
            nameView = (TextView) view.findViewById(R.id.name_text_view);
            addressView = (TextView) view.findViewById(R.id.address_text_view);
        }

    }
}
