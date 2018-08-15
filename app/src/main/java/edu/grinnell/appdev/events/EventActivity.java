package edu.grinnell.appdev.events;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.CalendarContract.Events;

public class EventActivity extends AppCompatActivity{

    private Event eventData;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Get the view from new_activity.xml
            setContentView(R.layout.activity_individual_event);
            Intent mIntent = getIntent();
            int eventNo = mIntent.getIntExtra("eventNo", 0);

            eventData = MainActivity.eventList.get(eventNo);

            setView();

        }


    /**
     *
     * @return ret : A list that contains the info about the start time of the event
     */
    private ArrayList<String> getEventInfo(){
            ArrayList<String> ret = new ArrayList<>();
            final String month = new SimpleDateFormat("MMM", Locale.US).format(eventData.getStartTime());
            final String day = new SimpleDateFormat("dd", Locale.US).format(eventData.getStartTime());
            final String hour = new SimpleDateFormat("hh", Locale.US).format(eventData.getStartTime());
            final String minutes = new SimpleDateFormat("mm", Locale.US).format(eventData.getStartTime());
            final String year = new SimpleDateFormat("yyyy", Locale.US).format(eventData.getStartTime());

            ret.add(year);
            ret.add(month);
            ret.add(day);
            ret.add(hour);
            ret.add(minutes);

            return ret;
        }

    /**
     * Set up the view for the expanded event
     */
    private void setView() {
            //Begin time
            ArrayList<String> eventInfo = getEventInfo();


            // Parse the list to get specific elements of the start time
            String month = eventInfo.get(1);
            String day = eventInfo.get(2);
            String hour = eventInfo.get(3);
            String minutes = eventInfo.get(4);
            String ampm = new SimpleDateFormat("aa", Locale.US).format(eventData.getStartTime());
            String dayName = new SimpleDateFormat("EEEE", Locale.US).format(eventData.getStartTime());

            // Get it to display the right format
            String startTime = hour + ":" + minutes + " " + ampm;


            final String title = eventData.getTitle();
            final String content = eventData.getContent();
            final String location = eventData.getLocation();
            final String email = eventData.getEmail();

            // Add elements to the text view
            TextView tvMonthText = findViewById(R.id.tvMonthText);
            TextView tvDayText = findViewById(R.id.tvDayText);
            TextView tvEventName = findViewById(R.id.tvEventName);
            TextView tvEventTime = findViewById(R.id.tvTime);
            TextView tvEventLocation = findViewById(R.id.tvLocation);
            TextView tvContent = findViewById(R.id.Content);
            TextView tvEmail = findViewById(R.id.tvEmail);

            tvMonthText.setText(month);
            tvDayText.setText(day);
            tvEventName.setText(title);
            tvEventTime.setText(dayName + ", " + day + " " + month + " at " + startTime);
            tvEventLocation.setText(location);
            tvContent.setText(content);
            tvEmail.setText(email);

            //Button responsible for adding events to the calender
            Button calenderBtn = findViewById(R.id.Calender);
            calenderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Ask for permission from the user to access the calender app
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EventActivity.this,
                                    new String[]{Manifest.permission.WRITE_CALENDAR},
                                    Constants.PERMISSIONS_REQUEST);
                    }
                    // Permission has already been granted previously
                    else {
                        insertEvent();
                    }

                }
            });


        }

    /**
     * Add the event to the calender
     */
    @SuppressLint("MissingPermission")
    private void insertEvent() {

        //Begin time
        ArrayList<String> eventInfo = getEventInfo();
        String year = eventInfo.get(0);
        String month = eventInfo.get(1);
        String day = eventInfo.get(2);
        String hour = eventInfo.get(3);
        String minutes = eventInfo.get(4);

        //End time
        final String endMonth = new SimpleDateFormat("MMM", Locale.US).format(eventData.getEndTime());
        final String endDay = new SimpleDateFormat("dd", Locale.US).format(eventData.getEndTime());
        final String endHour = new SimpleDateFormat("hh", Locale.US).format(eventData.getEndTime());
        final String endMinutes = new SimpleDateFormat("mm", Locale.US).format(eventData.getEndTime());
        final String endYear = new SimpleDateFormat("yyyy", Locale.US).format(eventData.getEndTime());
        final String title = eventData.getTitle();
        final String description = eventData.getContent();

        // Initialze resolver responsible to insert content in a calender
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();


        //Need to set AM_PM
        // Use Async query handler
        Calendar beginTime = Calendar.getInstance(TimeZone.getTimeZone("Chicago"));
        Calendar cal = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("MMM", Locale.US).parse(month);
            cal.setTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        beginTime.set(Integer.parseInt(year), cal.get(Calendar.MONTH), Integer.parseInt(day),
                Integer.parseInt(hour), Integer.parseInt(minutes));

        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("Chicago"));
        try {
            Date date = new SimpleDateFormat("MMM").parse(endMonth);
            cal.setTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        endTime.set(Integer.parseInt(endYear), cal.get(Calendar.MONTH), Integer.parseInt(endDay),
                Integer.parseInt(endHour), Integer.parseInt(endMinutes));

        //Need to change from default value
        values.put(Events.CALENDAR_ID, 3);
        values.put(Events.TITLE, title);
        values.put(Events.DTSTART, beginTime.getTimeInMillis());
        values.put(Events.DTEND, endTime.getTimeInMillis());
        values.put(Events.DESCRIPTION, description);
        values.put(Events.EVENT_LOCATION, eventData.getLocation());
        values.put(Events.EVENT_TIMEZONE, String.valueOf(TimeZone.getTimeZone("Chicago")));

        //Add it to the calender
        cr.insert(Events.CONTENT_URI, values);
        Toast.makeText(getApplicationContext(), "Event inserted!", Toast.LENGTH_SHORT).show();

    }

    /**
     *
     * @param requestCode Integer returned by the permission handler for this specific permission
     * @param permissions
     * @param grantResults Returns the result according to the user's decision
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  Constants.PERMISSIONS_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    insertEvent();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_LONG).show();
                }
            }

        }

    }
