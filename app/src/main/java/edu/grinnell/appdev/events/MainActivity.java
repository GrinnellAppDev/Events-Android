package edu.grinnell.appdev.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete{
    private String xmlData;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link to the XML file
        String link = "http://25livepub.collegenet.com/calendars/web-calendar.xml";

        //Downloading the XML through a separate thread
        new Downloader(this).execute(link);



    }

    /**
     *
     * @param result An XML string that is return by the Asynctask
     */
    @Override
    public void onDownloadComplete(String result) {
        xmlData = result;
        XMLPullParser parser = new XMLPullParser();
        if (xmlData != null) {
            try {
                parser.parse(xmlData);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        eventList = parser.getEventList();
        Log.d("Debug", eventList.get(0).getContent());
    }

    /**
     *
     * @param failMsg Asynctask returns this if the download failed
     */
    @Override
    public void onDownloadFail(String failMsg) {
        xmlData = failMsg;
    }
}
