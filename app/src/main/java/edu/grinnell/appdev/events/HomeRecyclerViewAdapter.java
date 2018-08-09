package edu.grinnell.appdev.events;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.text.SimpleDateFormat;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Event> eventList;
    private Context context;
    private ArrayList<Event> favorites;

    public HomeRecyclerViewAdapter(ArrayList<Event> list, Context context){
        this.eventList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View eventRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.event_row, parent, false);
        return new ViewHolder(eventRow);
    }

    public void configureView(ViewHolder holder, int position){

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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        configureView(holder, position);

        //Expands a particular event page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventActivity.class);
                Bundle b = new Bundle();
                b.putInt("eventNo", position);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        setUpFavoritesButton(holder, position);

    }

    public void setUpFavoritesButton(final ViewHolder holder, final int position){
        //Animate the favorites button
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        final ToggleButton favorites = (ToggleButton) holder.itemView.findViewById(R.id.myToggleButton);

        if (eventList != null) {
            if (eventList.get(position).getIsFavorite() == 1) {
                favorites.setChecked(true);
            }
        }

        //Listen for any toggle in the favorites button
        favorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //animation
                compoundButton.startAnimation(scaleAnimation);
                if (isChecked){
                    //NEED TO ADD TO FAVORITES
                    eventList.get(position).setIsFavorite(1);
                    Toast.makeText(holder.itemView.getContext(), "checked", Toast.LENGTH_SHORT).show();
                }
                else {
                    eventList.get(position).setIsFavorite(0);
                    Toast.makeText(holder.itemView.getContext(), "unchecked", Toast.LENGTH_SHORT).show();
                }
                storeEvents(eventList);
            }
        });
    }
    //FIND A BETTER WAY TO ABSTRACT OUT THIS (ALSO USED IN MAIN ACTIVITY)
    public void storeEvents(ArrayList<Event> eventArrayList){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();

        String json = gson.toJson(eventArrayList); //Convert the array to json

        editor.putString(MainActivity.FULL_LIST, json); //Put the variable in memory
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
