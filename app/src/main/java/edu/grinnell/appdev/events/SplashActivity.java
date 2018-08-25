package edu.grinnell.appdev.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Start Main Activity after Splash screen is displayed
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
