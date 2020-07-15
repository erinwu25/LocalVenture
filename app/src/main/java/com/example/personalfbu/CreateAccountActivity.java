package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
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

        //
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClick Create Account button");
                String username = etCreateUsername.getText().toString();
                String password = etCreatePassword.getText().toString();
                String email = etCreateEmail.getText().toString();
                createAccount(username, email, password);
            }
        });

    }

    private void createAccount(String username, String email, String password) {
        ParseUser user = new ParseUser();  // create new user

        // set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put("email", email);

        // invoke signup in background
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // yay no errors!

                }
                else {
                    // there was an error and we need to handle it

                }
            }
        });
        finish();

    }
}