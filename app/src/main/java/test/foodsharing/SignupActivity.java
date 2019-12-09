package test.foodsharing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Activity that handles the creation of new accounts

public class SignupActivity extends AppCompatActivity {

    EditText nameField;
    EditText emailField;
    EditText schoolField;
    EditText passwordField;
    EditText verificationField;
    Button submitButton;
    TextView titleText;

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

        titleText.setText("Create Account");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    // handles account creation when Create button is clicked
    private void createAccount() {

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String verification = verificationField.getText().toString();
        String school = schoolField.getText().toString();

        if (password.equals(verification)) {
            if (email.length() > 0 && password.length() > 0) {
                if (!checkAccountExists(email)) {
                    saveToDatabase(name, email, password, verification, school);

                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Password and verification don't match!", Toast.LENGTH_SHORT).show();
        }
    }

    // check to see if an account has already been registered with the email
    private boolean checkAccountExists(String email) {
        DataSource ds = DataSource.getInstance();
        String pw = ds.getPassword(email);
        return pw != null;
    }

    // saves the new user to the database
    private void saveToDatabase(String name, String email, String password, String pwConfirm, String school) {
        DataSource ds = DataSource.getInstance();
        ds.createAccount(name, email, password, pwConfirm, school);
    }
}
