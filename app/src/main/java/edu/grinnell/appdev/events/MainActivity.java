package edu.grinnell.appdev.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static edu.grinnell.appdev.events.Constants.XML_STRING;


public class MainActivity extends AppCompatActivity implements OnDownloadComplete, onParseComplete{
    private String xmlData;
    public static List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link to the XML file
        String link = XML_STRING;

        //Downloading the XML through a separate thread
        new Downloader(this).execute(link);



        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationViewInitialize(bottomNavigationView);

    }

    void bottomNavigationViewInitialize(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_home:
                                break;
                            case R.id.menu_search:
                                break;
                            case R.id.menu_favorites:
                                break;
                        }
                        return true;
                    }
                });
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
        configureRecyclerView();
    }

    /**
     *
     * @param failMsg Aynctask return this if the parsing fails
     */
    @Override
    public void onParseFail(String failMsg) {
        Toast.makeText(getApplicationContext(), failMsg, Toast.LENGTH_SHORT).show();
    }

    public void configureRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter((ArrayList<Event>) eventList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
