package edu.grinnell.appdev.events;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static edu.grinnell.appdev.events.MainActivity.FAVORITES_LIST;
import static edu.grinnell.appdev.events.MainActivity.favoritesList;
import static edu.grinnell.appdev.events.MainActivity.storeEvents;


public class HomeRecyclerViewAdapter extends Adapter<HomeRecyclerViewAdapter.ViewHolder> implements Filterable{

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
        favoritesList = new ArrayList<>();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String favStr = sharedPrefs.getString(FAVORITES_LIST, null);
        if (favStr != null){
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            favoritesList = gson.fromJson(favStr, type); //Restore previous data
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View eventRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.event_row, parent, false);

        return new ViewHolder(eventRow);
    }

    /**
     *
     * @param holder Holder object that holds all the elements of a view
     * @param position Index in a recycler view
     */
    private void configureView(ViewHolder holder, int position){

        //final Event eventData = eventList.get(position);
        final Event eventData = filteredList.get(position);

        eventData.setStartTime(new Date(eventData.getStartTimeNew()));

        String month = new SimpleDateFormat("MMM", Locale.US).format(eventData.getStartTime());
        String day = new SimpleDateFormat("dd", Locale.US).format(eventData.getStartTime());
        String hour = new SimpleDateFormat("hh", Locale.US).format(eventData.getStartTime());
        String minutes = new SimpleDateFormat("mm", Locale.US).format(eventData.getStartTime());
        String ampm = new SimpleDateFormat("aa", Locale.US).format(eventData.getStartTime());
        String dayName = new SimpleDateFormat("EEEE", Locale.US).format(eventData.getStartTime());
        String startTime = hour + ":" + minutes + " " + ampm + " on " + dayName;

        holder.tvMonthText.setText(month);
        holder.tvDayText.setText(day);
        holder.tvEventName.setText(eventData.getTitle());
        holder.tvEventTime.setText(startTime);
        holder.tvEventLocation.setText(eventData.getLocation());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        configureView(holder, position);

        //Expands a particular event page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("Event", eventArrayList.get(position));
                context.startActivity(intent);
            }
        });
        setUpFavoritesButton(holder, position);

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

    /**
     *
     * @param holder Holder object that holds all the elements of a view
     * @param position Index in a recycler view
     */
    private void setUpFavoritesButton(final ViewHolder holder, final int position){
        //Animate the favorites button
        final ScaleAnimation scaleAnimation = setAnimation();

        //Make sure that the buttons work properly when view is getting recycled
        holder.favorites.setOnCheckedChangeListener(null);
        holder.favorites.setChecked(false);

        //Restore state of toggle button while scrolling and refreshing the data
        if (eventArrayList != null) {
            Event event = eventArrayList.get(position);
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
                //Event event = eventList.get(position);
                Event event = eventArrayList.get(position);
                if (isChecked){
                    FragmentFavorites.addEvent(event, favoritesList);
                }
                else {
                    FragmentFavorites.removeWithID(event.getId(), favoritesList);
                }
                // Create/Update shared preference for favorites list
                storeEvents(favoritesList, context, FAVORITES_LIST);
            }
        });
    }


    /**
     *
     * @return int : The size of list of events
     */
    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();

                if (query.isEmpty()){
                    filteredList = eventArrayList;
                }
                else {
                    ArrayList<Event> tempFilteredList = new ArrayList<>();
                    for (Event event: eventArrayList){
                        if (event.getTitle().contains(constraint)|| event.getContent().contains(constraint)){
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
                Log.d("published result", filteredList.toString());
                notifyDataSetChanged();
            }
        };
    }


    /**
     * ViewHolder class that holds all the views for a particular row in the recycler view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvMonthText;
            private TextView tvDayText;
            private TextView tvEventName;
            private TextView tvEventTime;
            private TextView tvEventLocation;
            private ToggleButton favorites;

            private ViewHolder(View itemView) {
                super(itemView);

                tvMonthText = itemView.findViewById(R.id.tvMonthText);
                tvDayText = itemView.findViewById(R.id.tvDayText);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvEventTime = itemView.findViewById(R.id.tvEventTime);
                tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
                favorites = itemView.findViewById(R.id.myToggleButton);
            }

        }
    }
