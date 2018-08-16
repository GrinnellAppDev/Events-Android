package edu.grinnell.appdev.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete, onParseComplete{
    private String xmlData;
    public static List<Event> eventList;
    public static ArrayList<Event> favoritesList;

    private FragmentHome homeFragment;
    private FragmentSearch searchFragment;
    private FragmentFavorites favoritesFragment;
    public static SharedPreferences shared;

    public static final String FULL_LIST = "FULL_LIST";
    public static final String FAVORITES_LIST = "FAVORITES_LST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String json = sharedPrefs.getString(FULL_LIST, null); //Retrieve previously saved data

        if (json != null) {
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            eventList = gson.fromJson(json, type); //Restore previous data
            Toast.makeText(this, "Data restored from cache", Toast.LENGTH_SHORT).show();
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
        searchFragment = new FragmentSearch();
        favoritesFragment = new FragmentFavorites();

        Bundle home_frag_args = new Bundle();

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
                            case R.id.menu_search:
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

    public void downloadContent(){
        new Downloader(this).execute(Constants.XML_STRING);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        //Iffy solution (check again)
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
        setFragment(homeFragment);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }
        deleteSharedPreferencesFile(FULL_LIST);
        this.recreate();

        return super.onOptionsItemSelected(item);
    }

    public void deleteSharedPreferencesFile(String filename){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(filename);
        editor.apply();
    }

    public void setUpMainActivityUI(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationViewInitialize(bottomNavigationView);

        Toolbar homeToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(homeToolBar);
    }


}
