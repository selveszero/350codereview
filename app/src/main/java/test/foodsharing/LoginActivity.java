package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_field);
        password = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }


    // handles login functionality
    private void login() {
        String emailText = email.getText().toString();
        String pwText = password.getText().toString();

        String correctPassword = getPassword(emailText);

        if (correctPassword != null) {
            if (correctPassword.equals(pwText)) {
                // successful login
                cacheUser(emailText);
                Toast.makeText(this, "Successful login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, EventsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Incorrect email/password combination", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Incorrect email/password combination", Toast.LENGTH_SHORT).show();
        }

    }

    // check if an email is already registered
    private String getPassword(String email) {
        DataSource ds = DataSource.getInstance();
        String pw = ds.getPassword(email);
        return pw;
    }

    // save user locally so they don't have to keep logging in
    private void cacheUser(String email) {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("current_user", email);
        editor.apply();
    }
}
