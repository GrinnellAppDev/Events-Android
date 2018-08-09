package edu.grinnell.appdev.events;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static edu.grinnell.appdev.events.Constants.XML_STRING;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete, onParseComplete{
    private String xmlData;
    public static List<Event> eventList;
    private FragmentHome homeFragment;
    private FragmentSearch searchFragment;
    private FragmentFavorites favoritesFragment;
    SharedPreferences shared;

    public final String FULL_LIST = "FULL_LIST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new FragmentHome();
        searchFragment = new FragmentSearch();
        favoritesFragment = new FragmentFavorites();

        //Link to the XML file
        String link = XML_STRING;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String json = sharedPrefs.getString(FULL_LIST, null); //Retrieve previously saved data


        if (json != null) {
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            eventList = gson.fromJson(json, type); //Restore previous data
            setFragment(homeFragment);
        }
        else {
            //Downloading the XML through a separate thread
            new Downloader(this).execute(link);

        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        //setFragment(homeFragment);
        bottomNavigationViewInitialize(bottomNavigationView);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());


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
        storeEvents();
    }


    /**
     *
     * @param failMsg Aynctask return this if the parsing fails
     */
    @Override
    public void onParseFail(String failMsg) {
        Toast.makeText(getApplicationContext(), failMsg, Toast.LENGTH_SHORT).show();
    }

    public void storeEvents(){
        shared = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();

        String json = gson.toJson(eventList); //Convert the array to json

        editor.putString(FULL_LIST, json); //Put the variable in memory
        editor.commit();
    }

}
