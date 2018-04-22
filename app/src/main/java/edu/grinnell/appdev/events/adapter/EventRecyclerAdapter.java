package edu.grinnell.appdev.events.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.grinnell.appdev.events.Event;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {

    private List<Event> eventList;

    private Context context;
    private Realm eventRealm;

    public EventRecyclerAdapter(Context context, Realm eventRealm) {
        this.context = context;
        this.eventRealm = eventRealm;

        eventList = new ArrayList<Event>();

        RealmResults<Event> eventResults = eventRealm.where(Event.class).findAll().sort("title", Sort.ASCENDING);

        for(Event event : eventResults) {
            eventList.add(event);
        }
    }

    @Override
    public EventRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View eventRow = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.event_row, parent, false);
        return new ViewHolder(eventRow);
    }

    @Override
    public void onBindViewHolder(EventRecyclerAdapter.ViewHolder holder, int position) {
        final Event eventData = eventList.get(position);

        //holder.tvImageText.setText(eventData.getStartTime());
        //holder.tvImageText.setText(eventData.getEndTime());
        holder.tvEventName.setText(eventData.getTitle());
        //holder.tvEventTime.setText(eventData.getTime());
        //holder.tvEventLocation.setText(eventData.getLocation());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvent(String date, String title, String time, String location, String numComments, String numAttendees) {
        eventRealm.beginTransaction();

        Event newEvent = eventRealm.createObject(Event.class, UUID.randomUUID().toString());
        //newEvent.setDate(date);
        newEvent.setTitle(title);
        //newEvent.setTime(time);
        //newEvent.setLocation(location);
        //newEvent.setNumComments(numComments);
        //newEvent.setNumAttendees(numAttendees);

        eventRealm.commitTransaction();

        eventList.add(0, newEvent);
        notifyItemInserted(0);
    }

    public void updateEvent(String eventIDThatWasEdited, int positionToEdit) {
        Event event = eventRealm.where(Event.class).
                equalTo("eventID", eventIDThatWasEdited).
                findFirst();

        eventList.set(positionToEdit, event);
        notifyItemChanged(positionToEdit);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivEventPic;
        public TextView tvImageText;
        public TextView tvEventName;
        public TextView tvEventTime;
        public TextView tvEventLocation;
        public ImageView ivComments;
        public TextView tvNumComments;
        public TextView tvNumAttendees;

        public ViewHolder(View itemView) {
            super(itemView);

            /*ivEventPic = itemView.findViewById(R.id.ivEventPic);
            tvImageText = itemView.findViewById(R.id.tvImageText);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            ivComments = itemView.findViewById(R.id.ivComments);
            tvNumComments = itemView.findViewById(R.id.tvNumComments);
            tvNumAttendees = itemView.findViewById(R.id.tvNumAttendees);*/
        }

    }

}
