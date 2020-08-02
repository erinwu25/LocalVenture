package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
                if (username.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = etCreatePassword.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = etCreateEmail.getText().toString();
                if (email.isEmpty() || (isValidEmail(email) == false)) {
                    Toast.makeText(CreateAccountActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccount(username, email, password);
                Intent toLogin = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });

    }

    private void createAccount(String username, String email, String password) {
        // create new user
        final ParseUser user = new ParseUser();

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
                    Toast.makeText(CreateAccountActivity.this, "Issue creating account. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}