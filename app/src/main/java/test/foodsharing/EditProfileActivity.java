package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    EditText nameField;
    EditText emailField;
    EditText schoolField;
    EditText passwordField;
    EditText verificationField;
    Button submitButton;

    private TextView titleText;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.name_field);
        emailField = findViewById(R.id.email_field);
        schoolField = findViewById(R.id.school_field);
        passwordField = findViewById(R.id.password_field);
        verificationField = findViewById(R.id.verification_field);
        submitButton = findViewById(R.id.submit_button);

        titleText = findViewById(R.id.titleText);
        titleText.setText("Edit Profile");

        // get current user email
        email = getUser();
        emailField.setText(email);

        submitButton.setText("Update info");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAccount();
            }
        });

    }

    // functionality has not been connected to mongo yet - will allow user to edit profile details
    private void editAccount() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String verification = verificationField.getText().toString();

        if (password.equals(verification)) {
            if (email.length() > 0 && password.length() > 0) {
                SharedPreferences sharedPref = this.getSharedPreferences("accounts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(email, password);
                editor.apply();

                cacheUser(email);

                Toast.makeText(this, "Account info updated!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, EventsActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Password and verification don't match!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUser() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        return sharedPref.getString("current_user", "");
    }

    private void cacheUser(String email) {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("current_user", email);
        editor.apply();
    }
}
