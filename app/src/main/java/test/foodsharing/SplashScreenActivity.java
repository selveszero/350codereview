package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

// Redirects to Login or Events depending on if a user is logged in

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        String email = sharedPref.getString("current_user", "");

        if (email.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        }


    }
}
