package edu.grinnell.appdev.events;

import android.text.Html;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;


public class XMLPullParser {

    private static final int LENGTH_OF_XML_DECLARATION = 40;
    private List<Event> eventList;
    private String text;
    private Event event;

    public List<Event> getEventList() {
        return eventList;
    }

    public void parse(String result) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(result.substring(LENGTH_OF_XML_DECLARATION - 1)));
        eventList = new ArrayList<>();
        int eventType = xpp.getEventType();

        //Iterate through each event
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = xpp.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("title")) {
                        event = new Event();
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = xpp.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("entry")) {
                        eventList.add(event);
                    } else if (tagName.equalsIgnoreCase("title")) {
                        event.setTitle(text);
                    } else if (tagName.equalsIgnoreCase("content")) {
                        event.setContent(text);
                        parseContent(text);
                    }
                    break;

                default:
                    break;
            }
            eventType = xpp.next();
        }
    }

    private void parseContent(String content) {
        String arr[] = Html.fromHtml(content).toString().split("\n");
        String description = arr[3];
        if (!(arr.length < 6) && description.length() > 0) {
            event.setContent(description);

            String location = arr[0];
            event.setLocation(location);

            String date = arr[1];
            Log.d("debug", date);
            try {
                parseDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to insert ":00" when the given time does not include minutes
    public static String addMinutes(String input) {
        // Save the am/pm part of the string (last two digits)
        String ampm = input.substring(input.length() - 2, input.length());

        // Save the time part of the string (everything except the last two digits)
        String num = input.substring(0, input.length() - 2);

        // Concatenate all 3 pieces together, inserting the minutes between the given time and am/pm

        return num + ":00" + ampm;
    }

    public static ArrayList<String> standardizeTime(String unformattedTime, String dayOfWeek, String endDayOfWeek,
                                                    String month, String endMonth, String dayOfMonth, String endDayOfMonth, String year){
        // Create a tokenizer object that separates the test string using the given substring
        StringTokenizer token = new StringTokenizer(unformattedTime, " - ");

        // The "tokens" separated by the substring above that are extracted from the
        // given string will be stored in this list
        ArrayList<String> strings = new ArrayList<>();

        // Read all of the "tokens" and store them in the list
        while (token.hasMoreTokens()) {
            strings.add(token.nextToken());
        }
        // The first token (the start time)
        String startTime = strings.get(0);

        // The second token (the end time)
        String endTime = strings.get(1);

        // Check if the start time includes am/pm
        if (!startTime.contains("am") && !startTime.contains("pm")) {
            // If not, find out if the end time is am or pm and append that to the end of the start time
            startTime = startTime + endTime.charAt(endTime.length() - 2) + "m";
        }

        // Check if the start time includes minutes (by checking if it includes a ":")
        if (!startTime.contains(":")) {
            // If not, add minutes
            startTime = addMinutes(startTime);
        }

        // Check if end time includes minutes
        if (!endTime.contains(":")) {
            // If not, add minutes
            endTime = addMinutes(endTime);
        }

        // Save the modified strings in the list again
        strings.set(0, dayOfWeek + " " + month + " " + dayOfMonth + " " + year+ " " + startTime);
        strings.set(1, endDayOfWeek + " " + endMonth + " " + endDayOfMonth + " " + year+ " " + endTime);

        return strings;
    }

    private void parseDate(String date) throws ParseException {

        //Splitting the date string using for commas and spaces to extract all the important info
        String delimiters = "\\s*(\\s|,)\\s*";
        String dateArr[] = date.split(delimiters);


        //Check for valid events
        if (dateArr.length > 1) {

            //All the components start and end date and time components of an event
            String dayOfWeek = dateArr[0];
            String month = dateArr[1];
            String dayOfMonth = dateArr[2];
            String endMonth;
            String endDayofWeek;
            String endDayofMonth;
            String year;
            String unformattedStartStr;
            String unformattedEndStr;
            String unformattedTime;
            ArrayList<String> formattedDateTime;


            //Events that span within a day
            if (dateArr.length < 10) {

                year = dateArr[3];
                endDayofWeek = dayOfWeek;
                endMonth = month;
                endDayofMonth = dayOfMonth;
                unformattedStartStr = dateArr[4];
                unformattedEndStr = dateArr[6];

                //Time that needs to be standardized
                unformattedTime = unformattedStartStr + " " + "-" + " " + unformattedEndStr;

                //List that contains the standardized time
                formattedDateTime = standardizeTime(unformattedTime, dayOfWeek, endDayofWeek, month, endMonth, dayOfMonth, endDayofMonth,year);
            }
            else {
                //Saturday, April 21, 7am – Sunday, April 22, 2018, 1am
                year = dateArr[8];
                endDayofWeek = dateArr[5];
                endDayofMonth = dateArr[7];
                endMonth = dateArr[6];
                unformattedStartStr = dateArr[3];
                unformattedEndStr = dateArr[9];

                //Time that needs to be standardized
                unformattedTime = unformattedStartStr + " " + "-" + " " + unformattedEndStr;

                //List that contains the standardized time
                formattedDateTime = standardizeTime(unformattedTime, dayOfWeek, endDayofWeek, month, endMonth, dayOfMonth, endDayofMonth,year);
            }

            //Storing the start time and end time
            Date start = new SimpleDateFormat("EEEE MMMM dd yyyy hh:mma", Locale.ENGLISH).parse(formattedDateTime.get(0));
            Date end = new SimpleDateFormat("EEEE MMMM dd yyyy hh:mma", Locale.ENGLISH).parse(formattedDateTime.get(1));

            event.setStartTime(start);
            event.setEndTime(end);

            Log.d("start", start.toString());
            Log.d("end", end.toString());

        }
    }
}


