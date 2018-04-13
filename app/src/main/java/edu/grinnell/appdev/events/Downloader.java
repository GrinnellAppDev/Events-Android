package edu.grinnell.appdev.events;

import android.app.Activity;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static edu.grinnell.appdev.events.Constants.*;

/**
 * This class is responsible for downloading the XML from the web and
 * converting it into a string
 */
public class Downloader extends AsyncTask <String, Void, Integer>{

    private String xmlString;
    private OnDownloadComplete mOnDownloadComplete;

    Downloader(Activity activity) {
        this.mOnDownloadComplete = (OnDownloadComplete) activity;
    }

    /**
     *
     * @param strings An array of string, of which the first argument is the
     *                link to the website that hosts the XML data
     * @return Int An Integer that reports if there is an error parsing,
     *             or converting into an input stream, or if the data is
     *             null, or if the parsing was successful
     */
    @Override
    protected Integer doInBackground(String... strings) {
        URL url;

        //Extract the URL from the array
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ERROR_URL_PARSING;
        }
        URLConnection urlConnection;
        InputStream inputStream;

        //Establish a connection and start an input stream
        try {
            urlConnection = url.openConnection();
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_STREAM;
        }

        //Store string return by the helper method
        xmlString = getStringFromInputStream(inputStream);

        //Return an integer depending on success or failure
        if (xmlString == null) {
            return ERROR_NULL_STRING;
        }
        else {
            return SUCCESS;
        }
    }

    /**
     *
     * @param is Input Stream that is used by the buffer reader to build a buffer string
     * @return str A string that holds the XML data extracted from the given Input
     *             stream
     */
    private static String getStringFromInputStream(InputStream is) {
        //Initializations
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        //Read from the buffer reader and build the string using string builder
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();

            //Close the stream
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param result An integer that tells if the download was a success
     */
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == SUCCESS) {
            mOnDownloadComplete.onDownloadComplete(xmlString);
        }
        else {
            String failMsg = "Downloading of the XML failed";
            mOnDownloadComplete.onDownloadFail(failMsg);
        }
    }

}
