package edu.grinnell.appdev.events;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity{

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
            //Begin time
            final String month = new SimpleDateFormat("MMM").format(eventData.getStartTime());
            final String day = new SimpleDateFormat("dd").format(eventData.getStartTime());
            final String hour = new SimpleDateFormat("hh").format(eventData.getStartTime());
            final String minutes = new SimpleDateFormat("mm").format(eventData.getStartTime());
            String ampm = new SimpleDateFormat("aa").format(eventData.getStartTime());
            String dayName = new SimpleDateFormat("EEEE").format(eventData.getStartTime());
            final String year = new SimpleDateFormat("yyyy").format(eventData.getStartTime());
            String startTime = hour + ":" + minutes + " " + ampm;

            //End time
            final String endMonth = new SimpleDateFormat("MMM").format(eventData.getEndTime());
            final String endDay = new SimpleDateFormat("dd").format(eventData.getEndTime());
            final String endHour = new SimpleDateFormat("hh").format(eventData.getEndTime());
            final String endMinutes = new SimpleDateFormat("mm").format(eventData.getEndTime());
            String endAmpm = new SimpleDateFormat("aa").format(eventData.getEndTime());
            String endDayName = new SimpleDateFormat("EEEE").format(eventData.getEndTime());
            final String endYear = new SimpleDateFormat("yyyy").format(eventData.getEndTime());


            final String title = eventData.getTitle();
            String content = eventData.getContent();
            final String location = eventData.getLocation();

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


            Button calenderBtn = findViewById(R.id.Calender);
            calenderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, Constants.PERMISSIONS_REQUEST);
                    }

                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();


                    //Need to set AM_PM
                    Calendar beginTime = Calendar.getInstance(TimeZone.getTimeZone("Chicago"));
                    Calendar cal = Calendar.getInstance();
                    try {
                        Date date = new SimpleDateFormat("MMM").parse(month);
                        cal.setTime(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    beginTime.set(Integer.parseInt(year), cal.get(Calendar.MONTH), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minutes));

                    Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("Chicago"));
                    try {
                        Date date = new SimpleDateFormat("MMM").parse(endMonth);
                        cal.setTime(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    endTime.set(Integer.parseInt(endYear), cal.get(Calendar.MONTH), Integer.parseInt(endDay), Integer.parseInt(endHour), Integer.parseInt(endMinutes));

                    values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                    values.put(CalendarContract.Events.DESCRIPTION, title);
                    values.put(CalendarContract.Events.EVENT_LOCATION, location);

                    cr.insert(CalendarContract.Events.CONTENT_URI, values);
                }
            });


        }
    }
