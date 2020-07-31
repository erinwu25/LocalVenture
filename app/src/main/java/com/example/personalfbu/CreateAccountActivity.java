package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String TAG = "CreateAccountActivity";
    EditText etCreateUsername;
    EditText etCreatePassword;
    EditText etCreateEmail;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // find elements in view
        etCreateUsername = findViewById(R.id.etCreateUsername);
        etCreatePassword = findViewById(R.id.etCreatePassword);
        etCreateEmail = findViewById(R.id.etCreateEmail);
        btnSignUp = findViewById(R.id.btnSignUp);

        // on click listener for signup
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClick Create Account button");
                String username = etCreateUsername.getText().toString();
                String password = etCreatePassword.getText().toString();
                String email = etCreateEmail.getText().toString();
                createAccount(username, email, password);
                Intent toEditProfile = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(toEditProfile);
            }
        });

    }

    private void createAccount(String username, String email, String password) {
        final ParseUser user = new ParseUser();  // create new user

        // set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put("emailAddress", email);
        user.setEmail(email);
        // invoke signup in background
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateAccountActivity.this, "Issue creating account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}