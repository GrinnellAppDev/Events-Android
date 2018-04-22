package edu.grinnell.appdev.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import edu.grinnell.appdev.events.adapter.EventRecyclerAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((EventApplication)getApplication()).openRealm();

        eventRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        eventRecyclerView.setLayoutManager(layoutManager);
        adapter = new EventRecyclerAdapter(this, ((EventApplication)getApplication()).getEventRealm());
        eventRecyclerView.setAdapter(adapter);

        RealmConfiguration config = new RealmConfiguration.Builder().
                deleteRealmIfMigrationNeeded().build();
        final Realm realm = Realm.getInstance(config);
    }
}
