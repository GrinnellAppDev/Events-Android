package edu.grinnell.appdev.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration config = new RealmConfiguration.Builder().
                deleteRealmIfMigrationNeeded().build();
        final Realm realm = Realm.getInstance(config);
    }
}
