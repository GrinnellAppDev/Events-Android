package edu.grinnell.appdev.events.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.grinnell.appdev.events.Event;
import edu.grinnell.appdev.events.R;
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
                R.layout.event_row, parent, false);
        return new ViewHolder(eventRow);
    }

    @Override
    public void onBindViewHolder(EventRecyclerAdapter.ViewHolder holder, int position) {
        final Event eventData = eventList.get(position);

        String month = new SimpleDateFormat("MMM").format(eventData.getStartTime());
        String day = new SimpleDateFormat("dd").format(eventData.getStartTime());
        String hour = new SimpleDateFormat("hh").format(eventData.getStartTime());
        String minutes = new SimpleDateFormat("mm").format(eventData.getStartTime());
        String ampm = new SimpleDateFormat("aa").format(eventData.getStartTime());
        String dayName = new SimpleDateFormat("EEEE").format(eventData.getStartTime());
        String startTime = hour + ":" + minutes + " " + ampm + " on " + dayName;

        holder.tvMonthText.setText(month);
        holder.tvDayText.setText(day);
        holder.tvEventName.setText(eventData.getTitle());
        holder.tvEventTime.setText(startTime);
        holder.tvEventLocation.setText(eventData.getLocation());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvent(String title, String content, Date startTime, Date endTime, String location) {
        eventRealm.beginTransaction();

        Event newEvent = eventRealm.createObject(Event.class, UUID.randomUUID().toString());
        newEvent.setTitle(title);
        newEvent.setContent(content);
        newEvent.setStartTime(startTime);
        newEvent.setEndTime(endTime);
        newEvent.setLocation(location);

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
        public TextView tvMonthText;
        public TextView tvDayText;
        public TextView tvEventName;
        public TextView tvEventTime;
        public TextView tvEventLocation;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMonthText = itemView.findViewById(R.id.tvMonthText);
            tvDayText = itemView.findViewById(R.id.tvDayText);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
        }

    }

}
