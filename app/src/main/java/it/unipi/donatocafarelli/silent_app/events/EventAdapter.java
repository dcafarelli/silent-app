package it.unipi.donatocafarelli.silent_app.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.unipi.donatocafarelli.silent_app.R;
import it.unipi.donatocafarelli.silent_app.roomDB.EventEntity;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context mContext;
    private List<EventEntity> mEventEntity;

    public EventAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_event_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        String eventName = mEventEntity.get(i).getEventID();
        Date startTime = mEventEntity.get(i).getStartTime();
        Date endTime = mEventEntity.get(i).getEndTime();
        SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());

        viewHolder.eventView.setText(eventName);
        viewHolder.dateStartView.setText(myDateFormat.format(startTime));
        viewHolder.dateEndView.setText(myDateFormat.format(endTime));
    }

    public void setEvent(List<EventEntity> event){
        this.mEventEntity = event;
        notifyDataSetChanged();
    }

    public EventEntity getEventAtPosition(int position){
        return mEventEntity.get(position);
    }

    @Override
    public int getItemCount() {

        if(mEventEntity == null) return 0;
        return mEventEntity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventView;
        TextView dateStartView;
        TextView dateEndView;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            eventView = itemView.findViewById(R.id.event_text_view);
            dateStartView = itemView.findViewById(R.id.dateStart_text_view);
            dateEndView = itemView.findViewById(R.id.dateEnd_text_view);
        }
    }
}



