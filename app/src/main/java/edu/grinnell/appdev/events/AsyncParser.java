package edu.grinnell.appdev.events;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

import static edu.grinnell.appdev.events.Constants.ERROR_PARSING;
import static edu.grinnell.appdev.events.Constants.FRESH_START;
import static edu.grinnell.appdev.events.Constants.LENGTH_OF_XML_DECLARATION;
import static edu.grinnell.appdev.events.Constants.SUCCESS;

/**
 * This class is responsible for parsing the XML string and creating a final events list
 */
public class AsyncParser extends AsyncTask<String, Void, Integer>{

    private List<Event> eventList;
    private String text;
    private Event event;
    private onParseComplete mOnParseComplete;
    private ProgressDialog progressDialog;
    private Activity activity;

    private String title;
    private String content;
    private String evlocation;
    private Long startTime;
    private Long endTime;
    private String subEmail;
    private String subName;
    private String organizer;
    private String id;
    private Date startTimeCurEvent;
    private Date startTimePrevEvent;

    AsyncParser (Activity activity) {
        this.mOnParseComplete = (onParseComplete) activity;
        this.activity = activity;
    }

    /**
     * Creates a dialog box indicating the user that the data is being downloaded
     */
    @Override
    protected void onPreExecute(){
        if (FRESH_START) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("App status");
            progressDialog.setMessage("Downloading data");
            progressDialog.show();
        }
    }

    //The method is supposed to parse the XML string that is passed as an argument
    @Override
    protected Integer doInBackground(String... strings) {
        XmlPullParserFactory factory;
        XmlPullParser xpp;
        int eventType;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(new StringReader(strings[0].substring(LENGTH_OF_XML_DECLARATION - 1)));
            eventList = new ArrayList<>();
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return ERROR_PARSING;
        }
        int count = 0;
        //Iterate through each event
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = xpp.getName();
            switch (eventType) {
                //Case where the parser finds core information about an event
                case XmlPullParser.TEXT:
                    text = xpp.getText();
                    break;
                //Case where the parser finds an event end tag
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("title")) {
                        title = text;
                    } else if (tagName.equalsIgnoreCase("id")) {
                        id = text;
                    } else if (tagName.equalsIgnoreCase("content")) {
                        parseContent(text);

                        //Adding row divided for each day in recycler view. Stored a an event object
                        // with only one field
                        if (startTimePrevEvent == null || startTimePrevEvent.before(startTimeCurEvent)){
                            if (startTimeCurEvent != null) {
                                Event pseudoEvent = new Event("", "", "",
                                        startTimeCurEvent.getTime(), (long) 0, "", "", "",
                                        "", 1);
                                eventList.add(pseudoEvent);
                                startTimePrevEvent = startTimeCurEvent;
                            }
                        }

                        //Populate the event and add it to the list
                        if (startTime != null) {
                            event = new Event(title, content, evlocation, startTime, endTime, subName,
                                    subEmail, organizer, id, 0);
                            eventList.add(event);
                        }
                    }
                    break;

                default:
                    break;
            }
            //Look for the next tag
            try {
                eventType = xpp.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return ERROR_PARSING;
            } catch (IOException e) {
                e.printStackTrace();
                return ERROR_PARSING;
            }
        }
        return SUCCESS;
    }

    /**
     *
     * @param content A string that will be converted to a parsable format
     */
    private Integer parseContent(String content) {

        //Add email
        Document doc= Jsoup.parse(content);
        Element email = doc.select("a").last();
        //subEmail = email.text();

        //Convert to XHTML into parsable format
        String arr[] = Html.fromHtml(content).toString().split("\n");
        for(int i = 0; i < arr.length; i++){
            String data = arr[i];
            if (data.contains("Submitter Name")) {
                subName = data;
            }
            else if (data.contains("Submitter Email")){
                subEmail = data;
            }
            else if (data.contains("Organizer")){
                organizer = data;
            }
        }

        //Description of the event
        String description = arr[3];

        //Check for any outlier since the XML is not standardized
        if (!(arr.length < 6) && description.length() > 0) {
            this.content = description;

            //Location of the event
            String location = arr[0];
            evlocation = location;

            //Start and end date for the events
            String date = arr[1];
            try {
                parseDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return ERROR_PARSING;
            }
        }
        return SUCCESS;
    }

    // Helper method to insert ":00" when the given time does not include minutes
    /**
     *
     * @param input Time that needs to be added minutes
     * @return Str Time represented in a string format
     */
    private static String addMinutes(String input) {
        // Save the am/pm part of the string (last two digits)
        String ampm = input.substring(input.length() - 2, input.length());

        // Save the time part of the string (everything except the last two digits)
        String num = input.substring(0, input.length() - 2);

        // Concatenate all 3 pieces together, inserting the minutes between the given time and am/pm
        return num + ":00" + ampm;
    }

    /**
     *
     * @param unformattedTime Time that needs to be standardized
     * @param dayOfWeek Day of the week that the event starts
     * @param endDayOfWeek Day of the week that the event ends
     * @param month The month that the event starts
     * @param endMonth The month that the event ends
     * @param dayOfMonth Day of the month that the event starts
     * @param endDayOfMonth Day of the month that the event ends
     * @param year The year that the event starts and ends
     * @return strings formatted start and end times
     */
    private static ArrayList<String> standardizeTime(String unformattedTime, String dayOfWeek,
                                                    String endDayOfWeek,String month,
                                                    String endMonth, String dayOfMonth,
                                                    String endDayOfMonth, String year){
        // Create a tokenizer object that separates the test string using the given substring
        StringTokenizer token = new StringTokenizer(unformattedTime, " - ");

        // The "tokens" separated by the substring above that are extracted from the
        // given string will be stored in this list
        ArrayList<String> formattedTimes = new ArrayList<>();

        // Read all of the "tokens" and store them in the list
        while (token.hasMoreTokens()) {
            formattedTimes.add(token.nextToken());
        }
        // The first token (the start time)
        String startTime = formattedTimes.get(0);

        // The second token (the end time)
        String endTime = formattedTimes.get(1);

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
        formattedTimes.set(0, dayOfWeek + " " + month + " " + dayOfMonth + " " + year+ " " + startTime);
        formattedTimes.set(1, endDayOfWeek + " " + endMonth + " " + endDayOfMonth + " " + year+ " " + endTime);

        return formattedTimes;
    }

    /**
     *
     * @param date Date that needs to be parsed
     * @throws ParseException
     */
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
                formattedDateTime = standardizeTime(unformattedTime, dayOfWeek, endDayofWeek, month,
                        endMonth, dayOfMonth, endDayofMonth,year);
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
                formattedDateTime = standardizeTime(unformattedTime, dayOfWeek, endDayofWeek, month,
                        endMonth, dayOfMonth, endDayofMonth,year);
            }

            //Storing the start time and end time
            Date start = new SimpleDateFormat("EEEE MMMM dd yyyy hh:mma", Locale.ENGLISH)
                    .parse(formattedDateTime.get(0));
            Date end = new SimpleDateFormat("EEEE MMMM dd yyyy hh:mma", Locale.ENGLISH)
                    .parse(formattedDateTime.get(1));

            //Ignore the time of the event
            startTimeCurEvent = new SimpleDateFormat("EEEE MMMM dd yyyy", Locale.US)
                    .parse(dayOfWeek + " " + month + " " + dayOfMonth + " " + year);

            //Store the date as Long for parcelable
            if (start != null) {
                startTime = start.getTime();
                endTime = end.getTime();
            }
        }
    }


    /**
     *
     * @param result An integer that tells if the download was a success
     */
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == SUCCESS) {
            mOnParseComplete.onParseComplete(eventList);
        }
        else {
            String failMsg = "Unable to load data";
            mOnParseComplete.onParseFail(failMsg);
        }
        if (FRESH_START) {
            progressDialog.dismiss();
            FRESH_START = false;
        }
    }
}


