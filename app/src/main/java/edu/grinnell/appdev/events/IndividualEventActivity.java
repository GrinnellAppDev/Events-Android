package edu.grinnell.appdev.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

public class IndividualEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_event);

        // For scroll: yourTextView.setMovementMethod(new ScrollingMovementMethod());


    }
}
