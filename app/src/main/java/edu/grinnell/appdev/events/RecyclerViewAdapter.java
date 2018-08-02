package edu.grinnell.appdev.events;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.text.SimpleDateFormat;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Event> eventList;

    public RecyclerViewAdapter(ArrayList<Event> list){
        this.eventList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View eventRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.event_row, parent, false);
        return new ViewHolder(eventRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventActivity.class);
                Bundle b = new Bundle();
                b.putInt("eventNo", position); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                context.startActivity(intent);
            }
        });
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
