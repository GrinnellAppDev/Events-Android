package edu.grinnell.appdev.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static edu.grinnell.appdev.events.Constants.*;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete, onParseComplete{
    private String xmlData;
    public List<Event> eventList;
    public static ArrayList<Event> favoritesList;


    private FragmentHome homeFragment;
    private FragmentCalender searchFragment;
    private FragmentFavorites favoritesFragment;
    public static SharedPreferences shared;
    BottomNavigationView bottomNavigationView;

    public static final String FULL_LIST = "FULL_LIST";
    public static final String FAVORITES_LIST = "FAVORITES_LST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String json = sharedPrefs.getString(FULL_LIST, null); //Retrieve previously saved data

        if (json != null) {
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            eventList = gson.fromJson(json, type); //Restore previous data
            initializeFragments();
            addBundleArgs();
            Toast.makeText(this, "Data restored from local device", Toast.LENGTH_SHORT).show();
            setFragment(homeFragment);
        }
        else {
            //Downloading the XML through a separate thread
            downloadContent();
        }

        setUpMainActivityUI();

    }

    private void initializeFragments(){
        homeFragment = new FragmentHome();
        searchFragment = new FragmentCalender();
        favoritesFragment = new FragmentFavorites();

    }

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
                            case R.id.menu_calender:
                                setFragment(searchFragment);
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
    public void downloadContent(){
        new Downloader(this).execute(XML_STRING);
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
        storeEvents((ArrayList<Event>) eventList, this, FULL_LIST);
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
     * Stores the event as a file using shared preference
     * @param eventArrayList The list to be stored
     * @param context Context param to initialize shared preferene
     * @param filename Files are stored with filename used a keys
     */
    public static void storeEvents(ArrayList<Event> eventArrayList, Context context, String filename){
        shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = "";
        json = gson.toJson(eventArrayList); //Convert the array to json
        editor.putString(filename, json); //Put the variable in memory
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Respond to each item selcted on the menu bar
        if (id == R.id.action_refresh) {
            // Delete the current events list and download everything again
            deleteSharedPreferencesFile(FULL_LIST);
            this.recreate();
            return true;
        }
        else if(id == R.id.action_settings){
            Toast.makeText(this, "yet to be implemented", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.action_help){
            /*Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("text/email");
            email.putExtra(Intent.EXTRA_EMAIL, new String[] {"am.lamsal@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Help/Feedback");
            startActivity(Intent.createChooser(email, "Ask help/Send feedback"));*/
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * @param filename Files with filename as keys will be deleted locally
     */
    public void deleteSharedPreferencesFile(String filename){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(filename);
        editor.apply();
    }

    public void setUpMainActivityUI(){
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationViewInitialize(bottomNavigationView);

        Toolbar homeToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(homeToolBar);
    }


}
