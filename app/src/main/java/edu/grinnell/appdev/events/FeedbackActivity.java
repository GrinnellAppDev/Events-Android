package edu.grinnell.appdev.events;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        IndividualEventActivity.setUpToolBar(this);
        configureView();
    }

    private void configureView() {
        final EditText feedback = findViewById(R.id.etFeedbackContent);
        Button send = findViewById(R.id.Feedback_btn);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String fbk = feedback.getText().toString();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/email");
                email.putExtra(Intent.EXTRA_EMAIL, new String[] {"am.lamsal@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Help/Feedback");
                email.putExtra(Intent.EXTRA_TEXT, fbk);
                startActivity(Intent.createChooser(email, "Ask help/Send feedback"));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
