package edu.grinnell.appdev.events;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EventApplication extends Application {

    private Realm eventRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        eventRealm = Realm.getInstance(config);
    }

    public void closeRealm() {
        eventRealm.close();
    }

    public Realm getEventRealm() {
        return eventRealm;
    }
}
