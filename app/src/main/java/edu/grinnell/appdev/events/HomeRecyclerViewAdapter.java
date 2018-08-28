package edu.grinnell.appdev.events;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static edu.grinnell.appdev.events.Constants.FAVORITES_LIST;
import static edu.grinnell.appdev.events.FragmentFavorites.addEvent;
import static edu.grinnell.appdev.events.FragmentFavorites.removeWithID;
import static edu.grinnell.appdev.events.MainActivity.favoritesList;
import static edu.grinnell.appdev.events.MainActivity.writeToFile;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter implements Filterable{

    private Context context;
    private ArrayList<Event> eventArrayList;
    private ArrayList<Event> filteredList;

    /**
     *
     * @param context A context field required to store in shared preference
     */
    HomeRecyclerViewAdapter(Context context, ArrayList<Event> eventArrayList){
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.filteredList = eventArrayList;

    }

    @Override
    public int getItemViewType(int position){
        super.getItemViewType(position);
        if (filteredList.get(position).getIsDivider() == 1){
            return Constants.DIVIDER_ROW;
        }
        else {
            return Constants.NORMAL_ROW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case Constants.DIVIDER_ROW:
                return new DividerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_divider, parent, false));

            case Constants.NORMAL_ROW:
                return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false));
        }
        return null;
    }


    /**
     *
     * @param holder Holder object that holds all the elements of a view
     * @param position Index in a recycler view
     */
    private void configureView(RecyclerView.ViewHolder holder, int position){
        final Event eventData = filteredList.get(position);
        eventData.setStartTime(new Date(eventData.getStartTimeNew()));

        String month = new SimpleDateFormat("MMM", Locale.US)
                .format(eventData.getStartTime());
        String day = new SimpleDateFormat("dd", Locale.US)
                .format(eventData.getStartTime());
        String hour = new SimpleDateFormat("hh", Locale.US)
                .format(eventData.getStartTime());
        String minutes = new SimpleDateFormat("mm", Locale.US)
                .format(eventData.getStartTime());
        String ampm = new SimpleDateFormat("aa", Locale.US)
                .format(eventData.getStartTime());
        String dayName = new SimpleDateFormat("EEEE", Locale.US)
                .format(eventData.getStartTime());
        String startTime = hour + ":" + minutes + " " + ampm + " on " + dayName;

        ((EventViewHolder)holder).tvMonthText.setText(month);
        ((EventViewHolder)holder).tvDayText.setText(day);
        ((EventViewHolder)holder).tvEventName.setText(eventData.getTitle());
        ((EventViewHolder)holder).tvEventTime.setText(startTime);
        ((EventViewHolder)holder).tvEventLocation.setText(eventData.getLocation());
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // Set up view based on whether it is a divider row or an event row
        if (holder instanceof EventViewHolder){
            configureView((EventViewHolder) holder, position);
            //Expands a particular event page
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, IndividualEventActivity.class);
                    intent.putExtra("Event", filteredList.get(position));
                    context.startActivity(intent);

                    //Add transition between activity switch
                    ((MainActivity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            setUpFavoritesButton((EventViewHolder) holder, position);
        }
        else if (holder instanceof DividerViewHolder){
            final Event eventData = filteredList.get(position);
            eventData.setStartTime(new Date(eventData.getStartTimeNew()));

            String month = new SimpleDateFormat("MMM", Locale.US)
                    .format(eventData.getStartTime());
            String day = new SimpleDateFormat("dd", Locale.US)
                    .format(eventData.getStartTime());
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.US)
                    .format(eventData.getStartTime());
            String year = new SimpleDateFormat("yyyy", Locale.US)
                    .format(eventData.getStartTime());
            ((DividerViewHolder) holder).tvDividerText.setText(dayOfWeek + ", "
                    + day + " " + month + " " + year);
            ((DividerViewHolder) holder).tvDividerText.setTextColor(ContextCompat
                    .getColor(context, R.color.colorScarlet));
        }
    }

    /**
     *
     * @return Scale Animation : Animation of the favorites button when clicked
     */
    private ScaleAnimation setAnimation(){
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        return scaleAnimation;
    }

    /**
     *
     * @param id String that represents the unique id of a particular event
     * @param favorites List that represents favorites marked by the user
     * @return boolean to indicate whether a particular event is in the favorites list
     */
    private boolean containsID (String id, ArrayList<Event> favorites){
        for (Event event: favorites){
            if (id.equals(event.getId())){
                return true;
            }
        }
        return false;
    }

    public static int getIdInt(String id){
        int numId = Integer.parseInt(id.substring(28));
        return numId;
    }

    /**
     *
     * @param holder Holder object that holds all the elements of a view
     * @param position Index in a recycler view
     */
    private void setUpFavoritesButton(final EventViewHolder holder, final int position){
        //Animate the favorites button
        final ScaleAnimation scaleAnimation = setAnimation();

        //Make sure that the buttons work properly when view is getting recycled
        holder.favorites.setOnCheckedChangeListener(null);
        holder.favorites.setChecked(false);

        //Restore state of toggle button while scrolling and refreshing the data
        if (filteredList != null) {
            Event event = filteredList.get(position);
            if (containsID(event.getId(), favoritesList)){
                holder.favorites.setChecked(true);
            }
        }

        //Listen for any toggle in the favorites button
        holder.favorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //animation
                compoundButton.startAnimation(scaleAnimation);
                Event event = filteredList.get(position);
                NotificationHandler.createNotificationChannel((Activity) context);
                int lstPos = favoritesList.size() - 1;
                int eventId = getIdInt(event.getId());
                Log.d("ID in HRV", eventId+"");
                //Update the favorites list
                if (isChecked){
                    addEvent(event, favoritesList, lstPos);
                    //Set up notification when the event starts
                    Notification notification = NotificationHandler.buildNotfication((Activity) context, event.getTitle(), "Event has started");
                    NotificationHandler.createNotification((Activity) context,eventId, event.getStartTimeNew(), notification);
                }
                else {
                    //Removed Scheduled notification if the user removes from favorites
                    removeWithID(event.getId(), favoritesList);
                    NotificationHandler.deleteNotification((Activity) context, eventId);
                }
                // Create/Update shared preference for favorites list
                writeToFile(context, FAVORITES_LIST, favoritesList);
            }
        });
    }


    /**
     *
     * @return int : The size of list of events
     */
    @Override
    public int getItemCount() {
        return filteredList == null ? 0 : filteredList.size();
    }

    /**
     * Responsible for filtering the results and putting it in a new listbased on query typed by the user
     * @return Filter results, which will be used by the adapter
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();

                //Show everythings if query is an empty string
                if (query.isEmpty()){
                    filteredList = eventArrayList;
                }
                else {
                    ArrayList<Event> tempFilteredList = new ArrayList<>();
                    for (Event event: eventArrayList){
                        // Look for substrings in title and description
                        if (event.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                || event.getContent().toLowerCase()
                                .contains(constraint.toString().toLowerCase())){
                            tempFilteredList.add(event);
                        }
                    }
                    filteredList = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Event>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * ViewHolder class that holds all the views for a particular event row in the recycler view
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
            private TextView tvMonthText;
            private TextView tvDayText;
            private TextView tvEventName;
            private TextView tvEventTime;
            private TextView tvEventLocation;
            private ToggleButton favorites;

            private EventViewHolder(View itemView) {
                super(itemView);

                tvMonthText = itemView.findViewById(R.id.tvMonthText);
                tvDayText = itemView.findViewById(R.id.tvDayText);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvEventTime = itemView.findViewById(R.id.tvEventTime);
                tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
                favorites = itemView.findViewById(R.id.myToggleButton);
            }

    }

    /**
     * View holder that specifies divider texts between each day
     */
    public static class DividerViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDividerText;

        private DividerViewHolder(View itemView) {
            super(itemView);
            tvDividerText = itemView.findViewById(R.id.tvDividerText);
        }

    }

    }
