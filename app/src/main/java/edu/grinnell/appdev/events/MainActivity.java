package edu.grinnell.appdev.events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static edu.grinnell.appdev.events.Constants.XML_STRING;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete, onParseComplete{
    private String xmlData;
    public List<Event> eventList;
    public static ArrayList<Event> favoritesList;


    private FragmentHome homeFragment;
    private FragmentMap mapFragment;
    private FragmentFavorites favoritesFragment;
    BottomNavigationView bottomNavigationView;

    public static final String FULL_LIST = "FULL_LIST";
    public static final String FAVORITES_LIST = "FAVORITES_LST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoritesList = new ArrayList<>();
        fetchData(this);
        setUpMainActivityUI();

    }

    /**
     * Initialize the fragments
     */
    private void initializeFragments(){
        homeFragment = new FragmentHome();
        mapFragment = new FragmentMap();
        favoritesFragment = new FragmentFavorites();

    }

    /**
     *
     * @param filename Name of file to be written in internal storage
     * @param activity Activity being called from
     * @param fullList Boolean to distinguish between types of list
     * @return String: Data that is read from file
     */
    public static String readData(String filename, Activity activity, Boolean fullList){
        String yourFilePath = activity.getFilesDir() + "/" + filename;
        File yourFile = new File( yourFilePath );
        BufferedInputStream buf = null;
        String json = null;

        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        try {
            buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            json = new String(bytes);
        } catch (FileNotFoundException e) {
            if (fullList) {
                downloadContent(activity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;

    }

    /**
     * Fetches the data from file to initialize the recycler view
     * @param activity Activity being called from
     */
    private void fetchData(Activity activity){

        String json = readData(FULL_LIST, activity, true);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            eventList = gson.fromJson(json, type); //Restore previous data
            initializeFragments();
            addBundleArgs();
            Toast.makeText(this, "Data restored from local device", Toast.LENGTH_SHORT).show();
            setFragment(homeFragment);
        }
    }

    /**
     * Initialize the bottom navigation view. Also handles the switch between nav items
     * @param bottomNavigationView A bottomNavigationView object, in which a listener will be added
     */
    void bottomNavigationViewInitialize(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("CommitTransaction")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_home:
                                setFragment(homeFragment);
                                break;
                            case R.id.menu_map:
                                setFragment(mapFragment);
                                break;
                            case R.id.menu_favorites:
                                setFragment(favoritesFragment);
                                break;
                        }
                        return true;
                    }
                });
    }

    /**
     * Download the XML string
     */
    public static void downloadContent(Activity activity){
        new Downloader(activity).execute(XML_STRING);
    }

    /**
     *
     * @param fragment Particular fragment to switched to
     */
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     *
     * @param result An XML string that is return by the Asynctask
     */
    @Override
    public void onDownloadComplete(String result) {
        xmlData = result;
        if (xmlData != null) {
                new AsyncParser(this).execute(xmlData);
        }
    }

    /**
     *
     * @param failMsg Asynctask returns this if the download failed
     */
    @Override
    public void onDownloadFail(String failMsg) {
        xmlData = failMsg;
        Toast.makeText(getApplicationContext(), failMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param completeEventList Aynctask return this if the parsing is successful
     */
    @Override
    public void onParseComplete(List<Event> completeEventList) {
        eventList = completeEventList;
        initializeFragments();
        addBundleArgs();
        setFragment(homeFragment);
        Toast.makeText(this, "Downloaded the latest data", Toast.LENGTH_SHORT).show();
        writeToFile(this, FULL_LIST, (ArrayList<Event>) eventList);
    }


    /**
     *
     * @param failMsg Aynctask return this if the parsing fails
     */
    @Override
    public void onParseFail(String failMsg) {
        Toast.makeText(getApplicationContext(), failMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add event list to a bundle, which will be passed to a fragment
     */
    public void addBundleArgs (){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Event list", (ArrayList<? extends Parcelable>) eventList);
        homeFragment.setArguments(bundle);
    }


    /**
     *
     * @param context The context that the function is being called from
     * @param filename Name of file to be written
     * @param eventList ArrayList of events to be written to file
     */
    public static void writeToFile(Context context, String filename, ArrayList<Event> eventList){
        Gson gson = new Gson();
        String json = "";
        json = gson.toJson(eventList); //Convert the array to json string

        File file = new File(String.valueOf(context.getFilesDir()));
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, filename);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(json);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Respond to each item selcted on the menu bar
        if (id == R.id.action_refresh) {
            // Delete the current events list and download everything again
            deleteLocalFile(FULL_LIST);
            this.recreate();
            return true;
        }
        else if(id == R.id.action_settings){
            Toast.makeText(this, "yet to be implemented", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.action_help){
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * @param filename Files with filename will be deleted locally
     */
    public void deleteLocalFile(String filename){
        File file = new File(getFilesDir(), filename);
        file.delete();
    }

    /**
     * Set Up the UI for main Activity before the fragments are set up
     */
    public void setUpMainActivityUI(){
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationViewInitialize(bottomNavigationView);

        Toolbar homeToolBar = findViewById(R.id.my_toolbar);
        homeToolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(homeToolBar);
    }


}
