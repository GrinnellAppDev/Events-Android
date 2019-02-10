package edu.grinnell.appdev.events.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import edu.grinnell.appdev.events.MainActivity
import edu.grinnell.appdev.events.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
