package com.example.personalfbu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnCreateAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // see if there is a current logged in user
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // find elements in view
        etUsername = findViewById(R.id.etCreateUsername);
        etPassword = findViewById(R.id.etCreatePassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // click listeners for login and create account
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }

        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to sign up activity
                Intent toSignup = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(toSignup);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Toast.makeText(LoginActivity.this, "Error with login. Please try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                // navigate to main activity if user has signed in properly
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, SplashScreen.class);
        startActivity(i);
        finish();
    }
}
