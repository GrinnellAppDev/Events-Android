package edu.grinnell.appdev.events;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static edu.grinnell.appdev.events.MainActivity.favoritesList;

/**
 * Adapter for the recycler view of favorite events
 */
public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder>{

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View eventRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.event_row_favorites, parent, false);
        return new ViewHolder(eventRow);
    }

    /**
     *
     * @param holder Holder object that holds all the elements of a view
     * @param position Index in a recycler view
     */
    private void configureView(ViewHolder holder, int position){

        final Event eventData = favoritesList.get(position);
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
    public void onBindViewHolder(FavoritesRecyclerViewAdapter.ViewHolder holder, int position) {
        configureView(holder, position);
    }



    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
