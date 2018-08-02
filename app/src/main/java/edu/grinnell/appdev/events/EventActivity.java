package edu.grinnell.appdev.events;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class EventActivity extends Activity{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Get the view from new_activity.xml
            setContentView(R.layout.activity_individual_event);
            Intent mIntent = getIntent();
            int eventNo = mIntent.getIntExtra("eventNo", 0);

            Event event = MainActivity.eventList.get(eventNo);

            setView(event);


        }

        private void setView(Event eventData) {
            String month = new SimpleDateFormat("MMM").format(eventData.getStartTime());
            String day = new SimpleDateFormat("dd").format(eventData.getStartTime());
            String hour = new SimpleDateFormat("hh").format(eventData.getStartTime());
            String minutes = new SimpleDateFormat("mm").format(eventData.getStartTime());
            String ampm = new SimpleDateFormat("aa").format(eventData.getStartTime());
            String dayName = new SimpleDateFormat("EEEE").format(eventData.getStartTime());
            String startTime = hour + ":" + minutes + " " + ampm;
            String title = eventData.getTitle();
            String content = eventData.getContent();
            String location = eventData.getLocation();

            TextView tvMonthText = findViewById(R.id.tvMonthText);
            TextView tvDayText = findViewById(R.id.tvDayText);
            TextView tvEventName = findViewById(R.id.tvEventName);
            TextView tvEventTime = findViewById(R.id.tvTime);
            TextView tvEventLocation = findViewById(R.id.tvLocation);
            TextView tvContent = findViewById(R.id.Details);

            tvMonthText.setText(month);
            tvDayText.setText(day);
            tvEventName.setText(title);
            tvEventTime.setText(dayName + ", " + day + " " + month + " at " + startTime);
            tvEventLocation.setText(location);
            tvContent.setText(content);
        }
    }
